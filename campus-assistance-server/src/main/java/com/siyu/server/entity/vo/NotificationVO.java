package com.siyu.server.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.util.List;

public interface NotificationVO {
    @Data
    @ApiModel("NotificationVO in")
    class In {
		@NotEmpty
		@ApiModelProperty("消息传递去(若为'0'则广播到所有)")
		private List<String> toList;

		@ApiModelProperty("消息内容")
		@NotBlank
		private String content;


    }

    @Data
    @ApiModel("NotificationVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;

		@ApiModelProperty("消息类型(SYSTEM，ADMIN，COUNSELOR)")
		private String type;

		@ApiModelProperty("消息来自于")
		private String from;

		@ApiModelProperty("消息传递去(若为'0'则广播到所有)")
		private String to;

		@ApiModelProperty("消息内容")
		private String content;

		@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createTime;


    }

    @Data
    @ApiModel("NotificationVO condition")
    class Condition {
		@ApiModelProperty("消息类型(SYSTEM，ADMIN，COUNSELOR)")
		private String type;
    }

    @Data
    @ApiModel("NotificationVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;

		@ApiModelProperty("消息类型(SYSTEM，ADMIN，COUNSELOR)")
		private String type;

		@ApiModelProperty("消息来自于")
		private String from;

		@ApiModelProperty("消息传递去(若为'0'则广播到所有)")
		private String to;

		@ApiModelProperty("是否已读")
		private Boolean read;

		@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime createTime;
    }
}
