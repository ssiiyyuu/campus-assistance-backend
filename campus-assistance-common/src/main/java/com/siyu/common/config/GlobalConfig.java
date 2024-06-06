package com.siyu.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class GlobalConfig {
    @Value("${qiniu.url}")
    private String ossUrl;

    @Value("${qiniu.accessKey}")
    private String ossAccessKey;

    @Value("${qiniu.secretKey}")
    private String ossSecretKey;

    @Value("${qiniu.bucketName}")
    private String ossBucketName;

    @Value("${jwt.refresh}")
    private Integer jwtRefresh;

    @Value("${jwt.expire}")
    private Integer jwtExpire;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${user.salt}")
    private String userSalt;

    public static Long JWT_REFRESH;

    public static Long JWT_EXPIRE;

    public static String JWT_SECRET;

    public static String USER_SALT;

    public static String OSS_URL;

    public static String OSS_ACCESS_KEY;

    public static String OSS_SECRET_KEY;

    public static String OSS_BUCKET_NAME;

    @PostConstruct
    private void init() {
        JWT_REFRESH = 1000L * 60 * 60 * jwtRefresh;
        JWT_EXPIRE = 1000L * 60 * 60 * jwtExpire;
        JWT_SECRET = jwtSecret;
        USER_SALT = userSalt;
        OSS_URL = ossUrl;
        OSS_ACCESS_KEY = ossAccessKey;
        OSS_SECRET_KEY = ossSecretKey;
        OSS_BUCKET_NAME = ossBucketName;
    }


}
