package com.dnt.data.standard.server.model.mould.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.DwMould;
import com.dnt.data.standard.server.model.mould.entity.DwOperationLog;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldPhysicsRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwMouldResponse;
import com.dnt.data.standard.server.model.service.BaseService;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @description: 模型管理--服务接口层 <br>
 * @date: 2021/8/4 下午4:44 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMouldService extends BaseService<DwMould> {
    /**获取模型分页列表接口
     *
     * @param request
     * @return
     */
    IPage<DwMould> selectDwMouldPage(DwMouldRequest request);

    /**删除分类
     *
     * @param id
     * @param userCode
     * @return
     */
    R deleteMould(Long id, String userCode);

    /**选择公共字段集的字段信息
     *
     * @param publicMouldIds
     * @return
     */
    List<Map<String,Object>> selectPublicMouldField(List<Long> publicMouldIds);

    /**添加手工新建模型
     *
     * @param request
     * @param userCode
     * @return
     */
    R saveMould(DwMouldRequest request, String userCode);

    /**修改手工新建模型
     *
     * @param request
     * @param userCode
     * @return
     */
    R updateMould(DwMouldRequest request, String userCode);
    /**==========================================ddl操作接口=======================================**/
    /**ddl建模数据源类型下拉列表接口
     *
     * @param envFlag
     * @return
     */
    JSONArray selectDDLSourceTypeItem(String envFlag);

    /**添加ddl建模
     *
     * @param request
     * @param userCode
     * @return
     */
    R saveDDLMould(DwMouldRequest request, String userCode);

    /**修改ddl建模
     *
     * @param request
     * @param userCode
     * @return
     */
    R updateDDLMould(DwMouldRequest request, String userCode);

    /**==========================================物理化操作=======================================**/
    /**查看默认数据源接口
     *
     * @param projectId
     * @return
     */
    R detailDefaultDatasource(Long projectId);

    /**查看指定存储资源下的 项目下拉列表
     *
     * @param envFlag
     * @param sourceTypeId
     * @return
     */
    R selectProjectItem(String envFlag,Long sourceTypeId);

    /**模型物理化
     *
     * @param lotNumber
     * @param userCode
     * @param request
     * @return
     */
    R doMouldPhysics(Long lotNumber,String userCode, DwMouldPhysicsRequest request);

    /**
     * 异步写记录日志
     *
     * @param gg
     */
    void writeOperationLog(DwOperationLog gg);

    /**
     * 模型发布记录模型相关数据的参数
     * @param dm
     * @param id
     */
    void writeMouldReleaseLog(DwMould dm,Long id);

    /**
     * 根据模型查询物理化的日志信息
     *
     * @param mouldId
     * @param mouldFlag
     * @return
     */
    List<DwOperationLog> selectMouldPhysicsLog(Long mouldId,Integer mouldFlag);

    /**查看详情
     *
     * @param id
     * @return
     */
    DwMouldResponse detailMould(Long id);

    /**发布模型操作
     *
     * @param id
     * @param mouldStatus
     * @param userCode
     * @return
     */
    R doMouldRelease(Long id,Integer mouldStatus,String userCode);

    /**查看模型物理化的DDL语句
     *
     * @param mouldId
     * @return
     */
    R selectMouldPhysicsDDL(Long mouldId);

    /**查看模型物理化表结构
     *
     * @param mouldId
     * @return
     */
    R selectMouldPhysicsStructure(Long mouldId);

    /**查看模型物理化表数据
     *
     * @param physicsId
     * @return
     */
    R selectMouldPhysicsTable(Long physicsId);

    /**查询物理化操作日志 批次下的详细信息
     *
     * @param lotNumber
     * @param mouldFlag
     * @return
     */
    List<DwOperationLog> selectMouldPhysicsChildLog(Long lotNumber, Integer mouldFlag);
    /**====================================通用业务属性======================================================**/
    /**通用业务属性通用下拉列表
     *
     * @return
     */
    Map<String, Object> selectMouldCurrencyAttributeItem();

    /**通用业务属性负责人下拉列表
     *
     * @return
     */
    List<Map<String, Object>> selectMouldBossheadItem();

    /**查看模型物理化表数据信息
     *
     * @param mouldId
     * @return
     */
    List<Map<String, Object>> selectMouldTableStructure(Long mouldId);

    /**文件批量导入操作
     *
     * @param processCode
     * @param uploadFile
     * @param userCode
     * @param projectId
     * @param categoryId
     * @return
     */
    R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId);

    /**获取导入文件进度
     *
     * @param processCode
     * @return
     */
    R getImportProgress(String processCode);

    /**
     * 上传大文
     * @param uploadFile
     * @param userCode
     */
    void uploadBigFile(MultipartFile uploadFile, String userCode);
}
