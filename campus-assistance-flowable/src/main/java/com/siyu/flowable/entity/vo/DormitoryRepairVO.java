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

public interface DormitoryRepairVO {
    @Data
    @ApiModel("创建宿舍维修流程")
    class Create {
        @NotBlank
        @ApiModelProperty(value = "报修类型")
        private String type;

        @NotBlank
        @ApiModelProperty(value = "报修描述")
        private String description;

        @NotBlank
        @ApiModelProperty(value = "报修地址")
        private String createLocation;

        @ApiModelProperty(value = "创建附件")
        private List<AttachmentDTO> attachments;
    }

    @Data
    @ApiModel("办理宿舍维修流程")
    class Transact {
        @Valid
        @NotNull
        @ApiModelProperty(value = "办理内容")
        CommentDTO comment;

        @ApiModelProperty(value = "办理附件")
        private List<AttachmentDTO> attachments;
    }

    @Data
    @ApiModel("宿舍维修详情")
    class Detail {
        @ApiModelProperty(value = "宿舍维修信息")
        Create create;

        @ApiModelProperty(value = "宿舍维修办理结果")
        Transact transact;

        @ApiModelProperty(value = "宿舍维修状态")
        private String status;

        @ApiModelProperty(value = "办理人")
        private SysUserBaseDTO transactor;

        @ApiModelProperty(value = "发起人")
        private SysUserBaseDTO initiator;

        @ApiModelProperty(value = "发起人评分")
        private Integer score;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @ApiModelProperty(value = "办理时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime transactTime;
    }

    @Data
    @ApiModel("我创建的宿舍维修")
    class Created {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "宿舍维修状态")
        private String status;

        @NotBlank
        @ApiModelProperty(value = "报修类型")
        private String type;

        @ApiModelProperty(value = "事件描述")
        private String description;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    @ApiModel("我受理的宿舍维修")
    class Assigned {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "当前任务ID")
        private String taskId;

        @ApiModelProperty(value = "宿舍维修状态")
        private String status;

        @NotBlank
        @ApiModelProperty(value = "报修类型")
        private String type;

        @ApiModelProperty(value = "事件描述")
        private String description;

        @ApiModelProperty(value = "发起人")
        private String initiator;

        @ApiModelProperty(value = "发起人评分")
        private Integer score;

        @ApiModelProperty(value = "发起时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @ApiModelProperty(value = "是否已办")
        private Boolean isAssigned;
    }
}
