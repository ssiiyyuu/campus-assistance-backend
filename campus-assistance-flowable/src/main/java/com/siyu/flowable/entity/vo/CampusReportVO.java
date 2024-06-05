package com.siyu.flowable.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siyu.common.domain.dto.SysUserBaseDTO;
import com.siyu.flowable.entity.dto.AttachmentDTO;
import com.siyu.flowable.entity.dto.CommentDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public interface CampusReportVO {
    @Data
    @ApiModel("创建校园上报流程")
    class Create {
        @NotBlank
        @ApiModelProperty(value = "上报事件Id")
        private String eventId;

        @ApiModelProperty(value = "上报事件", hidden = true)
        private String event;

        @ApiModelProperty(value = "上报事件level", hidden = true)
        private String level;

        @NotBlank
        @ApiModelProperty(value = "事件描述")
        private String description;

        @NotBlank
        @ApiModelProperty(value = "上报事件地址")
        private String createLocation;

        @ApiModelProperty(value = "创建附件")
        private List<AttachmentDTO> attachments;
    }

    @Data
    @ApiModel("办理校园上报流程")
    class Transact {

        @Valid
        @NotNull
        @ApiModelProperty(value = "办理内容")
        CommentDTO comment;

        @ApiModelProperty(value = "办理附件")
        private List<AttachmentDTO> attachments;
    }

    @Data
    @ApiModel("校园上报流程详情")
    class Detail {
        @ApiModelProperty(value = "校园上报信息")
        Create create;

        @ApiModelProperty(value = "校园上报办理结果")
        Transact transact;

        @ApiModelProperty(value = "校园上报流程状态")
        private String status;

        @ApiModelProperty(value = "发起人")
        private SysUserBaseDTO initiator;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    @ApiModel("我创建的校园上报流程")
    class Created {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "校园上报流程状态")
        private String status;

        @ApiModelProperty(value = "校园上报事件名")
        private String event;

        @ApiModelProperty(value = "校园上报事件等级")
        private String level;

        @ApiModelProperty(value = "事件描述")
        private String description;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    @ApiModel("我受理的校园上报流程")
    class Assigned {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "校园上报流程状态")
        private String status;

        @ApiModelProperty(value = "校园上报事件名")
        private String event;

        @ApiModelProperty(value = "校园上报事件等级")
        private String level;

        @ApiModelProperty(value = "事件描述")
        private String description;

        @ApiModelProperty(value = "发起人")
        private String initiator;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @ApiModelProperty(value = "是否已办")
        private Boolean isAssigned;
    }
}
