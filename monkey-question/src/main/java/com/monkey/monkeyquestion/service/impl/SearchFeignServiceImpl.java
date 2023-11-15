package com.monkey.monkeyquestion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.result.R;
import com.monkey.monkeyquestion.constant.QuestionPictureEnum;
import com.monkey.monkeyquestion.mapper.QuestionLabelMapper;
import com.monkey.monkeyquestion.mapper.QuestionMapper;
import com.monkey.monkeyquestion.pojo.Question;
import com.monkey.monkeyquestion.pojo.QuestionLabel;
import com.monkey.monkeyquestion.service.SearchFeignService;
import com.monkey.spring_security.mapper.UserMapper;
import com.monkey.spring_security.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: wusihao
 * @date: 2023/11/9 16:20
 * @version: 1.0
 * @description:
 */
@Service
public class SearchFeignServiceImpl implements SearchFeignService {

    @Resource
    private QuestionMapper questionMapper;
    @Resource
    private QuestionLabelMapper questionLabelMapper;
    @Resource
    private LabelMapper labelMapper;
    @Resource
    private UserMapper userMapper;
    /**
     * 查询所有问答
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/9 16:25
     */
    @Override
    public R queryAllQuestion() {
        List<Question> questionList = questionMapper.selectList(null);
        questionList.parallelStream().forEach(question -> {
            Long questionId = question.getId();

            // 得到问答标签信息
            QueryWrapper<QuestionLabel> questionLabelQueryWrapper = new QueryWrapper<>();
            questionLabelQueryWrapper.eq("question_id", questionId);
            questionLabelQueryWrapper.select("label_id");
            List<Object> labelIds = questionLabelMapper.selectObjs(questionLabelQueryWrapper);
            if (labelIds != null && labelIds.size() > 0) {
                QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
                labelQueryWrapper.in("id", labelIds);
                List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
                List<String> labelName = new ArrayList<>();
                labelList.forEach(fi -> labelName.add(fi.getLabelName()));
                question.setLabelName(labelName);
            }

            // 得到用户信息
            Long userId = question.getUserId();
            User user = userMapper.selectById(userId);
            question.setUsername(user.getUsername());
            question.setUserHeadImg(user.getPhoto());
            question.setUserBrief(user.getBrief());
            // 得到问答图片
            question.setPhoto(QuestionPictureEnum.QUESTION_DEFAULT_PIRCUTR.getUrl());
        });
        return R.ok(questionList);
    }

    /**
     得到所有用户所有问答，点赞，收藏，游览数
     *
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/14 10:58
     */
    @Override
    public R queryAllUserQuestionInfo() {
        QueryWrapper<Question> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.groupBy("user_id");
        articleQueryWrapper.select("user_id",
                "sum(collect_count) as collectCount",
                "sum(view_count) as viewCount",
                "sum(like_count) as likeCount",
                "count(*) as opusCount"
        );
        List<Map<String, Object>> maps = questionMapper.selectMaps(articleQueryWrapper);
        return R.ok(maps);
    }
}
