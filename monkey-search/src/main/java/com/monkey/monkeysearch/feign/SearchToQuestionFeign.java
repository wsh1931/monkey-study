package com.monkey.monkeysearch.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "monkey-question", contextId = "search-to-question")
public interface SearchToQuestionFeign {

    @GetMapping("/monkey-question/search/feign/queryAllQuestion")
    R queryAllQuestion();
}