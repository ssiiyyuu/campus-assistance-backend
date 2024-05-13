package com.siyu.server.service;

import com.siyu.common.domain.entity.SysDepartment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:57
 */
public interface SysDepartmentService extends IService<SysDepartment> {

    void deleteTree(String id);
}
