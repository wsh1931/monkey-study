package com.monkey.monkeybackend.Service.Blog;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeybackend.utils.result.ResultVO;


public interface LabelService {
    // 得到标签列表
    ResultVO getLabelList();
}
