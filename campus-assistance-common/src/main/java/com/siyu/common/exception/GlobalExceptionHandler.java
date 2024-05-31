package com.siyu.common.exception;

import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        e.printStackTrace();
        BindingResult exceptions = e.getBindingResult();
        if (exceptions.hasErrors()) {
            List<ObjectError> errors = exceptions.getAllErrors();
            if (!errors.isEmpty()) {
                FieldError fieldError = (FieldError) errors.get(0);
                return R.fail(ErrorStatus.PARAM_ERROR, "参数校验失败[" + fieldError.getDefaultMessage() + "]");
            }
        }
        return R.fail(ErrorStatus.PARAM_ERROR);
    }

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
        return R.fail(ErrorStatus.AUTHOR_ERROR);
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
        return R.fail(ErrorStatus.AUTHEN_ERROR);
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
