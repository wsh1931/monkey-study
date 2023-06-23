package com.monkey.monkeyblog.service.Impl.user.center;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.redis.RedisTimeConstant;
import com.monkey.monkeyUtils.redis.RedisUrlConstant;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyarticle.mapper.UserFansMapper;
import com.monkey.monkeyarticle.mapper.article.*;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.pojo.user.UserFans;
import com.monkey.monkeyarticle.pojo.user.UserVo;
import com.monkey.monkeyarticle.pojo.article.*;
import com.monkey.monkeyarticle.pojo.vo.article.ArticleVo;
import com.monkey.monkeyblog.mapper.user.RecentVisitUserhomeMapper;
import com.monkey.monkeyblog.pojo.Vo.LabelVo;
import com.monkey.monkeyblog.pojo.Vo.user.RecentVisitUserhomeVo;
import com.monkey.monkeyblog.pojo.user.RecentVisitUserhome;
import com.monkey.monkeyblog.service.user.center.UserHomeService;
import com.monkey.spring_security.mapper.user.UserMapper;
import com.monkey.spring_security.pojo.user.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserHomeServiceImpl implements UserHomeService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ArticleLikeMapper articleLikeMapper;
    @Autowired
    private ArticleCollectMapper articleCollectMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    @Autowired
    private UserFansMapper userFansMapper;
    @Autowired
    private RecentVisitUserhomeMapper recentVisitUserhomeMapper;
    @Autowired
    private ArticleLabelMapper articleLabelMapper;
    @Autowired
    private LabelMapper labelMapper;




    // 通过用户id查询用户信息Vo
    @Override
    public ResultVO getUserInformationByUserId(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        UserVo userVo = new UserVo();
        User user = userMapper.selectById(userId);
        BeanUtils.copyProperties(user, userVo);
        QueryWrapper<ArticleCollect> articleCollectQueryWrapper = new QueryWrapper<>();
        articleCollectQueryWrapper.eq("user_id", userId);
        userVo.setCollect(articleCollectMapper.selectCount(articleCollectQueryWrapper));
        // 获得用户总的点赞数, 收藏数，评论数
        // 找到用户发表的文章数
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("user_id", userId);
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);
        userVo.setArticleSum((long)articleList.size());
        Long userLikes = 0L;
        Long userCollects = 0L;
        Long userComment = 0L;
        Long articleVisits = 0L;
        // 获得用户所有文章的点赞数, 收藏数，评论数, 文章游览数
        for (Article article : articleList) {
            Long articleId = article.getId();
            articleVisits += article.getVisit();
            QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
            userLikeQueryWrapper.eq("article_id", articleId);
            Long userLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
            userLikes += userLike;

            QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
            userCollectQueryWrapper.eq("article_id", articleId);
            Long userCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
            userCollects += userCollect;

            QueryWrapper<ArticleComment> commentQueryWrapper = new QueryWrapper<>();
            commentQueryWrapper.eq("article_id", articleId);
            Long comment = articleCommentMapper.selectCount(commentQueryWrapper);
            userComment += comment;
        }

        userVo.setLikeSum(userLikes);
        userVo.setUserCollect(userCollects);
        userVo.setCommentSum(userComment);
        userVo.setVisit(articleVisits);

        // 得到用户粉丝数和关注数
        QueryWrapper<UserFans> userFansQueryWrapper = new QueryWrapper<>();
        userFansQueryWrapper.eq("user_id", userId);
        userVo.setFans(userFansMapper.selectCount(userFansQueryWrapper));

        QueryWrapper<UserFans> userFansQueryWrapper1 = new QueryWrapper<>();
        userFansQueryWrapper1.eq("fans_id", userId);
        userVo.setConcern(userFansMapper.selectCount(userFansQueryWrapper1));

        // 判断当前登录用户与作者是否是粉丝
        String nowUserId1 = data.get("nowUserId");
        if (nowUserId1 != null && !"".equals(nowUserId1)) {
            long nowUserId = Long.parseLong(nowUserId1);
            QueryWrapper<UserFans> fansQueryWrapper = new QueryWrapper<>();
            fansQueryWrapper.eq("fans_id", userId);
            fansQueryWrapper.eq("user_id", nowUserId);
            Long aLong = userFansMapper.selectCount(fansQueryWrapper);
            userVo.setIsFans(aLong);
        } else {
            userVo.setIsFans(0L);
        }

        return new ResultVO(ResultStatus.OK, null, userVo);

    }

    // 将访问者信息加入用户游览信息列表
    @Override
    public ResultVO recentlyView(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long reviewId = Long.parseLong(data.get("reviewId"));
        if (userId == reviewId) return new ResultVO(ResultStatus.OK, null, null);
        RecentVisitUserhome recentVisitUserhome = new RecentVisitUserhome();
        recentVisitUserhome.setBeVisitId(userId);
        recentVisitUserhome.setVisitId(reviewId);
        recentVisitUserhome.setCreateTime(new Date());
        int insert = recentVisitUserhomeMapper.insert(recentVisitUserhome);
        if (insert > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        } else {
            return new ResultVO(ResultStatus.NO, null, null);
        }
    }

    // 通过用户id得到最近来访用户信息
    @Override
    public ResultVO getRecentlyUserInfoByUserId(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        QueryWrapper<RecentVisitUserhome> recentVisitUserhomeQueryWrapper = new QueryWrapper<>();
        recentVisitUserhomeQueryWrapper.eq("be_visit_id", userId);
        recentVisitUserhomeQueryWrapper.orderByDesc("create_time");
        List<RecentVisitUserhome> recentVisitUserhomeList = recentVisitUserhomeMapper.selectList(recentVisitUserhomeQueryWrapper);
        // 去重，每次只展示同一用户的最新访问时间
        List<RecentVisitUserhomeVo> recentVisitUserhomeVoList = new ArrayList<>();
        Set<Long> set = new HashSet<>();
        for (RecentVisitUserhome recentVisitUserhome : recentVisitUserhomeList) {
            Long visitId = recentVisitUserhome.getVisitId();
            if (set.contains(visitId)) continue;
            else {
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
    public ResultVO getUserArticleClassficationCountByuserId(Map<String, String> data) {
        List<LabelVo> labelVoList = new ArrayList<>();
        long userId = Long.parseLong(data.get("userId"));
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.eq("user_id", userId);
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);

        for (Article article : articleList) {
            Long articleId = article.getId();
            QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
            articleLabelQueryWrapper.eq("article_id", articleId);
            List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
            for (ArticleLabel articleLabel : articleLabelList) {
                Long labelId = articleLabel.getLabelId();
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
    public ResultVO getArticleListByUserId(Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
        Long labelId = Long.parseLong(data.get("labelId"));
        String userId = data.get("userId");
        Page page = new Page<>(currentPage, pageSize);
        if (labelId != -1L) {
            QueryWrapper<ArticleLabel> articleLabelQueryWrapper = new QueryWrapper<>();
            articleLabelQueryWrapper.eq("label_id", labelId);
            List<ArticleLabel> articleLabelList = articleLabelMapper.selectList(articleLabelQueryWrapper);
            List<Long> ids = new ArrayList<>();
            for (ArticleLabel articleLabel : articleLabelList) {
                Long articleId = articleLabel.getArticleId();
                ids.add(articleId);
            }

            // selectPage方法查询到的数据不可为空
            if (ids.size() > 0) {
                QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
                articleQueryWrapper.in("id", ids);
                articleQueryWrapper.eq("user_id", userId);

                Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
                List<Article> records = selectPage.getRecords();
                List<ArticleVo> articleVoList = new ArrayList<>();
                for (Article article : records) {
                    ArticleVo temp = new ArticleVo();
                    BeanUtils.copyProperties(article, temp);
                    // 查询文章点赞数
                    Long articleId = article.getId();
                    QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                    userLikeQueryWrapper.eq("article_id", articleId);
                    Long userLikeSum = articleLikeMapper.selectCount(userLikeQueryWrapper);


                    // 查询文章收藏数
                    QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
                    userCollectQueryWrapper.eq("article_id", articleId);
                    Long collect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setCollect(collect);
                    temp.setLikeSum(userLikeSum);

                    // 判断用户是否点赞/收藏该文章
                    if (userId != null || !userId.equals("")) {
                        userCollectQueryWrapper.eq( "user_id", userId);
                        Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                        temp.setIsCollect(isCollect);
                        userLikeQueryWrapper.eq("user_id", userId);
                        Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
                        temp.setIsLike(isLike);
                    } else {
                        temp.setIsCollect(0L);
                        temp.setIsLike(0L);
                    }

                    articleVoList.add(temp);
                }

                // 按创建时间降序排序
                Collections.sort(articleVoList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
                selectPage.setRecords(articleVoList);
                return new ResultVO(ResultStatus.OK, null, selectPage);
            } else {
                return new ResultVO(ResultStatus.OK, null, null);
            }
        } else {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.orderByDesc("create_time");
            articleQueryWrapper.eq("user_id", userId);
            Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
            List<Article> records = selectPage.getRecords();
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article article : records) {
                ArticleVo temp = new ArticleVo();
                BeanUtils.copyProperties(article, temp);
                // 查询文章点赞数
                Long articleId = article.getId();
                QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                userLikeQueryWrapper.eq("article_id", articleId);
                Long userLikeSum = articleLikeMapper.selectCount(userLikeQueryWrapper);

                // 查询文章收藏数
                QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
                userCollectQueryWrapper.eq("article_id", articleId);
                Long collect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                temp.setCollect(collect);
                temp.setLikeSum(userLikeSum);

                // 判断用户是否点赞/收藏该文章
                if (userId != null || !userId.equals("")) {
                    userCollectQueryWrapper.eq( "user_id", userId);
                    Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setIsCollect(isCollect);
                    userLikeQueryWrapper.eq("user_id", userId);
                    Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
                    temp.setIsLike(isLike);
                } else {
                    temp.setIsCollect(0L);
                    temp.setIsLike(0L);
                }
                articleVoList.add(temp);
                // 根据点击的分页数得到当前页信息
            }

            selectPage.setRecords(articleVoList);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        }
    }

    // 通过用户id得到用户粉丝列表
    @Override
    public ResultVO getFansListByUserId(Map<String, String> data) {
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));

        long userId = Long.parseLong(data.get("userId"));
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
            String nowUserId = data.get("nowUserId");
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
    public ResultVO getConcernListByUserId(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        int currentPage = Integer.parseInt(data.get("currentPage"));
        int pageSize = Integer.parseInt(data.get("pageSize"));
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
            String nowUserId = data.get("nowUserId");
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

    // 通过用户id得到用户收藏文章列表
    @Override
    public ResultVO getUserCollectArticleListByUserId(Map<String, String> data) {
        long userId = Long.parseLong(data.get("userId"));
        long currentPage = Long.parseLong(data.get("currentPage"));
        long pageSize = Long.parseLong(data.get("pageSize"));
        Page page = new Page<>(currentPage, pageSize);
        String nowUserId = data.get("nowUserId");
        QueryWrapper<ArticleCollect> userCollectQueryWrapper = new QueryWrapper<>();
        userCollectQueryWrapper.eq("user_id", userId);
        List<ArticleCollect> articleCollectList = articleCollectMapper.selectList(userCollectQueryWrapper);
        List<Long> ids = new ArrayList<>();
        for (ArticleCollect articleCollect : articleCollectList) {
            Long articleId = articleCollect.getArticleId();
            ids.add(articleId);
        }

        // selectPage方法查询到的数据不可为空
        if (ids.size() > 0) {
            QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
            articleQueryWrapper.in("id", ids);

            Page selectPage = articleMapper.selectPage(page, articleQueryWrapper);
            List<Article> records = selectPage.getRecords();
            List<ArticleVo> articleVoList = new ArrayList<>();
            for (Article article : records) {
                ArticleVo temp = new ArticleVo();
                BeanUtils.copyProperties(article, temp);
                // 查询文章点赞数
                Long articleId = article.getId();
                QueryWrapper<ArticleLike> userLikeQueryWrapper = new QueryWrapper<>();
                userLikeQueryWrapper.eq("article_id", articleId);
                Long userLikeSum = articleLikeMapper.selectCount(userLikeQueryWrapper);


                // 查询文章收藏数
                QueryWrapper<ArticleCollect> userCollectQueryWrapper1 = new QueryWrapper<>();
                userCollectQueryWrapper1.eq("article_id", articleId);
                Long collect = articleCollectMapper.selectCount(userCollectQueryWrapper1);
                temp.setCollect(collect);
                temp.setLikeSum(userLikeSum);

                // 判断用户是否点赞/收藏该文章
                if (nowUserId != null || !nowUserId.equals("")) {
                    userCollectQueryWrapper.eq( "user_id", nowUserId);
                    Long isCollect = articleCollectMapper.selectCount(userCollectQueryWrapper);
                    temp.setIsCollect(isCollect);
                    userLikeQueryWrapper.eq("user_id", nowUserId);
                    Long isLike = articleLikeMapper.selectCount(userLikeQueryWrapper);
                    temp.setIsLike(isLike);
                } else {
                    temp.setIsCollect(0L);
                    temp.setIsLike(0L);
                }
                articleVoList.add(temp);
            }
            // 按创建时间降序排序
            Collections.sort(articleVoList, (o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
            selectPage.setRecords(articleVoList);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        } else {
            return new ResultVO(ResultStatus.OK, null, null);
        }
    }

    // 提交编辑资料之后更新用户信息
    @Override
    public ResultVO updateInformation(Map<String, String> data) {
        User userInformation = JSONObject.parseObject(data.get("userInformation"), User.class);
        int updateById = userMapper.updateById(userInformation);
        if (updateById > 0) {
            return new ResultVO(ResultStatus.OK, null, null);
        }
        return new ResultVO(ResultStatus.NO, null, null);
    }


}
