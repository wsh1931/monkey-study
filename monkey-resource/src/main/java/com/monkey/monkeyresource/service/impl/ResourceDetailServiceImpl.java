package com.monkey.monkeyresource.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.remote.response.ErrorResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.FormTypeEnum;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;
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
