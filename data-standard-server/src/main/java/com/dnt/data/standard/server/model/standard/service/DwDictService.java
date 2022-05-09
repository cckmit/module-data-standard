package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwDict;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDictExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDictRequest;
import com.dnt.data.standard.server.model.standard.entity.response.DwDictResponse;

import java.io.File;
import java.util.List;

/**
 * @description: 数据字典--服务接口层 <br>
 * @date: 2021/7/12 下午1:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

public interface DwDictService extends BaseService<DwDict> {
    /**
     * 获取数据字典分页数据
     * @param request
     * @return
     */
    IPage<DwDict> selectDictPage(DwDictRequest request);

    /**
     * 查看数据字典详情
     * @param id
     * @return
     */
    DwDictResponse detailDict(Long id);

    /**
     * 保存数据字典
     * @param request
     * @param userCode
     * @return
     */
    R saveDict(DwDictRequest request, String userCode);

    /**
     * 修改数据字典
     * @param request
     * @param userCode
     * @return
     */
    R updateDict(DwDictRequest request, String userCode);

    /**
     * 删除数据字典
     * @param id
     * @param userCode
     * @return
     */
    int deleteDict(Long id, String userCode);

    /**
     * 删除数据字典关联字段
     * @param id
     * @param userCode
     * @return
     */
    int deleteDictField(Long id, String userCode);

    /**
     * 导出数据接口
     * @return
     */
    List<DwDictExcel> selectDictList();

    /**
     * 上传操作
     * @param processCode
     * @param uploadFile
     * @param userCode
     * @param projectId
     * @param categoryId
     * @return
     */
    R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId);

    /**
     * 进度条
     * @param processCode
     * @return
     */
    R getImportProgress(String processCode);
}
