package com.monkey.monkeycourse.feign;

import com.monkey.monkeyUtils.result.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "monkey-user", contextId = "course-to-user")
public interface CourseToUserFeignService {
    // 通过用户id得到用户关注数和粉丝数
    @GetMapping("/monkey-user/feign/getUserConcernAndFansCountByUserId/{userId}")
    R getUserConcernAndFansCountByUserId(@PathVariable Long userId);

    // 通过fans_id和user_id判断当前登录用户是否是对方粉丝
    @GetMapping("/monkey-user/feign/judgeLoginUserAndAuthorConnect")
    R judgeLoginUserAndAuthorConnect(@RequestParam Long userId, @RequestParam Long fansId);
}
