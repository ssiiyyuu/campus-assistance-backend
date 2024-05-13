package com.siyu.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.domain.entity.SysDepartment;
import com.siyu.common.domain.entity.SysPermission;
import com.siyu.server.mapper.SysDepartmentMapper;
import com.siyu.server.service.SysDepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-09 02:06:57
 */
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> implements SysDepartmentService {

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public boolean save(SysDepartment entity) {
        initCodeAndLevel(entity);
        //隶属于同一党组织的层级不允许重名
        List<SysDepartment> departments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getParentId, entity.getParentId())
                .eq(SysDepartment::getName, entity.getName()));
        for(SysDepartment department : departments) {
            if(!department.getDeleted()) {
                return false;
            }
        }
        return sysDepartmentMapper.insert(entity) > 0;
    }

    @Override
    public boolean updateById(SysDepartment entity) {
        //隶属于同一党组织的层级不允许重名
        List<SysDepartment> departments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getParentId, entity.getParentId())
                .eq(SysDepartment::getName, entity.getName()));
        if(departments.isEmpty()) {
            return super.updateById(entity);
        }
        if(departments.get(0).getId().equals(entity.getId())) {
            return super.updateById(entity);
        }
        return false;
    }


    @Override
    public void deleteTree(String id) {
        List<String> ids = new ArrayList<>();
        getChildIdsByParentId(id, ids);
        ids.add(id);
        sysDepartmentMapper.deleteBatchIds(ids);
    }


    private void getChildIdsByParentId(String id, List<String> ids) {
        //根据id查询childIds
        List<SysDepartment> departments = sysDepartmentMapper.selectList(new LambdaQueryWrapper<SysDepartment>()
                .eq(SysDepartment::getParentId, id)
                .select(SysDepartment::getId));
        departments.forEach(permission -> {
            ids.add(permission.getId());
            //递归查询所有子孙节点
            getChildIdsByParentId(permission.getId(), ids);
        });
    }

    /**
     * 为党组织初始化层级与编码
     *
     * @param entity
     */
    private void initCodeAndLevel(SysDepartment entity) {
        SysDepartment parent = sysDepartmentMapper.selectById(entity.getParentId());
        if(parent == null) {
            entity.setLevel(1);
            entity.setParentId("0");
        } else {
            entity.setLevel(parent.getLevel() + 1);
        }
        generateCode(entity, parent);
        entity.setId(entity.getCode().replaceAll("-", ""));
    }

    /**
     * 为党组织初始化编码以区分上下级关系
     *
     * @param entity
     * @param parent
     */
    private void generateCode(SysDepartment entity, SysDepartment parent) {
        String parentCode = (parent == null) ? "" : parent.getCode();
        String maxCode = (parent == null) ? sysDepartmentMapper.getMaxCodeByLevel(1) : sysDepartmentMapper.getMaxCodeByParentId(parent.getId());
        int maxId;
        if (maxCode == null) {
            maxId = (int)Math.pow(10, entity.getLevel());
        } else {
            char[] chars = maxCode.toCharArray();
            int searchEndIndex = chars.length-2;  //倒数第2个开始提取
            int i = searchEndIndex;
            for (; i >= 0; i--) {
                if (chars[i] == '-') {
                    break;
                }
            }
            maxId = Integer.parseInt(new String(chars, i+1, searchEndIndex-i));
        }
        String levelCode = parentCode + (maxId + 1) + "-";  //本级编码，初始1
        entity.setCode(levelCode);
    }
}
