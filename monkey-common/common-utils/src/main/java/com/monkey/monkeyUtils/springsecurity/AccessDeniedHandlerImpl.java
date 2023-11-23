package com.monkey.monkeyUtils.springsecurity;

import com.alibaba.fastjson.JSONObject;
import com.monkey.monkeyUtils.constants.CommonEnum;
import com.monkey.monkeyUtils.constants.ECode;
import com.monkey.monkeyUtils.result.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author: wusihao
 * @date: 2023/11/21 16:06
 * @version: 1.0
 * @description: 授权失败统一异常处理
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String msg = "请求访问：" + request.getRequestURI() + "，授权失败，您没有权限访问";
        System.out.println(msg);
        response.setStatus(R.Error);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        Map<String, Object> result = new HashMap<>(CommonEnum.UNAUTHORIZED_LOGIN.getCode());
        result.put("code", ECode.NO_OPERATION_AUTHORITY);
        result.put("msg", msg);
        result.put("data", "token无效或过期,请重新登录");
        response.getWriter().write(JSONObject.toJSONString(result));
    }
}
