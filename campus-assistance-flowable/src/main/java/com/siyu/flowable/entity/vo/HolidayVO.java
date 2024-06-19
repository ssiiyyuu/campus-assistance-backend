package com.siyu.flowable.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siyu.common.domain.dto.SysUserBaseDTO;
import com.siyu.flowable.entity.dto.AttachmentDTO;
import com.siyu.flowable.entity.dto.CommentDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public interface HolidayVO {

    @Data
    @ApiModel("创建请假流程")
    class Create {
        @NotBlank
        @ApiModelProperty(value = "请假类型")
        private String type;

        @NotBlank
        @Length(min = 10)
        @ApiModelProperty(value = "请假原因")
        private String reason;

        @NotBlank
        @ApiModelProperty(value = "办理人Id")
        private String assigneeId;

        @NotNull
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期开始时间")
        private LocalDateTime startTime;

        @NotNull
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期结束时间")
        private LocalDateTime endTime;

        @ApiModelProperty(value = "附件")
        private List<AttachmentDTO> attachments;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel("请假流程销假")
    class Destroy {

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "销假时间", hidden = true)
        private LocalDateTime destroyTime;
    }

    @Data
    @ApiModel("我创建的请假流程列表项")
    class Created {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "请假流程状态")
        private String status;

        @ApiModelProperty(value = "请假类型")
        private String type;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期申请时间")
        private LocalDateTime createTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期开始时间")
        private LocalDateTime startTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期结束时间")
        private LocalDateTime endTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "销假时间", hidden = true)
        private LocalDateTime destroyTime;
    }
    @Data
    @ApiModel("我受理的请假流程列表项")
    class Assigned {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "请假流程状态")
        private String status;

        @ApiModelProperty(value = "请假类型")
        private String type;

        @ApiModelProperty(value = "发起人")
        private String initiator;

        @ApiModelProperty(value = "是否已办")
        private Boolean isAssigned;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期申请时间")
        private LocalDateTime creatTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期开始时间")
        private LocalDateTime startTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "假期结束时间")
        private LocalDateTime endTime;

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        @ApiModelProperty(value = "销假时间", hidden = true)
        private LocalDateTime destroyTime;
    }

    @Data
    @ApiModel("请假流程详情")
    class Detail {

        @ApiModelProperty(value = "请假流程状态")
        private String status;

        @ApiModelProperty(value = "请假信息")
        private Create create;

        @ApiModelProperty(value = "销假信息")
        private Destroy destroy;

        @ApiModelProperty(value = "发起人")
        private SysUserBaseDTO initiator;

        @ApiModelProperty(value = "办理意见")
        private List<CommentDTO> comments;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

    }

    @Data
    @ApiModel("请假流程分页条件")
    class Condition {

    }


}
