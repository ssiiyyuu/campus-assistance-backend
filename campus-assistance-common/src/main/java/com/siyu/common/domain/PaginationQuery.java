package com.siyu.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationQuery<T> {

    @ApiModelProperty(value = "页码", example = "1")
    private Long pageNum;

    @ApiModelProperty(value = "每页记录", example = "10")
    private Long pageSize;

    @ApiModelProperty(value = "查询条件")
    private T condition;

    @ApiModelProperty(value = "排序规则")
    private List<Sort> sorts;

}
