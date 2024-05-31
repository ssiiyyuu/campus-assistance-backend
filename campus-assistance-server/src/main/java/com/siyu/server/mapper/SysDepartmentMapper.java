package com.siyu.server.mapper;

import com.siyu.common.domain.entity.SysDepartment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.siyu.server.entity.dto.SysDepartmentBaseDTO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:57
 */
public interface SysDepartmentMapper extends BaseMapper<SysDepartment> {

    /**
     * 返回形如江西理工大学-信息工程学院-计算机213班
     * @param departmentCode
     * @return 部门全名
     */
    String getFullDepartmentName(String departmentCode);

    String getMaxCodeByLevel(int level);

    String getMaxCodeByParentId(String parentId);

    SysDepartmentBaseDTO selectBaseDepartmentByCode(String departmentCode);
}
