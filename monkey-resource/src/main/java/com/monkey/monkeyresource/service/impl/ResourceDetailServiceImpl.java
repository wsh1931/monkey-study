package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.remote.response.ErrorResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyresource.constant.FileTypeEnum;
import com.monkey.monkeyresource.constant.ResourcesEnum;
import com.monkey.monkeyresource.constant.TipConstant;
import com.monkey.monkeyresource.mapper.*;
import com.monkey.monkeyresource.pojo.*;
import com.monkey.monkeyresource.pojo.vo.FileVo;
import com.monkey.monkeyresource.pojo.vo.ResourcesVo;
import com.monkey.monkeyresource.service.ResourceDetailService;
import com.monkey.spring_security.JwtUtil;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

import static com.monkey.monkeyUtils.util.DateSelfUtils.judgeNowTimeAndAssignment;

/**
 * @author: wusihao
 * @date: 2023/10/16 8:59
 * @version: 1.0
 * @description:
 */
@Service
public class ResourceDetailServiceImpl implements ResourceDetailService {
    @Resource
    private ResourcesMapper resourcesMapper;
    @Resource
    private ResourceClassificationMapper resourceClassificationMapper;
    @Resource
    private ResourceConnectMapper resourceConnectMapper;
    @Resource
    private ResourceChargeMapper resourceChargeMapper;
    @Resource
    private ResourceBuyMapper resourceBuyMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ResourceScoreMapper resourceScoreMapper;
    /**
     * 查询资源标签列表
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/16 9:12
     */
    @Override
    public R queryResourceInfo(Long resourceId) {
        // 最终返回集合
        ResourcesVo resourcesVo = new ResourcesVo();
        Resources resources = resourcesMapper.selectById(resourceId);
        resources.setUrl(null);

        // 得到资源标签
        String []resourceLabel = resources.getResourceLabel().split(",");
        BeanUtils.copyProperties(resources, resourcesVo);
        List<String> resourceLabelList = new ArrayList<>(Arrays.asList(resourceLabel));

        resourcesVo.setResourceLabel(resourceLabelList);

        // 得到资源关系类型
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);

        Long resourceClassificationId = resourceConnect.getResourceClassificationId();
        String type = resourceConnect.getType();
        Long formTypeId = resourceConnect.getFormTypeId();
        resourcesVo.setType(type);
        resourcesVo.setFormTypeId(formTypeId);
        // 得到二级分类名称
        ResourceClassification resourceClassification = resourceClassificationMapper.selectById(resourceClassificationId);
        resourcesVo.setResourceClassificationName(resourceClassification.getName());

        // 得到资源作者信息
        Long voUserId = resourcesVo.getUserId();
        User selectById = userMapper.selectById(voUserId);
        resourcesVo.setUsername(selectById.getUsername());
        resourcesVo.setHeadImg(selectById.getPhoto());

        // 判断用户是否有资格下载此资源
        int isAuthorization = ResourcesEnum.IS_AUTHORIZATION.getCode();
        // 判断资源是否收费
        if (FormTypeEnum.FORM_TYPE_TOLL.getCode().equals(formTypeId)) {
            // 说明是收费资源，查找资源价格
            QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
            resourceChargeQueryWrapper.eq("resource_id", resourceId);
            ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeQueryWrapper);
            // 判断资源是否打折
            Integer isDiscount = resourceCharge.getIsDiscount();
            if (ResourcesEnum.IS_DISCOUNT.getCode().equals(isDiscount)) {
                Float discount = resourceCharge.getDiscount();
                Float price = resourceCharge.getPrice();
                Float resourcePrice = discount * price;
                String format = String.format("%.2f", resourcePrice);
                resourcesVo.setPrice(format);
                resourcesVo.setOriginPrice(String.format("%.2f", price));
            } else {
                resourcesVo.setPrice(String.valueOf(resourceCharge.getPrice()));
            }

            // 判断当前登录用户是否购买此资源
            String userId = JwtUtil.getUserId();
            if ("".equals(userId)) {
                isAuthorization = ResourcesEnum.NOT_AUTHORIZATION.getCode();
            } else {
                QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
                resourceBuyQueryWrapper.eq("user_id", userId);
                resourceBuyQueryWrapper.eq("resource_id", resourceId);
                Long selectCount = resourceBuyMapper.selectCount(resourceBuyQueryWrapper);
                if (selectCount <= 0) {
                    isAuthorization = ResourcesEnum.NOT_AUTHORIZATION.getCode();
                }
            }
        } else if (FormTypeEnum.FORM_TYPE_VIP.getCode().equals(formTypeId)) {
            // 判断用户是否为会员
            String userId = JwtUtil.getUserId();
            if ("".equals(userId)) {
                isAuthorization = ResourcesEnum.NOT_AUTHORIZATION.getCode();
            } else {
                User user = userMapper.selectById(userId);
                Date vipExpirationTime = user.getVipExpirationTime();
                // 判断用户vip是否过期
                if (!judgeNowTimeAndAssignment(vipExpirationTime)) {
                    // 说明用户vip已过期, 记录已过期用户vip集合
                    isAuthorization = ResourcesEnum.NOT_AUTHORIZATION.getCode();
                }
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("isAuthorization", isAuthorization);
        jsonObject.put("resourcesVo", resourcesVo);
        return R.ok(jsonObject);
    }

    /**
     * 下载文件资源
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/17 8:16
     */
    @Override
    public void downFileResource(HttpServletResponse response, HttpServletRequest request, Long resourceId) {
        try {
            // 判断用户是否有购买的权限
            boolean success = judgeUserIsAuthorizationDownResource(resourceId);
            if (!success) {
                throw new MonkeyBlogException(R.Error, TipConstant.noAuthorizatonDownResource);
            }

            Resources resources = resourcesMapper.selectById(resourceId);
            String filename = resources.getName();
            String url = resources.getUrl();
            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            String encodedFilename = URLEncoder.encode(filename, "UTF-8");
            URL urlStr = new URL(url);
            InputStream fileInputStream = urlStr.openStream();

            ServletContext servletContext = request.getServletContext();
            String mimeType = servletContext.getMimeType(filename);
            response.setHeader("content-type", mimeType);
            response.setHeader("content-disposition", "attachment;filename=" + encodedFilename);
            ServletOutputStream servletOutputStream = response.getOutputStream();
            byte[] buff = new byte[1024 * 1024];
            int len = 0;
            while ((len = fileInputStream.read(buff)) != -1) {
                servletOutputStream.write(buff, 0, len);
            }

            fileInputStream.close();
            servletOutputStream.close();
        } catch (Exception e) {
            throw new MonkeyBlogException(R.Error, e.getMessage());
        }
    }

    /**
     * 查询资源评价信息
     *
     * @param resourceId 资源id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 8:50
     */
    @Override
    public R queryResourceEvaluateInfo(Long resourceId) {
        QueryWrapper<ResourceScore> resourceScoreQueryWrapper = new QueryWrapper<>();
        resourceScoreQueryWrapper.eq("resource_id", resourceId);
        List<ResourceScore> resourceScoreList = resourceScoreMapper.selectList(resourceScoreQueryWrapper);
        int oneScore = 0;
        int twoScore = 0;
        int threeScore = 0;
        int fourScore = 0;
        int fiveScore = 0;
        int sumScore = 0;
        int userCount = 0;
        String scoreCount = "0";
        if (resourceScoreList == null || resourceScoreList.size() == 0) {
            JSONObject data = new JSONObject();
            data.put("oneScore", oneScore);
            data.put("twoScore", twoScore);
            data.put("threeScore", threeScore);
            data.put("fourScore", fourScore);
            data.put("fiveScore", fiveScore);
            data.put("scoreCount", scoreCount);
            data.put("userCount", userCount);
            return R.ok(data);
        }
        userCount = resourceScoreList.size();

        for (ResourceScore resourceScore : resourceScoreList) {
            Integer score = resourceScore.getScore();
            sumScore += score;
            switch (score) {
                case 1:
                    oneScore ++ ;
                    break;
                case 2:
                    twoScore ++ ;
                    break;
                case 3:
                    threeScore ++ ;
                    break;
                case 4:
                    fourScore ++ ;
                    break;
                case 5:
                    fiveScore ++ ;
            }
        }

        scoreCount = String.format("%.1f", (double)sumScore / userCount);
        JSONObject data = new JSONObject();
        data.put("oneScore", (int)((double)oneScore / userCount * 100));
        data.put("twoScore", (int)((double)twoScore / userCount * 100));
        data.put("threeScore", (int)((double)threeScore / userCount * 100));
        data.put("fourScore", (int)((double)fourScore / userCount * 100));
        data.put("fiveScore", (int)((double)fiveScore / userCount * 100));
        data.put("scoreCount", scoreCount);
        data.put("userCount", userCount);
        return R.ok(data);
    }

    /**
     * 查询相关资源列表
     *
     * @param resourceId
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/18 9:30
     */
    @Override
    public R resourceEvaluateInfo(Long resourceId) {
        // 得到资源分类id
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);
        Long resourceClassificationId = resourceConnect.getResourceClassificationId();
        // 查询与该资源分类标签相同的资源
        QueryWrapper<ResourceConnect> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("resource_classification_id", resourceClassificationId);
        queryWrapper.ne("resource_id", resourceId);
        queryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        queryWrapper.last("limit 10");
        queryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
        List<ResourceConnect> resourceConnectList = resourceConnectMapper.selectList(queryWrapper);
        if (resourceConnectList != null && resourceConnectList.size() > 0) {
            List<Long> resourceIdList = new ArrayList<>();
            Map<Long, ResourceConnect> hash = new HashMap<>();
            for (ResourceConnect connect : resourceConnectList) {
                Long resourceId1 = connect.getResourceId();
                hash.put(resourceId1, connect);
                resourceIdList.add(resourceId1);
            }

            // 最终返回集合
            List<ResourcesVo> resourcesVoList = new ArrayList<>();
                QueryWrapper<Resources> resourcesQueryWrapper = new QueryWrapper<>();
                resourcesQueryWrapper.in("id", resourceIdList);
                resourcesQueryWrapper.eq("status", ResourcesEnum.SUCCESS.getCode());
                List<Resources> resourcesList = resourcesMapper.selectList(resourcesQueryWrapper);
                for (Resources resources : resourcesList) {
                    ResourcesVo resourcesVo = new ResourcesVo();
                    BeanUtils.copyProperties(resources, resourcesVo);
                    Long resourcesId = resources.getId();
                    ResourceConnect connect = hash.get(resourcesId);
                    resourcesVo.setFormTypeId(connect.getFormTypeId());

                    Long userId = resources.getUserId();
                    User user = userMapper.selectById(userId);
                    resourcesVo.setUsername(user.getUsername());
                    resourcesVo.setHeadImg(user.getPhoto());

                    resourcesVo.setTypeUrl(FileTypeEnum.getFileUrlByFileType(resourcesVo.getType()).getUrl());
                    Long formTypeId = connect.getFormTypeId();
                    resourcesVo.setFormTypeId(formTypeId);
                    // 判断课程是否收费
                    if (formTypeId.equals(FormTypeEnum.FORM_TYPE_TOLL.getCode())) {
                        // 得到当前课程价格
                        QueryWrapper<ResourceCharge> resourceChargeQueryWrapper = new QueryWrapper<>();
                        resourceChargeQueryWrapper.eq("resource_id", resourcesId);
                        ResourceCharge resourceCharge = resourceChargeMapper.selectOne(resourceChargeQueryWrapper);
                        Integer isDiscount = resourceCharge.getIsDiscount();
                        if (isDiscount.equals(ResourcesEnum.IS_DISCOUNT.getCode())) {
                            resourcesVo.setPrice(String.format("%.1f", resourceCharge.getPrice() * resourceCharge.getDiscount()));
                        } else {
                            resourcesVo.setPrice(String.format("%.1f", resourceCharge.getPrice()));
                        }
                    }

                    resourcesVoList.add(resourcesVo);
                }

                return R.ok(resourcesVoList);
        }

        return R.ok();
    }

    /**
     * 判断当前登录用户是否有资格下载资源
     *
     * @param resourceId 资源 id
     * @return {@link null}
     * @author wusihao
     * @date 2023/10/17 8:17
     */
    private boolean judgeUserIsAuthorizationDownResource(Long resourceId) {
        // 得到资源关系类型
        QueryWrapper<ResourceConnect> resourceConnectQueryWrapper = new QueryWrapper<>();
        resourceConnectQueryWrapper.eq("resource_id", resourceId);
        resourceConnectQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        ResourceConnect resourceConnect = resourceConnectMapper.selectOne(resourceConnectQueryWrapper);
        Long formTypeId = resourceConnect.getFormTypeId();
        // 判断资源是否收费
        if (FormTypeEnum.FORM_TYPE_TOLL.getCode().equals(formTypeId)) {
            // 判断当前登录用户是否购买此资源
            String userId = JwtUtil.getUserId();
            if ("".equals(userId)) {
                return false;
            } else {
                QueryWrapper<ResourceBuy> resourceBuyQueryWrapper = new QueryWrapper<>();
                resourceBuyQueryWrapper.eq("user_id", userId);
                resourceBuyQueryWrapper.eq("resource_id", resourceId);
                Long selectCount = resourceBuyMapper.selectCount(resourceBuyQueryWrapper);
                if (selectCount <= 0) {
                    return false;
                }
            }
        } else if (FormTypeEnum.FORM_TYPE_VIP.getCode().equals(formTypeId)) {
            // 判断用户是否为会员
            String userId = JwtUtil.getUserId();
            if ("".equals(userId)) {
                return false;
            } else {
                User user = userMapper.selectById(userId);
                Date vipExpirationTime = user.getVipExpirationTime();
                // 判断用户vip是否过期
                if (!judgeNowTimeAndAssignment(vipExpirationTime)) {
                    // 说明用户vip已过期, 记录已过期用户vip集合
                    return false;
                }
            }
        }

        return true;
    }
}
