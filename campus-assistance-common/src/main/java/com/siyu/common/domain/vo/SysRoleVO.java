package com.siyu.common.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

public interface SysRoleVO {

	@Data
	@ApiModel("SysRoleVO assign")
	class Assign {
		@ApiModelProperty(value = "id", hidden = true)
		private String id;

		@ApiModelProperty("角色名称")
		private String roleName;

		@ApiModelProperty("是否分配")
		private Boolean assigned;
	}

    @Data
    @ApiModel("SysRoleVO in")
    class In {
		@NotBlank
	    @ApiModelProperty("角色名称")
	    private String roleName;

		@NotBlank
	    @ApiModelProperty("角色编码")
	    private String roleCode;

	    @ApiModelProperty("备注")
	    private String remark;

    }

    @Data
    @ApiModel("SysRoleVO out")
    class Out {
        @ApiModelProperty("id")
        private String id;
        
	    @ApiModelProperty("角色名称")
	    private String roleName;

	    @ApiModelProperty("角色编码")
	    private String roleCode;

	    @ApiModelProperty("备注")
	    private String remark;

    }

    @Data
    @ApiModel("SysRoleVO condition")
    class Condition {
        
	    @ApiModelProperty("角色名称")
	    private String roleName;

	    @ApiModelProperty("角色编码")
	    private String roleCode;

    }

    @Data
    @ApiModel("SysRoleVO table")
    class Table {
        @ApiModelProperty("id")
        private String id;
        
	    @ApiModelProperty("角色名称")
	    private String roleName;

	    @ApiModelProperty("角色编码")
	    private String roleCode;

	    @ApiModelProperty("备注")
	    private String remark;

    }
}
