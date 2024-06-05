package com.siyu.flowable.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.siyu.flowable.entity.dto.CommentDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface FlowableVO {

    @Data
    @ApiModel("流程部署VO")
    class Deployment {

        private String id;

        private String name;

        private Date deploymentTime;

        private String key;

        private String engineVersion;
    }

    @Data
    @ApiModel("流程阶段节点")
    class Stage {
        @ApiModelProperty(value = "节点名")
        private String name;

        @ApiModelProperty(value = "审批人")
        private String assignee;

        @ApiModelProperty(value = "接受时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime startTime;

        @ApiModelProperty(value = "办结时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;

        @ApiModelProperty(value = "耗时")
        private Long duration;

        @ApiModelProperty(value = "审批信息")
        private List<CommentDTO> comments;
    }

    @Data
    @ApiModel("流程列表项")
    class Table {
        @ApiModelProperty(value = "流程ID")
        private String processId;

        @ApiModelProperty(value = "流程定义Key")
        private String definitionKey;

        @ApiModelProperty(value = "当前节点ID")
        private String taskId;

        @ApiModelProperty(value = "当前节点名")
        private String taskName;

        @ApiModelProperty(value = "发起人")
        private String initiator;

        @ApiModelProperty(value = "当前审批人")
        private String assignee;

        @ApiModelProperty(value = "流程开始时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime creatTime;

        @ApiModelProperty(value = "流程结束时间")
        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime endTime;
    }


    @Data
    @ApiModel("流程查询条件")
    class Condition {
        @ApiModelProperty(value = "流程定义key")
        private String definitionKey;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
        private List<LocalDateTime> startTime;
    }
}
