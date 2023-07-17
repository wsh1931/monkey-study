package com.monkey.monkeycourse.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyUtils.pojo.Vo.LabelVo;
import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
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
}
