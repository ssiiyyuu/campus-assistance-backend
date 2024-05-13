package com.siyu.server.service.impl;

import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Category;
import com.siyu.server.entity.vo.CategoryVO;
import com.siyu.server.mapper.CategoryMapper;
import com.siyu.server.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 09:00:20
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public CategoryVO.Table setTableBaseInfo(Category category) {
        CategoryVO.Table table = BeanUtils.copyProperties(category, new CategoryVO.Table());
        table.setParent(categoryMapper.selectById(category.getParentId()).getName());
        return table;
    }

    @Override
    public CategoryVO.Out setOutBaseInfo(Category category) {
        CategoryVO.Out out = BeanUtils.copyProperties(category, new CategoryVO.Out());
        out.setParent(categoryMapper.selectById(category.getParentId()).getName());
        return out;
    }
}
