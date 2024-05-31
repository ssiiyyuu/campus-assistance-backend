package com.siyu.server.entity.vo;

import com.siyu.common.domain.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

public interface SysDepartmentVO {
    @Data
    @ApiModel("SysDepartmentVO in")
    class In {
        @NotBlank
        private String parentId;

        @NotBlank
        private String name;

    }

    @Data
    @ApiModel("SysDepartmentVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String parent;

	    private String name;

	    private String remark;

	    private Integer level;

	    private String code;

    }

    @Data
    @ApiModel("SysDepartmentVO condition")
    class Condition {
	    private String name;

	    private Integer level;
    }

    @Data
    @ApiModel("SysDepartmentVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String parent;

	    private String name;

	    private String remark;

	    private Integer level;

	    private String code;

    }

    @Data
    @ApiModel("SysDepartmentVO tree")
    class Tree implements TreeNode<Tree, String> {
        @ApiModelProperty("id")
        private String id;

        private String parentId;

        private String name;

        private String remark;

        private Integer level;

        private String code;

        private List<Tree> childList;
    }
}
