package com.dnt.data.standard.server.model.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.sys.dao.DwMaskingRuleMapper;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingRule;
import com.dnt.data.standard.server.model.sys.service.DwMaskingRuleService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: 脱敏规则--服务接口实现层 <br>
 * @date: 2021/11/1 下午1:13 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMaskingRuleServiceImpl extends BaseServiceImpl<DwMaskingRuleMapper, DwMaskingRule> implements DwMaskingRuleService {
    /**
     * 添加API数据脱敏规则
     * @param userCode
     * @param entity
     * @return
     */
    @Override
    public R addRule(String userCode, DwMaskingRule entity) {
        log.info("DwMaskingRuleServiceImpl-->addRule 添加数据脱敏规则");
        try {
            if (StringUtils.isEmpty(entity.getMaskingRuleName())){
                return Result.fail("添加规则时脱敏规则名称不能为空！");
            }
            if (!Optional.fromNullable(entity.getMaskingType()).isPresent()){
                return Result.fail("添加规则时脱敏规则类型不能为空！");
            }
            List<DwMaskingRule> maskingRuleList = findMaskingRuleByRuleName(entity.getMaskingRuleName());
            if(CollectionUtils.isNotEmpty(maskingRuleList)){
                return Result.fail("添加规则时，脱敏规则名称已存在");
            }

            entity.setCreateTime(new Date());
            entity.setCreateUser(userCode);
            //插入数据信息
            this.baseMapper.insert(entity);
            return Result.ok("添加数据脱敏规则成功！");
        } catch (BeansException e) {
            e.printStackTrace();
            return Result.fail("添加数据脱敏规则异常！");
        }
    }

    /**
     * 编辑API数据脱敏规则
     * @param userCode
     * @param entity
     * @return
     */
    @Override
    public R editRule(String userCode, DwMaskingRule entity) {
        log.info("DwMaskingRuleServiceImpl-->editRule 编辑数据脱敏规则");
        try {
            if (!Optional.fromNullable(entity.getId()).isPresent()){
                return Result.fail("编辑规则时id不能为空！");
            }
            if (StringUtils.isEmpty(entity.getMaskingRuleName())){
                return Result.fail("编辑规则时脱敏规则名称不能为空！");
            }
            if (!Optional.fromNullable(entity.getMaskingType()).isPresent()){
                return Result.fail("编辑规则时脱敏规则类型不能为空！");
            }
            List<DwMaskingRule> maskingRuleList = findMaskingRuleByRuleName(entity.getMaskingRuleName());
            AtomicBoolean haveNa = new AtomicBoolean(false);

            if(CollectionUtils.isNotEmpty(maskingRuleList)){
                //判断名称是否存在
                maskingRuleList.forEach(mr->{
                    if(mr.getId().longValue()!=entity.getId().longValue()){
                        haveNa.set(true);
                    }
                });
            }
            if(haveNa.get()){
                return Result.fail("编辑规则时，脱敏规则名称已存在");
            }
            entity.setUpdateTime(new Date());
            entity.setUpdateUser(userCode);
            this.baseMapper.updateById(entity);
            return Result.ok("编辑数据脱敏规则成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("编辑数据脱敏规则异常！");
        }
    }

    /**
     * 根据 脱敏规则名称 查询脱敏规则是不是存在
     * @param ruleName
     * @return
     */
    private List<DwMaskingRule> findMaskingRuleByRuleName(String ruleName) {
        QueryWrapper<DwMaskingRule> q = Wrappers.query();
        q.select("id,masking_rule_name").eq("delete_model",1)
                .eq("masking_rule_name",ruleName);
        return this.baseMapper.selectList(q);
    }
    /**
     * 删除API脱敏规则
     * @param userCode
     * @param id
     * @return
     */
    @Override
    public R deleteRule(String userCode, Long id) {
        if (!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除脱敏规则时规则编号不能为空！");
        }
        DwMaskingRule entity=this.baseMapper.selectById(id);
        Integer num= entity.getDataSetNum();
        if(num>0){
            return Result.fail("有数据集使用该规则时不允许删除！");
        }
        entity.setDeleteModel(0);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userCode);
        this.baseMapper.updateById(entity);
        return Result.ok("删除API脱敏规则成功！");
    }

    /**
     * 分页获取脱敏规则列表
     * @param map
     * @param page
     * @return
     */
    @Override
    public IPage<DwMaskingRule> maskingRuleList(Map map, Page<DwMaskingRule> page) {
        QueryWrapper<DwMaskingRule> query=new QueryWrapper<>();
        if (Optional.fromNullable(map.get("maskingRuleName")).isPresent()){
            query.like(StringUtils.isNotEmpty(map.get("maskingRuleName").toString()),"masking_rule_name",map.get("maskingRuleName"));
        }
        query.eq(Optional.fromNullable(map.get("projectId")).isPresent(),"project_id",map.get("projectId"));
        query.eq("delete_model", 1);
        query.orderByDesc("create_time");
        return this.baseMapper.selectPage(page,query);
    }
}
