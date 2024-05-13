package com.siyu.server.service.impl;

import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.InformationStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Information;
import com.siyu.server.entity.vo.InformationVO;
import com.siyu.server.mapper.CategoryMapper;
import com.siyu.server.mapper.InformationMapper;
import com.siyu.server.mapper.SysDepartmentMapper;
import com.siyu.server.mapper.SysUserMapper;
import com.siyu.server.service.CategoryService;
import com.siyu.server.service.InformationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.siyu.shiro.entity.ShiroUser;
import com.siyu.shiro.utils.ShiroUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 08:55:29
 */
@Service
public class InformationServiceImpl extends ServiceImpl<InformationMapper, Information> implements InformationService {

    @Autowired
    private InformationMapper informationMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SysDepartmentMapper sysDepartmentMapper;

    @Override
    public void create(InformationVO.In in) {
        ShiroUser user = ShiroUtils.getCurrentUser();
        Information information = BeanUtils.copyProperties(in, new Information());
        information.setAuthorId(user.getId());
        information.setDepartmentCode(user.getDepartmentCode());
        information.setVisits(0);
        information.setStatus(InformationStatus.CREATED.name());
        informationMapper.insert(information);
    }

    @Override
    public void publish(String id) {
        Information information = informationMapper.selectById(id);
        if(!InformationStatus.CREATED.name().equals(information.getStatus())) {
            throw new BusinessException(ErrorStatus.STATUS_ERROR, "当前状态为: " + information.getStatus() + ", 不允许发布");
        }
        information.setStatus(InformationStatus.PUBLISHED.name());
        information.setPublishTime(LocalDateTime.now());
        informationMapper.updateById(information);
    }

    @Override
    public void offline(String id) {
        Information information = informationMapper.selectById(id);
        if(!InformationStatus.PUBLISHED.name().equals(information.getStatus())) {
            throw new BusinessException(ErrorStatus.STATUS_ERROR, "当前状态为: " + information.getStatus() + ", 不允许下线");
        }
        information.setStatus(InformationStatus.OFFLINE.name());
        informationMapper.updateById(information);
    }
    @Override
    public InformationVO.Table setTableBaseInfo(Information information) {
        InformationVO.Table table = BeanUtils.copyProperties(information, new InformationVO.Table());
        table.setAuthor(sysUserMapper.selectById(table.getAuthorId()).getNickname());
        table.setCategory(categoryMapper.selectById(table.getCategoryId()).getName());
        table.setDepartment(sysDepartmentMapper.getFullDepartmentName(table.getDepartmentCode()));
        return table;
    }
    @Override
    public InformationVO.Out setOutBaseInfo(Information information) {
        InformationVO.Out out = BeanUtils.copyProperties(information, new InformationVO.Out());
        out.setAuthor(sysUserMapper.selectById(out.getAuthorId()).getNickname());
        out.setCategory(categoryMapper.selectById(out.getCategoryId()).getName());
        out.setDepartment(sysDepartmentMapper.getFullDepartmentName(out.getDepartmentCode()));
        return out;
    }
}
