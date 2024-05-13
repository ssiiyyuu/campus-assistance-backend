package com.siyu.common.domain;

import com.siyu.common.enums.ErrorStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class R<T> {

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

    @ApiModelProperty(value = "返回状态码")
    private String status;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "响应数据")
    private T data;

    private R() {

    }

    public static boolean isSuccess(R<?> result) {
        return result != null && result.success;
    }

    private static <D> R<D> parse(HttpStatus status) {
        R<D> result = new R<>();
        result.setStatus(String.valueOf(status.value()));
        result.setSuccess(true);
        result.setMessage(status.getReasonPhrase());
        return result;
    }

    private static <D> R<D> parse(ErrorStatus status) {
        R<D> result = new R<>();
        result.setStatus(status.getCode());
        result.setSuccess(false);
        result.setMessage(status.getMessage());
        return result;
    }

    public static <D> R<D> ok(D data) {
        R<D> result = parse(HttpStatus.OK);
        result.setData(data);
        return result;
    }

    public static <D> R<D> ok(D data, String message) {
        R<D> result = ok(data);
        result.setMessage(message);
        return result;
    }

    public static <D> R<D> fail(ErrorStatus status) {
        R<D> result = parse(status);
        return result;
    }

    public static <D> R<D> fail(ErrorStatus status, String message) {
        R<D> result = fail(status);
        result.setMessage(message);
        return result;
    }

    public static <D> R<D> noContent() {
        return parse(HttpStatus.NO_CONTENT);
    }

    public static <D> R<D> noContent(String message) {
        R<D> result = noContent();
        result.setMessage(message);
        return result;
    }
}
