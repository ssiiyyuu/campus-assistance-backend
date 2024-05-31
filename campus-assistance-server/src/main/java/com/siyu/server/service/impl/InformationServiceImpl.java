package com.siyu.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.enums.InformationStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.BeanUtils;
import com.siyu.server.entity.Category;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    @Transactional
    @Override
    public Information loadSystem(String id) {
        Information information = informationMapper.selectById(id);
        Category category = categoryMapper.selectById(information.getCategoryId());
        if(category == null || (!category.getName().equals("系统公告") && !category.getName().equals("系统动态"))) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        information.setVisits(information.getVisits()+1);
        informationMapper.updateById(information);
        return information;
    }

    @Transactional
    @Override
    public Information loadCampus(String id) {
        Information information = informationMapper.selectById(id);
        Category category = categoryMapper.selectById(information.getCategoryId());
        if(category == null || (!category.getName().equals("校园公告") && !category.getName().equals("校园动态"))) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        information.setVisits(information.getVisits()+1);
        informationMapper.updateById(information);
        return information;
    }

    @Override
    public void createCampusDynamics(InformationVO.In in) {
        Category category = categoryMapper.selectById(in.getCategoryId());
        if(category == null || !category.getName().equals("校园动态")) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        create(in);
    }

    @Override
    public void createCampusAnnouncement(InformationVO.In in) {
        Category category = categoryMapper.selectById(in.getCategoryId());
        if(category == null || !category.getName().equals("校园公告")) {
            throw new BusinessException(ErrorStatus.QUERY_ERROR);
        }
        create(in);
    }

    @Override
    public void checkAuthorPublish(String id) {
        Information information = informationMapper.selectById(id);
        if(!ShiroUtils.getCurrentUserId().equals(information.getAuthorId())) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR);
        }
        if(!InformationStatus.CREATED.name().equals(information.getStatus())) {
            throw new BusinessException(ErrorStatus.STATUS_ERROR, "当前状态为: " + information.getStatus() + ", 不允许发布");
        }
        information.setStatus(InformationStatus.PUBLISHED.name());
        information.setPublishTime(LocalDateTime.now());
        informationMapper.updateById(information);
    }

    @Override
    public void checkAuthorOffline(String id) {
        Information information = informationMapper.selectById(id);
        if(!ShiroUtils.getCurrentUserId().equals(information.getAuthorId())) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR);
        }
        if(!InformationStatus.PUBLISHED.name().equals(information.getStatus())) {
            throw new BusinessException(ErrorStatus.STATUS_ERROR, "当前状态为: " + information.getStatus() + ", 不允许下线");
        }
        information.setStatus(InformationStatus.OFFLINE.name());
        informationMapper.updateById(information);
    }

    @Override
    public void checkAuthorUpdate(String id, InformationVO.In in) {
        Information information = informationMapper.selectById(id);
        if(!ShiroUtils.getCurrentUserId().equals(information.getAuthorId())) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR);
        }
        Information info = BeanUtils.copyProperties(in, information);
        info.setUpdateTime(null);
        informationMapper.updateById(info);
    }

    @Override
    public void checkAuthorRemove(List<String> ids) {
        List<Information> list = informationMapper.selectList(new LambdaQueryWrapper<Information>()
                .select(Information::getId, Information::getAuthorId)
                .in(Information::getId, ids));
        boolean flag = list.stream()
                .anyMatch(item -> !ShiroUtils.getCurrentUserId().equals(item.getAuthorId()));
        if(flag) {
            throw new BusinessException(ErrorStatus.AUTHOR_ERROR);
        }
        informationMapper.deleteBatchIds(ids);
    }
}
