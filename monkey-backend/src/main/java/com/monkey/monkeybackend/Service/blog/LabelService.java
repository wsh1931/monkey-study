package com.monkey.monkeybackend.Service.blog;

import com.alibaba.fastjson.JSONObject;


public interface LabelService {
    // 得到标签列表
    JSONObject getLabelList();
}
