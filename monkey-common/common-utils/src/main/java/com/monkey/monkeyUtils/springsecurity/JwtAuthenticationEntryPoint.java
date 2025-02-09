package com.monkey.monkeyUtils.springsecurity;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.ECode;
import com.monkey.monkeyUtils.result.R;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证失败统一异常处理
 * jwt 认证进入点 【认证失败处理类 返回未授权】
 *
 * @author wusihao
 * @date 2020年9月19日10:04:54
 */
@Component
public class  JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String msg = "请求访问：" + request.getRequestURI() + "，认证失败，您的权限不足";;
        response.setStatus(R.Error);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> result = new HashMap<>(CommonEnum.UNAUTHORIZED_LOGIN.getCode());
        result.put("code", ECode.UNAUTHORIZED);
        result.put("msg", msg);
        result.put("data", "权限不足");
        response.getWriter().write(JSONObject.toJSONString(result));
    }
}

