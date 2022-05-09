package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.standard.entity.DwDataElement;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDataElementExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDataElementRequest;
import com.dnt.data.standard.server.model.standard.entity.response.DwDataElementResponse;
import com.dnt.data.standard.server.model.service.BaseService;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据元-服务接口层  <br>
 * @date: 2021/7/21 下午3:22 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwDataElementService extends BaseService<DwDataElement> {
    /**
     * 分页查询列表
     * @param request
     * @return
     */
    IPage<DwDataElement> selectDataElementPage(DwDataElementRequest request);

    /**
     * 返回数据元详情信息
     * @param id
     * @return
     */
    DwDataElementResponse detailDataElement(Long id);

    /**
     * 查询数据元分类下拉列表
     * @return
     */
    List<Map<String,Object>> selectDataElementTypeItem();

    /**
     * 查询数据元数据字典下拉列表
     * @return
     */
    List<Map<String, Object>> selectDictItem();

    /**
     * 查看平缓目录的目录信息
     * @param id
     * @return
     */
    List<Map<String, Object>> selectParallelCatalogue(Long id);

    /**
     * 添加数据元信息
     * @param request
     * @param userCode
     * @return
     */
    R saveDataElement(DwDataElementRequest request, String userCode);

    /**
     * 编辑数据元信息
     * @param request
     * @param userCode
     * @return
     */
    R updateDataElement(DwDataElementRequest request, String userCode);

    /**
     * 删除数据元信息
     * @param id
     * @param userCode
     * @return
     */
    int deleteDataElement(Long id, String userCode);

    /**
     * 根据名称与分类过滤数据元信息
     * @param name
     * @param categoryId
     * @return
     */
    List<DwDataElementExcel> selectDataElementList(String name, Long categoryId);

    /**
     * 文件批量导入操作
     *
     * @param processCode
     * @param uploadFile
     * @param userCode
     * @param projectId
     * @param categoryId
     * @return
     */
    R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId);

    /**
     * 获取导入文件进度
     * @param processCode
     * @return
     */
    R getImportProgress(String processCode);
}
