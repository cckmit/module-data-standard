package com.dnt.data.standard.server.model.standard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.standard.entity.DwTarget;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetRequest;

import java.util.List;
import java.util.Map;

/**
 * @description: 指标--服务接口层 <br>
 * @date: 2021/7/19 下午2:59 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwTargetService extends BaseService<DwTarget> {
    /**
     * 获取指标分页数据
     * @param request
     * @return
     */
    IPage<DwTarget> selectTargetPage(DwTargetRequest request);

    /**
     * 指标数据详情
     * @param id
     * @return
     */
    DwTarget detailTarget(Long id);

    /**
     * 查询时间周期修饰
     * @return
     */
    Map<String,List<Map<String,Object>>> selectAttributeItem(Long projectId);

    /**
     * 查询质量校验函数下拉列表
     * @return
     */
    List<Map<String, Object>> selectFunctionItem(Long projectId);

    /**
     * 保存指标信息
     * @param request
     * @param userCode
     * @return
     */
    R saveTarget(DwTargetRequest request, String userCode);

    /**
     * 更新指标信息
     * @param request
     * @param userCode
     * @return
     */
    R updateTarget(DwTargetRequest request, String userCode);

    /**
     * 删除指标
     * @param id
     * @param userCode
     * @return
     */
    int deleteTarget(Long id, String userCode);

}
