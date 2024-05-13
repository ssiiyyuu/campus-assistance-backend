package com.siyu.server.service;

import com.siyu.server.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.Category;
import com.siyu.server.entity.vo.CategoryVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 09:00:20
 */
public interface CategoryService extends IService<Category> {
    
    CategoryVO.Table setTableBaseInfo(Category category);

    CategoryVO.Out setOutBaseInfo(Category category);
}
