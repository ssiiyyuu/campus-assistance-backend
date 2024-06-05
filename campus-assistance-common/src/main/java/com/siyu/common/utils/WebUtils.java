package com.siyu.common.utils;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public class WebUtils {

    public static final String AUTHENTICATION_HEADER = "Authorization";

    public static void writePNG2Response(InputStream inputStream, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("image/png;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        byte[] buf = new byte[1024];
        int length;
        OutputStream out = response.getOutputStream();
        while ((length = inputStream.read(buf)) != -1) {
            out.write(buf, 0, length);
        }
        out.close();
    }

    public static void write2Response(Object object, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());

        PrintWriter writer = response.getWriter();

        String json = JSONObject.toJSONString(object, JSONWriter.Feature.WriteMapNullValue);
        writer.write(json);
        writer.close();
    }

    public static HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null) {
            throw new NullPointerException();
        }
        return requestAttributes.getResponse();
    }

    public static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(requestAttributes == null) {
            throw new NullPointerException();
        }
        return requestAttributes.getRequest();
    }

    public static void setHeader(String name, String value) {
        setHeader(name, value, getHttpServletResponse());
    }

    public static void setHeader(String name, String value, HttpServletResponse response) {
        response.setHeader(name, value);
    }

    public static String getHeader(String name) {
        return getHeader(name, getHttpServletRequest());
    }

    public static String getHeader(String name, HttpServletRequest request) {
        return request.getHeader(name);
    }
}
