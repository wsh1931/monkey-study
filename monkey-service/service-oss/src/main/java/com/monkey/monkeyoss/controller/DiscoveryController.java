package com.monkey.monkeyoss.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author: wusihao
 * @description:
 * @date: 2023/7/10 8:52
 * @version: 1.0
 */
@RestController
@CrossOrigin
public class DiscoveryController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/test")
    public String discovery(String serviceName) {
        // 通过服务应用名，找到服务具体信息
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
        instances.forEach(System.out::println);
        return instances.get(0).toString();
    }
}
