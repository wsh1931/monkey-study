package com.monkey.monkeyUtils.util;

import com.monkey.monkeyUtils.exception.MonkeyBlogException;
import com.monkey.monkeyUtils.result.R;

import javax.servlet.http.HttpServletResponse;

/**
 * @author: wusihao
 * @date: 2023/11/19 16:09
 * @version: 1.0
 * @description:
 */
public class WebUtils {

    /**
     * 将字符串渲染到客户端
     *
     * @param string 需要渲染的字符串
     * @return {@link null}
     * @author wusihao
     * @date 2023/11/19 16:09
     */
     public static String renderString(HttpServletResponse response, String string) {
         try {
             response.setStatus(R.SUCCESS);
             response.setContentType("application/json");
             response.setCharacterEncoding("utf-8");
             response.getWriter().print(string);
         } catch (Exception e) {
             throw new MonkeyBlogException(R.Error, e.getMessage());
         }

         return null;
     }

}
