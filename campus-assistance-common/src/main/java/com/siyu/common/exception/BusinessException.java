package com.siyu.common.exception;

import com.siyu.common.enums.ErrorStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {

    private String code;

    private String message;

    private BusinessException() {

    }

    public BusinessException(ErrorStatus status) {
        this.code = status.getCode();
        this.message = status.getMessage();
    }

    public BusinessException(ErrorStatus status, String message) {
        this.code = status.getCode();
        this.message = message;
    }

    public BusinessException appendMessage(String message) {
        this.message = this.message + "[" + message + "]";
        return this;
    }
}
