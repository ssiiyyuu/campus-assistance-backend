package com.siyu.server.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public interface InformationVO {
    @Data
    @ApiModel("InformationVO in")
    class In {
        @ApiModelProperty(value = "id", hidden = true)
        private String id;
		private String categoryId;
	    private String title;
	    private String cover;
	    private String content;
    }

    @Data
    @ApiModel("InformationVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;
		private String categoryId;
		private String category;
	    private String title;
	    private String cover;
		private String authorId;
		private String author;
	    private String content;
	    private Integer visits;
	    private String status;
	    private String departmentCode;
		private String department;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime publishTime;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime createTime;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime updateTime;
    }

    @Data
    @ApiModel("InformationVO condition")
    class Condition {
		private String categoryId;
	    private String title;
	    private String content;
	    private String status;
	    private String departmentCode;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private List<LocalDateTime> publishTime;
    }

    @Data
    @ApiModel("InformationVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;
		private String categoryId;
		private String category;
	    private String title;
	    private String cover;
		private String authorId;
		private String author;
	    private Integer visits;
	    private String status;
	    private String departmentCode;
		private String department;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime publishTime;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime createTime;
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
		private LocalDateTime updateTime;
    }
}
