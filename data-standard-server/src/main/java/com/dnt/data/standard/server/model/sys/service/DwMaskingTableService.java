package com.dnt.data.standard.server.model.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingTable;
import com.dnt.data.standard.server.model.sys.entity.request.DwMaskingTableRequest;

/**
 * @description: 脱敏规则表--服务接口层 <br>
 * @date: 2021/11/1 下午1:12 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMaskingTableService extends BaseService<DwMaskingTable> {
    /**
     * 添加脱敏表
     * @param userCode
     * @param entity
     * @return
     */
    R addMaskingTable(String userCode, DwMaskingTable entity);

    /**
     * 编辑脱敏表
     * @param entity
     * @param userCode
     * @return
     */
    R editMaskingTable(DwMaskingTable entity, String userCode);

    /**
     * 删除脱敏表
     * @param userCode
     * @param id
     * @return
     */
    R deleteMaskingTable(String userCode, Long id);

    IPage<DwMaskingTable> maskingTableList(DwMaskingTableRequest request, Page<DwMaskingTable> page);

    /**
     * 批量开启/关闭血缘启用状态
     * @param userCode
     * @param request
     * @return
     */
    R updateBloodRuleStatus(String userCode, DwMaskingTableRequest request);
}
