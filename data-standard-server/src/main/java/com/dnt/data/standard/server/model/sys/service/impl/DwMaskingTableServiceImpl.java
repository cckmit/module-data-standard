package com.dnt.data.standard.server.model.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.sys.entity.request.DwMaskingTableRequest;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.sys.dao.DwMaskingRuleMapper;
import com.dnt.data.standard.server.model.sys.dao.DwMaskingTableMapper;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingRule;
import com.dnt.data.standard.server.model.sys.entity.DwMaskingTable;
import com.dnt.data.standard.server.model.sys.service.DwMaskingTableService;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @description: 脱敏规则表--服务接口实现层 <br>
 * @date: 2021/11/1 下午1:13 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMaskingTableServiceImpl extends BaseServiceImpl<DwMaskingTableMapper, DwMaskingTable> implements DwMaskingTableService {
    
    @Autowired
    private DwMaskingRuleMapper dwMaskingRuleMapper;

    /**
     * 添加脱敏表
     * @param userCode
     * @param entity
     * @return
     */
    @Override
    @Transactional
    public R addMaskingTable(String userCode, DwMaskingTable entity) {
        if (!Optional.fromNullable(entity.getMaskingRuleId()).isPresent()){
            return Result.fail("添加脱敏表时脱敏规则编号不能为空！");
        }
        entity.setCreateTime(new Date());
        entity.setCreateUser(userCode);
        this.baseMapper.insert(entity);
        //改变脱敏规则表统计数量
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("id",entity.getMaskingRuleId());
        DwMaskingRule rule= dwMaskingRuleMapper.selectOne(wrapper);
        rule.setDataSetNum(rule.getDataSetNum()+1);
        dwMaskingRuleMapper.updateById(rule);
        return Result.ok("添加脱敏表成功！");
    }

    /**
     *编辑脱敏表信息
     * @param entity
     * @param userCode
     * @return
     */
    @Override
    public R editMaskingTable(DwMaskingTable entity, String userCode) {
        if (!Optional.fromNullable(entity.getId()).isPresent()){
            return Result.fail("编辑数据源时数据源编号不能为空！");
        }

        try {
            entity.setUpdateUser(userCode);
            entity.setUpdateTime(new Date());
            this.baseMapper.updateById(entity);
            return Result.ok("编辑数据源成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.fail("编辑数据源出现异常！");
        }
    }

    /**
     *删除脱敏表信息
     * @param userCode
     * @param id
     * @return
     */
    @Override
    @Transactional
    public R deleteMaskingTable(String userCode, Long id) {
        if (!Optional.fromNullable(id).isPresent()){
            return Result.fail("删除脱敏表信息时编号不能为空！");
        }
        DwMaskingTable entity= this.baseMapper.selectById(id);
        entity.setId(id);
        entity.setUpdateTime(new Date());
        entity.setUpdateUser(userCode);
        entity.setDeleteModel(0);
        this.baseMapper.updateById(entity);
        //改变脱敏规则表统计数量
        QueryWrapper wrapper=new QueryWrapper();
        wrapper.eq("id",entity.getMaskingRuleId());
        DwMaskingRule rule= dwMaskingRuleMapper.selectOne(wrapper);
        rule.setDataSetNum(rule.getDataSetNum()-1);
        dwMaskingRuleMapper.updateById(rule);
        return Result.ok("删除脱敏表信息成功！");
    }

    /**
     * 分页获取脱敏表信息
     * @param request
     * @param page
     * @return
     */
    @Override
    public IPage<DwMaskingTable> maskingTableList(DwMaskingTableRequest request, Page<DwMaskingTable> page) {
        QueryWrapper<DwMaskingTable> query = Wrappers.query();
        query.eq("masking_rule_id",request.getMaskingRuleId())
        .like(StringUtils.isNotEmpty(request.getTableName()),"table_name" ,request.getTableName())
        .like(StringUtils.isNotEmpty(request.getFieldName()),"field_name" ,request.getFieldName())
        .eq(Optional.fromNullable(request.getProjectId()).isPresent(),"project_id",request.getProjectId())
        .eq("delete_model", 1)
        .orderByDesc("create_time");

        IPage<DwMaskingTable> backPage = this.baseMapper.selectPage(page,query);
        return backPage;
    }

    /**
     * 批量开启/关闭血缘启用状态
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R updateBloodRuleStatus(String userCode, DwMaskingTableRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMaskingTableServiceImpl-->updateBloodRuleStatus 批量开启/关闭血缘启用状态");
        }
        List<Long> ids = request.getIds();
        Integer status = request.getIsBloodRuleStatus();
        ids.forEach(id->{

            DwMaskingTable t = this.baseMapper.selectById(id);
            Integer snow = t.getIsBloodRuleStatus();

            if(status.intValue()==snow.intValue()){
                //血缘启用状态 1 开启 0 关闭
                log.warn(status==1?"当前血缘状态已开启不能重复操作":"当前血缘状态已关闭不能重复操作");
                return;
            }

            DwMaskingTable mt = new DwMaskingTable();
            mt.setId(id);
            mt.setIsBloodRuleStatus(status);
            mt.setUpdateUser(userCode);
            mt.setUpdateTime(new Date());

            int ii = this.baseMapper.updateById(mt);
            log.warn("成功执行了{}条数据",ii);
        });


        return Result.ok("操作成功");
    }
}
