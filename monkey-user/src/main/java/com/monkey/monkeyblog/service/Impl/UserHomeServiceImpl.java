package com.monkey.monkeyblog.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.mapper.CollectContentConnectMapper;
import com.monkey.monkeyUtils.pojo.CollectContentConnect;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyblog.feign.UserToArticleFeignService;
import com.monkey.monkeyblog.feign.UserToQuestionFeignService;
import com.monkey.monkeyblog.mapper.UserFansMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyblog.pojo.UserFans;
import com.monkey.monkeyUtils.pojo.vo.UserVo;
import com.monkey.monkeyblog.mapper.RecentVisitUserhomeMapper;
import com.monkey.monkeyblog.pojo.Vo.*;
import com.monkey.monkeyblog.pojo.RecentVisitUserhome;
import com.monkey.monkeyblog.rabbitmq.EventConstant;
import com.monkey.monkeyblog.rabbitmq.RabbitmqExchangeName;
import com.monkey.monkeyblog.rabbitmq.RabbitmqRoutingName;
import com.monkey.monkeyblog.service.UserHomeService;

import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class UserHomeServiceImpl implements UserHomeService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserFansMapper userFansMapper;
    @Resource
    private RecentVisitUserhomeMapper recentVisitUserhomeMapper;

    @Resource
    private LabelMapper labelMapper;
    @Resource
    private CollectContentConnectMapper collectContentConnectMapper;

    @Resource
    private UserToArticleFeignService userToArticleFeignService;

    @Resource
    private UserToQuestionFeignService userToQuestionFeignService;
    @Resource
    private RabbitTemplate rabbitTemplate;


    // 通过用户id查询用户信息Vo
    @Override
    public ResultVO getUserInformationByUserId(Long userId, String nowUserId1) {
        UserVo userVo = new UserVo();
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(user, userVo);
        // 得到用户全部收藏数
        QueryWrapper<CollectContentConnect> collectContentConnectQueryWrapper = new QueryWrapper<>();
        collectContentConnectQueryWrapper.eq("user_id", userId);
        userVo.setCollect(collectContentConnectMapper.selectCount(collectContentConnectQueryWrapper));
        // 获得用户总的点赞数, 收藏数，评论数
        // 找到用户发表的文章数
        R resultArticle = userToArticleFeignService.getUserArticleCountByUserId(userId);
        if (resultArticle.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultArticle.getCode(), resultArticle.getMsg());
        }

        List<ArticleVo> articleVoList = (List<ArticleVo>)resultArticle.getData(new TypeReference<List<ArticleVo>>(){});
        userVo.setArticleSum((long)articleVoList.size());
        Long userLikes = 0L;
        Long userCollects = 0L;
        Long userComment = 0L;
        Long visits = 0L;
        // 获得用户所有文章的点赞数, 收藏数，评论数, 文章游览数
        for (ArticleVo articleVo : articleVoList) {
            visits += articleVo.getViewCount();
            userLikes += articleVo.getLikeCount();

            // 得到文章收藏数
            userCollects += articleVo.getCollectCount();

            userComment += articleVo.getCommentCount();
        }

        // 得到用户问答,点赞数, 收藏数, 游览数
        R resultQuestion = userToQuestionFeignService.getQuestionListByQuestionId(userId);
        if (resultQuestion.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultQuestion.getCode(), resultQuestion.getMsg());
        }
        List<QuestionVo> questionVoList = (List<QuestionVo>) resultQuestion.getData(new TypeReference<List<QuestionVo>>(){});
        for (QuestionVo questionVo : questionVoList) {
            visits += questionVo.getViewCount();
            // 得到问答收藏数
            userCollects += questionVo.getCollectCount();

            // 得到问答点赞数
            userLikes += questionVo.getLikeCount();

            userComment += questionVo.getReplyCount();
        }

        userVo.setLikeSum(userLikes);
        userVo.setUserCollect(userCollects);
        userVo.setCommentSum(userComment);
        userVo.setVisit(visits);

        // 得到用户粉丝数和关注数
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", userId);
        userVo.setFans(userFansMapper.selectCount(userFansQueryWrapper));

        QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
        userFansQueryWrapper1.eq("fans_id", userId);
        userVo.setConcern(userFansMapper.selectCount(userFansQueryWrapper1));
        // 判断当前登录用户与作者是否是粉丝
        if (nowUserId1 != null && !"".equals(nowUserId1)) {
            long nowUserId = Long.parseLong(nowUserId1);
            QueryWrapper<UserFans> fansQueryWrapper = new QueryWrapper<>();
            fansQueryWrapper.eq("user_id", userId);
            fansQueryWrapper.eq("fans_id", nowUserId);
            Long aLong = userFansMapper.selectCount(fansQueryWrapper);
            userVo.setIsFans(aLong);
        } else {
            userVo.setIsFans(0L);
        }


        // 得到用户提问数
        R resultUserQuestionCount = userToQuestionFeignService.getUserQuestionCountByUserId(userId);
        if (resultUserQuestionCount.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultUserQuestionCount.getCode(), resultUserQuestionCount.getMsg());
        }

        Long questionSum = (Long) resultUserQuestionCount.getData(new TypeReference<Long>(){});
        userVo.setQuestionSum(questionSum);

        return new ResultVO(ResultStatus.OK, null, userVo);

    }

    // 将访问者信息加入用户游览信息列表
    @Override
    public ResultVO recentlyView(Long userId, Long reviewId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("event", EventConstant.insertUserRecentlyView);
        jsonObject.put("userId", userId);
        jsonObject.put("reviewId", reviewId);
        Message message = new Message(jsonObject.toJSONString().getBytes());
        rabbitTemplate.convertAndSend(RabbitmqExchangeName.userInsertDirectExchange,
                RabbitmqRoutingName.userInsertRouting, message);
        if (userId.equals(reviewId)) {
            return new ResultVO(ResultStatus.OK, null, null);
        }

        return new ResultVO(ResultStatus.OK, null, null);
    }

    // 通过用户id得到最近来访用户信息
    @Override
    public ResultVO getRecentlyUserInfoByUserId(Long userId) {
        QueryWrapper<RecentVisitUserhome> recentVisitUserhomeQueryWrapper = new QueryWrapper<>();
        recentVisitUserhomeQueryWrapper.eq("be_visit_id", userId);
        recentVisitUserhomeQueryWrapper.orderByDesc("create_time");
        List<RecentVisitUserhome> recentVisitUserhomeList = recentVisitUserhomeMapper.selectList(recentVisitUserhomeQueryWrapper);
        // 去重，每次只展示同一用户的最新访问时间
        List<RecentVisitUserhomeVo> recentVisitUserhomeVoList = new ArrayList<>();
        Set<Long> set = new HashSet<>();
        for (RecentVisitUserhome recentVisitUserhome : recentVisitUserhomeList) {
            Long visitId = recentVisitUserhome.getVisitId();
            if (set.contains(visitId)) {
                continue;
            } else {
                set.add(visitId);
            }
            RecentVisitUserhomeVo recentVisitUserhomeVo = new RecentVisitUserhomeVo();
            recentVisitUserhomeVo.setVisitTime(recentVisitUserhome.getCreateTime());
            User user = userMapper.selectById(visitId);
            recentVisitUserhomeVo.setVisitUserPhoto(user.getPhoto());
            recentVisitUserhomeVo.setVisitUsername(user.getUsername());
            recentVisitUserhomeVo.setVisitId(visitId);
            recentVisitUserhomeVoList.add(recentVisitUserhomeVo);
        }
        return new ResultVO(ResultStatus.OK, null, recentVisitUserhomeVoList);
    }

    // 通过用户id得到用户所发表的所有文章分类数
    @Override
    public ResultVO getUserArticleClassficationCountByuserId(Long userId) {
        List<LabelVo> labelVoList = new ArrayList<>();
        // 找到用户发表的文章数
        R resultArticle = userToArticleFeignService.getUserArticleCountByUserId(userId);
        if (resultArticle.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(resultArticle.getCode(), resultArticle.getMsg());
        }

        List<ArticleVo> articleVoList = (List<ArticleVo>)resultArticle.getData(new TypeReference<List<ArticleVo>>(){});

        for (ArticleVo articleVo : articleVoList) {
            Long articleId = articleVo.getId();
            R articleLabelListByarticleId = userToArticleFeignService.getArticleLabelListByarticleId(articleId);
            if (articleLabelListByarticleId.getCode() != R.SUCCESS) {
                throw new MonkeyBlogException(articleLabelListByarticleId.getCode(), articleLabelListByarticleId.getMsg());
            }

            List<ArticleLabelVo> articleLabelVoList = (List<ArticleLabelVo>) articleLabelListByarticleId.getData(new TypeReference<List<ArticleLabelVo>>(){});
            for (ArticleLabelVo articleLabelVo : articleLabelVoList) {
                Long labelId = articleLabelVo.getLabelId();
                Label label = labelMapper.selectById(labelId);
                String labelName = label.getLabelName();
                Boolean is_choice = false;
                for (LabelVo labelVo : labelVoList) {
                    String name = labelVo.getLabelName();
                    if (name.equals(labelName)) {
                        is_choice = true;
                        labelVo.setArticleCount(labelVo.getArticleCount() + 1);
                    }
                }

                if (!is_choice) {
                    LabelVo labelVo = new LabelVo();
                    labelVo.setLabelName(labelName);
                    labelVo.setArticleCount(1L);
                    labelVo.setId(label.getId());
                    labelVoList.add(labelVo);
                }
            }
        }
        return new ResultVO(ResultStatus.OK, null, labelVoList);
    }

    @Override
    public R getArticleListByUserId(Long currentPage, Long pageSize, Long labelId, String userId) {
        R articleListByUserId = userToArticleFeignService.getArticleListByUserId(currentPage, pageSize, labelId, userId);
        if (articleListByUserId.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(articleListByUserId.getCode(), articleListByUserId.getMsg());
        }

        Page selectPage = (Page) articleListByUserId.getData(new TypeReference<Page>(){});
        return R.ok(selectPage);
    }

    // 通过用户id得到用户粉丝列表
    @Override
    public ResultVO getFansListByUserId(Integer currentPage, Integer pageSize, Long userId, String nowUserId) {

        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", userId);
        userFansQueryWrapper.orderByDesc("create_time");
        List<UserFans> userFansList = userFansMapper.selectList(userFansQueryWrapper);
        List<UserVo> userList = new ArrayList<>();
        int len = (currentPage - 1) * pageSize;
        for (int i = len; i < Math.max(len, userFansList.size()); i ++ ) {
            UserFans userFans = userFansList.get(i);
            UserVo userVo = new UserVo();
            Long fansId = userFans.getFansId();
            User user = userMapper.selectById(fansId);
            userVo.setId(user.getId());
            userVo.setPhoto(user.getPhoto());
            userVo.setUsername(user.getUsername());
            userVo.setBrief(user.getBrief());

            if (nowUserId == null || "".equals(nowUserId)) {
                userVo.setIsFans(0L);
            } else {
                QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
                userFansQueryWrapper1.eq("user_id", user.getId());
                userFansQueryWrapper1.eq("fans_id", nowUserId);
                Long selectCount = userFansMapper.selectCount(userFansQueryWrapper1);
                userVo.setIsFans(selectCount);
            }

            userList.add(userVo);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", userFansList.size());
        jsonObject.put("userList", userList);
        return new ResultVO(ResultStatus.OK, null, jsonObject);
    }

    // 通过用户id得到关注列表
    @Override
    public ResultVO getConcernListByUserId(Integer currentPage, Integer pageSize, Long userId, String nowUserId) {

        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("fans_id", userId);
        userFansQueryWrapper.orderByDesc("create_time");
        List<UserFans> userFansList = userFansMapper.selectList(userFansQueryWrapper);
        List<UserVo> userList = new ArrayList<>();
        int len = (currentPage - 1) * pageSize;
        for (int i = len; i < Math.max(len, userFansList.size()); i ++ ) {
            UserFans userFans = userFansList.get(i);
            UserVo userVo = new UserVo();
            Long userFansUserId = userFans.getUserId();
            User user = userMapper.selectById(userFansUserId);
            userVo.setId(user.getId());
            userVo.setPhoto(user.getPhoto());
            userVo.setUsername(user.getUsername());
            userVo.setBrief(user.getBrief());

            if (nowUserId == null || "".equals(nowUserId)) {
                userVo.setIsFans(0L);
            } else {

                QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
                userFansQueryWrapper1.eq("user_id", user.getId());
                userFansQueryWrapper1.eq("fans_id", nowUserId);
                Long selectCount = userFansMapper.selectCount(userFansQueryWrapper1);
                userVo.setIsFans(selectCount);
            }

            userList.add(userVo);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", userFansList.size());
        jsonObject.put("userList", userList);
        return new ResultVO(ResultStatus.OK, null, jsonObject);
    }
    // 提交编辑资料之后更新用户信息
    @Override
    public ResultVO updateInformation(String userInformation1) {
        User userInformation = JSONObject.parseObject(userInformation1, User.class);
        String username = userInformation.getUsername();
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        User user = userMapper.selectOne(userQueryWrapper);
        if (user != null) {
            return new ResultVO(ResultStatus.OK, "该用户名已存在，请重试", null);
        }
        int updateById = userMapper.updateById(userInformation);
        if (updateById > 0) {
            return new ResultVO(ResultStatus.OK, "更新用户信息成功", null);
        }
        return new ResultVO(ResultStatus.NO, null, null);
    }

    // 调不通模块的方法
    // 通过用户id得到文章提问列表
    @Override
    public R getQuestionListByUserId(Long userId, Long currentPage, Long pageSize) {
        R questionListByUserId = userToQuestionFeignService.getQuestionListByUserId(userId, currentPage, pageSize);
        if (questionListByUserId.getCode() != R.SUCCESS) {
            throw new MonkeyBlogException(questionListByUserId.getCode(), questionListByUserId.getMsg());
        }

        Page selectPage = (Page) questionListByUserId.getData(new TypeReference<Page>(){});
        return R.ok(selectPage);
    }
}
