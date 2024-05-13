package com.siyu.common.exception;

import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public R<String> BusinessException(BusinessException e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.resolve(e.getCode()), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(UnauthorizedException.class)
    public R<String> UnauthorizedException(UnauthorizedException e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.AUTHOR_ERROR, "未授权访问");
    }

    @ResponseBody
    @ExceptionHandler(AuthorizationException.class)
    public R<String> AuthorizationException(AuthorizationException e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.AUTHOR_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(UnauthenticatedException.class)
    public R<String> UnauthenticatedException(UnauthenticatedException e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.AUTHEN_ERROR, "未通过认证");
    }

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public R<String> AuthenticationException(AuthenticationException e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.AUTHEN_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public R<String> Exception(Exception e) {
        e.printStackTrace();
        return R.fail(ErrorStatus.SYS_ERROR);
    }

}
