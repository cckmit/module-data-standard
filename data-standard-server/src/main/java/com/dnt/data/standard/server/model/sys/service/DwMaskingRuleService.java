package com.dnt.data.standard.server.model.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.BaseService;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingRule;

import java.util.Map;

/**
 * @description: 脱敏规则--服务接口层 <br>
 * @date: 2021/11/1 下午1:12 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwMaskingRuleService extends BaseService<DwMaskingRule> {
    /**
     * 添加规则
     * @param userCode
     * @param entity
     * @return
     */
    R addRule(String userCode, DwMaskingRule entity);

    /**
     * 编辑规则
     * @param userCode
     * @param entity
     * @return
     */
    R editRule(String userCode, DwMaskingRule entity);

    /**
     * 删除规则
     * @param userCode
     * @param id
     * @return
     */
    R deleteRule(String userCode, Long id);

    /**
     * 分页查询规则
     * @param map
     * @param page
     * @return
     */
    IPage<DwMaskingRule> maskingRuleList(Map map, Page<DwMaskingRule> page);

}
