package com.monkey.monkeybackend.Service.Impl.Blog;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeybackend.Mapper.Blog.LabelMapper;
import com.monkey.monkeybackend.Pojo.Label;
import com.monkey.monkeybackend.Service.Blog.LabelService;
import com.monkey.monkeybackend.utils.result.ResultStatus;
import com.monkey.monkeybackend.utils.result.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public ResultVO getLabelList() {
        List<Label> labelList = labelMapper.selectList(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("labelList", labelList);

        return new ResultVO(ResultStatus.OK, null, jsonObject);
    }
}
