package com.siyu.flowable.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDTO {

    @ApiModelProperty(hidden = true)
    private String type;

    @NotBlank
    @ApiModelProperty("审批内容")
    private String content;

}
