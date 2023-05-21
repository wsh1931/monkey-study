package com.monkey.monkeybackend.Service.Impl.blog;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeybackend.Mapper.blog.LabelMapper;
import com.monkey.monkeybackend.Pojo.Label;
import com.monkey.monkeybackend.Service.blog.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LabelServiceImpl implements LabelService {
    @Autowired
    private LabelMapper labelMapper;

    @Override
    public JSONObject getLabelList() {
        List<Label> labelList = labelMapper.selectList(null);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("label_list", labelList);
        return jsonObject;
    }
}
