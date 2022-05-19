package com.dnt.data.standard.server.model.mould.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.controller.BaseController;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeValueRequest;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeValueService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/* *
 * @desc 通用业务属性value值
 * @Return:
 * @author: ZZP
 * @date:  2022/5/18 15:21
 * @Version V1.1.0
 */

@RestController
@RequestMapping("/v1/dw/CurrencyAttributeValue")
@Api(value = "dwCurrencyAttributeValue", tags = "通用业务属Value值性接口")
@Slf4j
public class DwCurrencyAttributeValueController extends BaseController {
    @Autowired
    private DwCurrencyAttributeValueService dwCurrencyAttributeValueService;

    //获取数通用业务属性value值树形列表
    @ApiOperation(value = "获取通用业务属性value值树形列表", notes = "获取通用业务属性value值树形列表接口")
    @PostMapping(value = {"/tree/list"})
    public R treeList(@RequestHeader Long projectId, @RequestBody DwCurrencyAttributeValueRequest request) {
        log.info("DwCurrencyAttributeValueController-->pageList 获取通用业务属性value值树形列表");
        request.setProjectId(projectId);
        List list = dwCurrencyAttributeValueService.selectCurrencyAttributeValueTree(request);
        return Result.ok(list);
    }


    /**
     * 查看详情
     **/
    @ApiOperation(value = "查看通用业务属性value值详情", notes = "查看通用业务属性value值情接口")
    @GetMapping(value = {"/detailCurrencyAttributeValue/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "通用业务属性value值ID", required = true, dataType = "Long")
    })
    public R detailCurrencyAttribute(@PathVariable("id") Long id) {
        log.info("DwCurrencyAttributeValueController--> detailCurrencyAttribute 查看id为{}的通用业务属性value值详情", id);
        if (!Optional.fromNullable(id).isPresent()) {
            return Result.fail("查看通用业务属性value值信息时ID不能为空");
        }
        DwCurrencyAttributeValue t = this.dwCurrencyAttributeValueService.detailCurrencyAttributeValue(id);
        return Result.ok(t);
    }


    /**
     * 添加通用业务属性value值
     **/
    @ApiOperation(value = "添加通用业务属性value值", notes = "添加通用业务属性value值接口")
    @PostMapping(value = {"/saveCurrencyAttribute"})
    public R saveCurrencyAttribute(@RequestHeader String userCode,
                                   @RequestHeader Long projectId,
                                   @RequestBody DwCurrencyAttributeValueRequest request) {
        log.info("DwCurrencyAttributeValueController-->saveCurrencyAttribute 添加通用业务属性value值 ");
        request.setProjectId(projectId);
        return this.dwCurrencyAttributeValueService.saveCurrencyAttributeValue(request, userCode);
    }


    /**
     * 修改通用业务属性value值
     **/
    @ApiOperation(value = "修改通用业务属性value值", notes = "修改通用业务属性value值接口")
    @PutMapping(value = {"/updateCurrencyAttribute"})
    public R updateCurrencyAttribute(@RequestHeader String userCode,
                                     @RequestHeader Long projectId,
                                     @RequestBody DwCurrencyAttributeValueRequest request) {
        log.info("DwCurrencyAttributeValueController-->updateCurrencyAttribute 修改通用业务属性value值");
        if (!Optional.fromNullable(request.getId()).isPresent()) {
            return Result.fail("修改通用业务属性value值时ID不能为空");
        }
        request.setProjectId(projectId);
        return this.dwCurrencyAttributeValueService.updateCurrencyAttributeValue(request, userCode);
    }


    /**
     * 检验字段指定的字段与值是否重复
     **/
    @ApiOperation(value = "检验字段值在当前项目是否重复", notes = "检验字段值在当前项目是否重复")
    @GetMapping(value = {"/remoteCheckInProject"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "value", value = "验证重复的值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "oldValue", value = "验证重复的旧值", required = true, dataType = "String"),
            @ApiImplicitParam(name = "property", value = "验证重复的字段【数据表中的字段名】", required = true, dataType = "String")
    })
    public R remoteCheckInProject(@RequestParam("value") String value,
                                  @RequestParam("oldValue") String oldValue,
                                  @RequestParam("property") String property,
                                  @RequestHeader("projectId") Long projectId) {
        if (StringUtils.isEmpty(property)) {
            return Result.fail("检查字段信息不能为空");
        }
        if (StringUtils.isEmpty(value)) {
            return Result.fail("检查字段的内容信息不能为空");
        }

        boolean t = remoteCheckInProject(property, oldValue, value, projectId, "通用业务属性value值 name ", dwCurrencyAttributeValueService);
        log.info("检验字段值是否重复接口:" + t + "");
        return Result.ok(t);
    }

    /**
     * 删除通用业务属性value值
     **/
    @ApiOperation(value = "删除通用业务属性value值", notes = "删除通用业务属性value值接口")
    @DeleteMapping(value = {"/deleteCurrencyAttributeValue/{id}"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "删除通用业务属性value值ID", required = true, dataType = "Long")
    })
    public R deleteCurrencyAttribute(@RequestHeader String userCode, @RequestHeader String projectId, @PathVariable("id") Long id) {
        log.info("DwCurrencyAttributeValueController-->CurrencyAttribute 删除通用业务属性value值时ID为{}信息", id);

        if (!Optional.fromNullable(id).isPresent()) {
            return Result.fail("删除通用业务属性value值信息时ID不能为空");
        }

        QueryWrapper<DwCurrencyAttributeValue> query = new QueryWrapper<>();
        query.eq("parent_id", id)
                .eq("project_id", projectId)
                .eq("delete_model", 1);
        List<DwCurrencyAttributeValue> plist = this.dwCurrencyAttributeValueService.list(query);
        if (plist.size() > 0) {
            return Result.fail("请删除子节点后再来删除该条数据！");
        }

        return Result.ok("删除成功了：" + this.dwCurrencyAttributeValueService.deleteCurrencyAttributeValue(id, userCode) + " 条数据");
    }

}
