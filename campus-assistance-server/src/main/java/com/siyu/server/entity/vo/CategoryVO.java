package com.siyu.server.entity.vo;

import com.siyu.common.domain.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface CategoryVO {
    @Data
    @ApiModel("CategoryVO in")
    class In {
        @NotBlank
        private String parentId;

        @NotBlank
	    private String name;

	    private String remark;

    }

    @Data
    @ApiModel("CategoryVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String parent;

	    private String name;

	    private String remark;

    }

    @Data
    @ApiModel("CategoryVO condition")
    class Condition {
	    private String name;
    }

    @Data
    @ApiModel("CategoryVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String parent;

	    private String name;

	    private String remark;
    }

    @Data
    @ApiModel("CategoryVO tree")
    class Tree implements TreeNode<Tree, String> {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String name;

        private String remark;

        private List<Tree> childList;
    }
}
