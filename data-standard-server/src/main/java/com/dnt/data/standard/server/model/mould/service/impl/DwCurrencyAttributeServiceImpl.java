package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwCurrencyAttributeMapper;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: 通用业务属性--服务接口实现层 <br>
 * @date: 2021/7/28 下午1:05 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwCurrencyAttributeServiceImpl extends BaseServiceImpl<DwCurrencyAttributeMapper, DwCurrencyAttribute> implements DwCurrencyAttributeService {
    @Autowired
    private DwCurrencyAttributeMapper dwCurrencyAttributeMapper;
    /**获取数通用业务属性分页列表**/
    @Override
    public IPage<DwCurrencyAttribute> selectCurrencyAttributePage(DwCurrencyAttributeRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->selectCurrencyAttributePage 获取数通用业务属性分页列表");
        }
        /**页数**/
        Integer pn = request.getPageNum();
        /**每页显示的记录数**/
        Integer ps = request.getPageSize();
        Page<DwCurrencyAttribute> page = new Page<>(pn,ps);
        QueryWrapper<DwCurrencyAttribute> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .eq(Optional.fromNullable(request.getType()).isPresent(),"a.type",request.getType())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        return this.dwCurrencyAttributeMapper.selectCurrencyAttributePage(page,q);
    }
    /**查看详情**/
    @Override
    public DwCurrencyAttribute detailCurrencyAttribute(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->detailCurrencyAttribute 查看详情");
        }
        DwCurrencyAttribute ca = this.dwCurrencyAttributeMapper.selectById(id);
        Long categoryId = ca.getCategoryId();
        ca.setCategoryName(getCategoryNameById(categoryId));
        return ca;
    }
    /**添加通用业务属性**/
    @Override
    public R saveCurrencyAttribute(DwCurrencyAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->saveCurrencyAttribute 添加通用业务属性");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加通用业务属性时，通用业务属性的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加通用业务属性时，通用业务属性的编号不能为空");
        }

        /**NO.1 判断名称信息是否存在**/
        List<DwCurrencyAttribute> lists = findDwCurrencyAttributeByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加通用业务属性时名称已存在");
        }
        DwCurrencyAttribute ds = new DwCurrencyAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ds);
        ds.setCreateTime(new Date());
        ds.setCreateUser(userCode);
        int ac = this.dwCurrencyAttributeMapper.insert(ds);
        log.info("成功添加{}条通用业务属性数据",ac);

        return Result.ok("添加通用业务属性操作成功");
    }
    /**修改通用业务属性**/
    @Override
    public R updateCurrencyAttribute(DwCurrencyAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->updateCurrencyAttribute 修改通用业务属性");
        }
        Long id = request.getId();
        String name = request.getName();
        /**NO.1 业务字段非空判断**/
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改通用业务属性时，通用业务属性的ID不能为空");
        }
        if(StringUtils.isEmpty(name)){
            return Result.fail("修改通用业务属性时，通用业务属性的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改通用业务属性时，通用业务属性的编号不能为空");
        }

        List<DwCurrencyAttribute> lists = findDwCurrencyAttributeByName(name,request.getCategoryId());

        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lists)){
            /**判断名称是否存在**/
            lists.forEach(na->{
                if(na.getId().longValue()!=id.longValue()){
                    haveNa.set(true);
                    return;
                }

            });
        }

        if(haveNa.get()){
            return Result.fail("修改通用业务属性时，名称已存在");

        }
        /**NO.2 构建数据**/
        DwCurrencyAttribute ds = new DwCurrencyAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ds);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        int i = this.dwCurrencyAttributeMapper.updateById(ds);
        log.info("修改通用业务属性{}条数据",i);
        return Result.ok("修改通用业务属性操作成功");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwCurrencyAttribute> findDwCurrencyAttributeByName(String name, Long categoryId) {
        QueryWrapper<DwCurrencyAttribute> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwCurrencyAttributeMapper.selectList(q);
    }

    /**删除通用业务属性**/
    @Override
    public int deleteCurrencyAttribute(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->deleteCurrencyAttribute 删除通用业务属性");
        }
        //NO.1 构建数据
        DwCurrencyAttribute ds = new DwCurrencyAttribute();
        ds.setId(id);
        ds.setDeleteModel(0);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        //NO.2执行操作
        return this.dwCurrencyAttributeMapper.updateById(ds);
    }
}
