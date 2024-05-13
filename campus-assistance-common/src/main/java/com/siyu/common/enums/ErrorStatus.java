package com.siyu.common.enums;

import lombok.Getter;

@Getter
public enum ErrorStatus {

    SYS_ERROR("A0000", "系统异常"),

    AUTHOR_ERROR("A0200", "权限异常"),

    AUTHEN_ERROR("A0201", "认证异常"),

    TOKEN_ERROR("A0300", "TOKEN认证异常"),

    LOGIN_ERROR("A0400", "登录失败"),

    STATUS_ERROR("A0500", "状态异常"),

    INSERT_ERROR("S0100", "插入异常"),

    UNKNOWN("UNKNOWN", "UNKNOWN")
    ;



    ErrorStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;

    private final String message;

    /**
     * 根据状态码获取枚举类型
     * @param code
     * @return ErrorStatus
     */
    public static ErrorStatus resolve(String code) {
        if (code == null || code.isEmpty()) {
            return UNKNOWN;
        }
        for (ErrorStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return UNKNOWN;
    }


}
