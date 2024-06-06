package com.siyu.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.Information;
import com.siyu.server.entity.vo.InformationVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author MybatisPlusGenerator
 * @since 2024-05-11 08:55:29
 */
public interface InformationService extends IService<Information> {

    void create(InformationVO.In in);

    void publish(String id);

    void offline(String id);

    InformationVO.Table setTableBaseInfo(Information information);

    InformationVO.Out setOutBaseInfo(Information information);

    Information load(String id);

    void createCampusDynamics(InformationVO.In in);

    void createCampusAnnouncement(InformationVO.In in);

    void checkAuthorPublish(String id);

    void checkAuthorOffline(String id);

    void checkAuthorRemove(List<String> ids);

    void checkAuthorUpdate(String id, InformationVO.In in);
}
