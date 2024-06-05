package com.siyu.common.domain;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@ApiModel("分页查询结果")
public class PaginationResult <T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "页码", example = "1")
    private Long pageNum;

    @ApiModelProperty(value = "每页记录", example = "10")
    private Long pageSize;

    @ApiModelProperty(value = "总的记录数", example = "23")
    private Long total;

    @ApiModelProperty(value = "总页数", example = "6")
    private Long totalPages;

    @ApiModelProperty(value = "结果数据列表")
    private List<T> data;

    public static <T> PaginationResult<T> of(Page<T> page) {
        return new PaginationResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), page.getRecords());
    }
    public static <T> PaginationResult<T> of(Page<?> page, List<T> data) {
        return new PaginationResult<>(page.getCurrent(), page.getSize(), page.getTotal(), page.getPages(), data);
    }
}
