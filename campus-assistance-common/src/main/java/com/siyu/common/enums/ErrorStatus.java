package com.siyu.common.enums;

import lombok.Getter;

@Getter
public enum ErrorStatus {

    SYS_ERROR("A0000", "系统异常"),

    PARAM_ERROR("A0100", "参数校验失败"),

    AUTHOR_ERROR("A0200", "权限异常"),

    AUTHEN_ERROR("A0201", "认证失败"),

    TOKEN_ERROR("A0300", "TOKEN认证失败"),

    LOGIN_ERROR("A0400", "登录失败"),

    STATUS_ERROR("A0500", "状态异常"),

    INSERT_ERROR("S0100", "插入异常"),

    UPDATE_ERROR("S0200", "更新异常"),

    DELETE_ERROR("S0300", "删除异常"),

    QUERY_ERROR("S0400", "查询异常"),

    UPLOAD_ERROR("S0500", "上传异常"),

    SOCKET_ERROR("B0100", "socket异常"),

    IO_ERROR("S0600", "IO异常"),

    UNKNOWN("UNKNOWN", "UNKNOWN");



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
