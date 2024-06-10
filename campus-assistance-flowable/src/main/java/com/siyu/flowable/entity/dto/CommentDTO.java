package com.siyu.flowable.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    @ApiModelProperty(hidden = true)
    private String type;

    @NotBlank
    @ApiModelProperty("审批内容")
    private String content;

}
