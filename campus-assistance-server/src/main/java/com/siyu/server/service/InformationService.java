package com.siyu.server.service;

import com.siyu.server.entity.Information;
import com.baomidou.mybatisplus.extension.service.IService;
import com.siyu.server.entity.vo.InformationVO;

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
}
