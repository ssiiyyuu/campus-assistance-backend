package com.siyu.server.entity.vo;

import com.siyu.common.domain.TreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

public interface SysPermissionVO {
    @Data
    @ApiModel("SysPermissionVO in")
    class In {
        @ApiModelProperty(value = "id", hidden = true)
        private String id;
        
	    @ApiModelProperty("所属上级")
	    private String parentId;

	    @ApiModelProperty("名称")
	    private String name;

	    @ApiModelProperty("类型(1:菜单,2:按钮)")
	    private Integer type;

	    @ApiModelProperty("权限值")
	    private String permissionValue;

	    @ApiModelProperty("访问路径")
	    private String path;

	    @ApiModelProperty("组件路径")
	    private String component;

	    @ApiModelProperty("图标")
	    private String icon;

	    @ApiModelProperty("状态(0:禁止,1:正常)")
	    private Integer status;

    }

    @Data
    @ApiModel("SysPermissionVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;
        
	    @ApiModelProperty("所属上级")
	    private String parentId;

	    @ApiModelProperty("名称")
	    private String name;

	    @ApiModelProperty("类型(1:菜单,2:按钮)")
	    private Integer type;

	    @ApiModelProperty("权限值")
	    private String permissionValue;

	    @ApiModelProperty("访问路径")
	    private String path;

	    @ApiModelProperty("组件路径")
	    private String component;

	    @ApiModelProperty("图标")
	    private String icon;

	    @ApiModelProperty("状态(0:禁止,1:正常)")
	    private Integer status;

    }

    @Data
    @ApiModel("SysPermissionVO condition")
    class Condition {
	    @ApiModelProperty("名称")
	    private String name;

	    @ApiModelProperty("类型(1:菜单,2:按钮)")
	    private Integer type;

	    @ApiModelProperty("状态(0:禁止,1:正常)")
	    private Integer status;

    }

    @Data
    @ApiModel("SysPermissionVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;
        
	    @ApiModelProperty("所属上级")
	    private String parentId;

	    @ApiModelProperty("名称")
	    private String name;

	    @ApiModelProperty("类型(1:菜单,2:按钮)")
	    private Integer type;

	    @ApiModelProperty("权限值")
	    private String permissionValue;

	    @ApiModelProperty("访问路径")
	    private String path;

	    @ApiModelProperty("组件路径")
	    private String component;

	    @ApiModelProperty("图标")
	    private String icon;

	    @ApiModelProperty("状态(0:禁止,1:正常)")
	    private Integer status;

    }

	@Data
	@ApiModel("SysPermissionVO tree")
	class Tree implements TreeNode<Tree, String> {
		@ApiModelProperty("id")
		private String id;

		@ApiModelProperty("所属上级")
		private String parentId;

		@ApiModelProperty("名称")
		private String name;

		@ApiModelProperty("类型(1:菜单,2:按钮)")
		private Integer type;

		@ApiModelProperty("权限值")
		private String permissionValue;

		@ApiModelProperty("访问路径")
		private String path;

		@ApiModelProperty("组件路径")
		private String component;

		@ApiModelProperty("图标")
		private String icon;

		@ApiModelProperty("状态(0:禁止,1:正常)")
		private Integer status;

		@ApiModelProperty("是否分配")
		private Boolean assigned;

		@ApiModelProperty("子节点")
		private List<Tree> childList;
	}
}
