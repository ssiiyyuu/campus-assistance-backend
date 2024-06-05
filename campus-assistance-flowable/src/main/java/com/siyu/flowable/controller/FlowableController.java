package com.siyu.flowable.controller;


import com.siyu.common.domain.PaginationQuery;
import com.siyu.common.domain.PaginationResult;
import com.siyu.common.domain.R;
import com.siyu.common.enums.ErrorStatus;
import com.siyu.common.exception.BusinessException;
import com.siyu.common.utils.WebUtils;
import com.siyu.flowable.entity.vo.FlowableVO;
import com.siyu.flowable.service.FlowableService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.flowable.engine.repository.Deployment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Api(tags = "工作流——基础服务")
@RestController
@RequestMapping("/flowable/base")
public class FlowableController{

    @Autowired
    private FlowableService flowableService;

    @ApiOperation(value = "部署流程图")
    @PostMapping("/deployment/deploy")
    public R<String> deployDeployment(@RequestPart("file") MultipartFile file) {
        Deployment deployment = flowableService.deployDeployment(file);
        return R.ok("部署id[" + deployment.getId() + "]");
    }

    @ApiOperation(value = "流程部署分页查询")
    @GetMapping("/deployment/page")
    public R<PaginationResult<FlowableVO.Deployment>> pageDeployment(@RequestBody PaginationQuery<?> query) {
        PaginationResult<FlowableVO.Deployment> page = flowableService.pageDeployment(query);
        return R.ok(page);
    }

    @ApiOperation(value = "删除流程部署")
    @DeleteMapping ("/deployment/{id}")
    public R<String> deleteDeployment(@PathVariable String id) {
        flowableService.deleteDeployment(id);
        return R.ok("流程部署删除成功");
    }

    @ApiOperation(value = "显示流程图")
    @GetMapping(value = "/process/diagram/{processId}")
    public void diagramProcess(@PathVariable String processId, HttpServletResponse httpServletResponse) {
        InputStream inputStream = flowableService.getDiagram(processId);
        try {
            WebUtils.writePNG2Response(inputStream, httpServletResponse);
        } catch (IOException e) {
            throw new BusinessException(ErrorStatus.IO_ERROR);
        }
    }

    @ApiOperation(value = "手动删除流程实例")
    @DeleteMapping("/process/{processId}")
    public R<?> deleteProcess(@PathVariable String processId) {
        flowableService.deleteProcess(processId);
        return R.noContent();
    }

    @ApiOperation(value = "流程实例阶段信息")
    @GetMapping("/process/stage/{processId}")
    public R<List<FlowableVO.Stage>> stageProcess(@PathVariable String processId) {
        List<FlowableVO.Stage> result = flowableService.stageProcess(processId);
        return R.ok(result);
    }

    @ApiOperation(value = "流程实例阶段分页查询")
    @PostMapping("/process/page")
    public R<PaginationResult<FlowableVO.Table>> pageProcess(@RequestBody PaginationQuery<FlowableVO.Condition> query) {
        PaginationResult<FlowableVO.Table>  result = flowableService.pageProcess(query);
        return R.ok(result);
    }

}
