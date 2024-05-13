package com.siyu.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Sort {

    @ApiModelProperty(value = "数据库内字段")
    private String fieldName;

    @ApiModelProperty(value = "asc升序, desc降序；不填则默认为asc", allowableValues = "asc, desc")
    private String direction;

    public static boolean isAsc(Sort sort) {
        return !isDesc(sort);
    }

    public static boolean isDesc(Sort sort) {
        return "desc".equalsIgnoreCase(sort.getDirection());
    }
}