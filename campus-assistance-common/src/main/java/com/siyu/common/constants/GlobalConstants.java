package com.siyu.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class GlobalConstants {

    @Value("${user.salt}")
    private String salt;

    @Value("${jwt.refresh}")
    private Integer refresh;

    @Value("${jwt.expire}")
    private Integer expire;

    @Value("${jwt.secret}")
    private String secret;

    public static Long JWT_REFRESH;

    public static Long JWT_EXPIRE;

    public static String JWT_SECRET;

    public static String USER_SALT;

    @PostConstruct
    private void init() {
        JWT_REFRESH = 1000L * 60 * 60 * refresh;
        JWT_EXPIRE = 1000L * 60 * 60 * expire;
        JWT_SECRET = secret;
        USER_SALT = salt;
    }


}
