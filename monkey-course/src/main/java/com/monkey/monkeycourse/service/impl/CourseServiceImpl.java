package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.Vo.LabelVo;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeycourse.mapper.CourseLabelMapper;
import com.monkey.monkeycourse.mapper.CourseMapper;
import com.monkey.monkeycourse.pojo.Course;
import com.monkey.monkeycourse.pojo.CourseLabel;
import com.monkey.monkeycourse.service.CourseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/7/15 20:44
 * @version: 1.0
 * @description:
 */
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private LabelMapper labelMapper;
    @Autowired
    private CourseLabelMapper courseLabelMapper;
    @Autowired
    private CourseMapper courseMapper;

    // 得到一级标签列表
    @Override
    public ResultVO getOneLabelList() {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_ONE.getCode());
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);
        List<LabelVo> labelVoList = new ArrayList<>();
        for (Label label : labelList) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(label, labelVo);
            labelVo.setSelected(false);
            labelVoList.add(labelVo);
        }
        return new ResultVO(ResultStatus.OK, null, labelVoList);
    }

    // 通过一级标签id得到二级标签
    @Override
    public ResultVO getTwoLabelListByOneLabelId(Long oneLabelId) {
        QueryWrapper<Label> labelQueryWrapper = new QueryWrapper<>();
        labelQueryWrapper.eq("parent_id", oneLabelId);
        labelQueryWrapper.eq("level", CommonEnum.LABEL_LEVEL_TWO.getCode());
        List<Label> labelList = labelMapper.selectList(labelQueryWrapper);

        List<LabelVo> labelVoList = new ArrayList<>();
        for (Label label : labelList) {
            LabelVo labelVo = new LabelVo();
            BeanUtils.copyProperties(label, labelVo);
            labelVo.setSelected(false);
            labelVoList.add(labelVo);
        }
        return new ResultVO(ResultStatus.OK, null, labelVoList);
    }

    // 通过二级标签分页查询文章列表
    @Override
    public ResultVO getCourseListByTwoLabelId(long twoLabelId, long currentPage, long pageSize) {
        QueryWrapper<CourseLabel> courseLabelQueryWrapper  = new QueryWrapper<>();
        courseLabelQueryWrapper.eq("label_id", twoLabelId);
        List<CourseLabel> courseLabelList = courseLabelMapper.selectList(courseLabelQueryWrapper);
        List<Long> courseIdList = new ArrayList<>();
        for (CourseLabel courseLabel : courseLabelList) {
            courseIdList.add(courseLabel.getCourseId());
        }
        if (courseLabelList.size() > 0) {
            QueryWrapper<Course> courseQueryWrapper = new QueryWrapper<>();
            courseQueryWrapper.in("id", courseIdList);
            courseQueryWrapper.orderByDesc("create_time");
            Page page = new Page<>(currentPage, pageSize);
            Page<Course> selectPage = courseMapper.selectPage(page, courseQueryWrapper);
            return new ResultVO(ResultStatus.OK, null, selectPage);
        }

        return new ResultVO(ResultStatus.OK, null, "");
    }
}
