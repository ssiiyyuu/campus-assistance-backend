package com.siyu.flowable.entity.vo;

import com.siyu.common.domain.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface CampusReportEventVO {
    @Data
    @ApiModel("CampusReportEventVO in")
    class In {
		@NotBlank
	    private String parentId;

	    @ApiModelProperty("校园上报事件名")
		@NotBlank
	    private String name;

	    @ApiModelProperty("校园上报事件登记")
		@NotBlank
	    private String level;

    }

    @Data
    @ApiModel("CampusReportEventVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;

		private String parentId;

	    @ApiModelProperty("校园上报事件名")
	    private String name;

	    @ApiModelProperty("校园上报事件登记")
	    private String level;

    }

    @Data
    @ApiModel("CampusReportEventVO condition")
    class Condition {
	    @ApiModelProperty("校园上报事件名")
	    private String name;

	    @ApiModelProperty("校园上报事件登记")
	    private String level;

    }

    @Data
    @ApiModel("CampusReportEventVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;

		private String parentId;

	    @ApiModelProperty("校园上报事件名")
	    private String name;

	    @ApiModelProperty("校园上报事件登记")
	    private String level;

    }

	@Data
	@ApiModel("CategoryVO tree")
	class Tree implements TreeNode<Tree, String> {
		@ApiModelProperty("id")
		private String id;

		private String parentId;

		@ApiModelProperty("校园上报事件名")
		private String name;

		@ApiModelProperty("校园上报事件登记")
		private String level;

		private List<Tree> childList;
	}
}
