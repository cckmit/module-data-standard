package com.dnt.data.standard.server.model.mould.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.mould.entity.excel.DwDbBaseExcel;
import com.dnt.data.standard.server.model.mould.entity.request.DwDbBaseRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDbBaseResponse;
import com.dnt.data.standard.server.model.mould.entity.DwDbBase;
import com.dnt.data.standard.server.model.service.BaseService;

import java.io.File;
import java.util.List;

/**
 * @description: 数据基础库--服务接口层 <br>
 * @date: 2021/7/29 上午11:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwDbBaseService extends BaseService<DwDbBase> {
    /**
     * 获取数据基础库分页列表
     * @param request
     * @return
     */
    IPage<DwDbBase> selectDbBasePage(DwDbBaseRequest request);

    /**
     * 查看详情
     * @param id
     * @return
     */
    DwDbBaseResponse detailDbBase(Long id);

    /**
     * 添加数据基础库
     * @param request
     * @param userCode
     * @return
     */
    R saveDbBase(DwDbBaseRequest request, String userCode);

    /**
     * 修改数据基础库
     * @param request
     * @param userCode
     * @return
     */
    R updateDbBase(DwDbBaseRequest request, String userCode);

    /**
     * 删除数据基础库
     * @param id
     * @param userCode
     * @return
     */
    int deleteDbBase(Long id, String userCode);

    /**
     * 查询指定条件下的 数据基础库数据
     * @param name
     * @param categoryId
     * @return
     */
    List<DwDbBaseExcel> selectDbBaseList(String name, Long categoryId);

    /**
     * 上传文件
     * @param processCode
     * @param uploadFile
     * @param userCode
     * @param projectId
     * @param categoryId
     * @return
     */
    R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId);

    /**
     * 获取进度条
     * @param processCode
     * @return
     */
    R getImportProgress(String processCode);
}
