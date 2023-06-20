package com.monkey.monkeyarticle.service.impl.blog;

import com.monkey.monkeyUtils.result.ResultStatus;
import com.monkey.monkeyUtils.result.ResultVO;
import com.monkey.monkeyUtils.mapper.LabelMapper;
import com.monkey.monkeyUtils.pojo.Label;
import com.monkey.monkeyarticle.service.blog.LabelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public ResultVO getLabelList() {
        List<Label> labels = labelMapper.selectList(null);
        return new ResultVO(ResultStatus.OK, null, labels);
    }
}
