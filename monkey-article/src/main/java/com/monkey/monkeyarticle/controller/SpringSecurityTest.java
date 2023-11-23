package com.monkey.monkeyarticle.controller;

import com.monkey.monkeyUtils.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wusihao
 * @date: 2023/11/19 9:50
 * @version: 1.0
 * @description:
 */
@Api(tags = "测试springsecurity接口")
@RestController
@RequestMapping("/monkey-article/springsecurity")
public class SpringSecurityTest {

    @ApiOperation("测试testSecured")
    @GetMapping("/test")
    // 使得只有community_manager权限的用户才可以执行此方法
    @Secured({"ROLE_community_manager"})
    public R testSecured() {
        return R.ok("测试@Secured注解成功");
    }

    @ApiOperation("测试PreAuthorize")
    @GetMapping("/testPreAuthorize")
    // 在进入方法之前做校验，与secured区别主要在于该注解不仅可以授权角色，也可以授权权限
    @PreAuthorize("hasAnyAuthority('wusihao')")
    public R testPreAuthorize() {
        return R.ok("测试@PreAuthorize注解成功");
    }

    @ApiOperation("自定义授权")
    @GetMapping("/testCustomAuthority")
    // 在进入方法之前做校验，与secured区别主要在于该注解不仅可以授权角色，也可以授权权限
    @PreAuthorize("@customPermission.hasCommunityManagerAuthority('wusihao')")
    public R testCustomAuthority() {
        return R.ok("测试@PreAuthorize注解成功");
    }

    @ApiOperation("测试PostAuthorize")
    @GetMapping("/testPostAuthorize")
    // 在执行完方法之后做检验
    @PostAuthorize("hasAnyAuthority('wusihao1')")
    public R testPostAuthorize() {
        System.out.println("PostAuthorize");
        return R.ok("测试@PostAuthorize注解成功");
    }

    @ApiOperation("测试PostFilter")
    @GetMapping("/testPostFilter")
    // 对方法返回的数据做过滤
    @PreAuthorize("@hasAnyAuthority('wusihao')")
    @PostFilter("filterObject.data == 1")
    public R testPostFilter() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        System.out.println("testPostFilter");
        return R.ok("测试@testPostFilter注解成功");
    }
    @ApiOperation("测试PreFilter")
    @GetMapping("/testPreFilter")
    // 对传入方法的参数做过滤
    @PreFilter("hasAnyAuthority('wusihao')")
    public R testPreFilter() {
        System.out.println("testPreFilter");
        return R.ok("测试@PreFilter注解成功");
    }

}
