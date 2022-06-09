package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwCurrencyAttributeMapper;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttribute;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeValueRequest;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeService;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeValueService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

    @Autowired
    private DwCurrencyAttributeValueService dwCurrencyAttributeValueService;

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
        q.eq("da.delete_model",1)
                .eq("da.project_id",request.getProjectId())
                .eq(Optional.fromNullable(request.getAttributeType()).isPresent(),"da.attribute_type",request.getAttributeType())
                .like(Optional.fromNullable(request.getAttributeName()).isPresent(),"da.attribute_name",request.getAttributeName())
                .orderByDesc("da.id");

        return this.dwCurrencyAttributeMapper.selectCurrencyAttributePage(page,q);
    }


    /*获取数通用业务属性树*/
    @Override
    public List selectCurrencyAttributeTree(DwCurrencyAttributeRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->selectCurrencyAttributeTree 获取数通用业务属性树");
        }

        //模糊搜索attribute_value
        QueryWrapper<DwCurrencyAttributeValue> query = Wrappers.query();
        query().select("DISTINCT(attribute_id) as attributeId")
                .like(Optional.fromNullable(request.getAttributeName()).isPresent(),"attribute_name",request.getAttributeName())
                .eq("delete_model",1);
        List<DwCurrencyAttributeValue> attributeIds = this.dwCurrencyAttributeValueService.getBaseMapper().selectList(query);
        HashSet<Long> ids = new HashSet<>();
        attributeIds.stream().forEach(dwCurrencyAttributeValue->{
            ids.add(dwCurrencyAttributeValue.getAttributeId());
        });
        QueryWrapper<DwCurrencyAttribute> queryWrapper = Wrappers.query();
        queryWrapper.select("id,attribute_name,attribute_type")
                .eq("delete_model",1)
                .eq("project_id",request.getProjectId())
                .lt("attribute_type",3);
        if(ids.size()>0){
            queryWrapper.in("id",ids);
        }
        List<DwCurrencyAttribute> dwCurrencyAttribute1 = this.dwCurrencyAttributeMapper.selectList(queryWrapper);

        //模糊搜索attribute_name
        QueryWrapper<DwCurrencyAttribute> q = Wrappers.query();
        q.select("id,attribute_name,attribute_type")
                .eq("delete_model",1)
                .eq("project_id",request.getProjectId())
                .lt("attribute_type",3)
                .like(Optional.fromNullable(request.getAttributeName()).isPresent(),"attribute_name",request.getAttributeName())
                .orderByDesc("id");

        List<DwCurrencyAttribute> dwCurrencyAttribute2 = this.dwCurrencyAttributeMapper.selectList(q);

        //用LinkedList去重合并两部分数据
        LinkedList<DwCurrencyAttribute> dwCurrencyAttributes = new LinkedList<>();
        dwCurrencyAttributes.addAll(dwCurrencyAttribute1);
        dwCurrencyAttributes.addAll(dwCurrencyAttribute2);

        //为查询出的属性赋值
        dwCurrencyAttributes.forEach(DwCurrencyAttribute->{
            DwCurrencyAttributeValueRequest valueRequest = new DwCurrencyAttributeValueRequest();
            valueRequest.setAttributeId(DwCurrencyAttribute.getId());
            valueRequest.setAttributeType(DwCurrencyAttribute.getAttributeType());
            List list = dwCurrencyAttributeValueService.selectCurrencyAttributeValueTree(valueRequest);
            DwCurrencyAttribute.setAttributeValues(list);
        });

        return dwCurrencyAttributes;
    }

    /**查看详情**/
    @Override
    public DwCurrencyAttribute detailCurrencyAttribute(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->detailCurrencyAttribute 查看详情");
        }
        DwCurrencyAttribute ca = this.dwCurrencyAttributeMapper.selectById(id);
        /*通用业务属性类型：1 枚举， 2 树形， 3 数字， 4 日期，5 文本*/
        Integer attributeType = ca.getAttributeType();

        DwCurrencyAttributeValueRequest valueRequest = new DwCurrencyAttributeValueRequest();
        valueRequest.setAttributeId(id);
        valueRequest.setAttributeType(attributeType);
        List list = dwCurrencyAttributeValueService.selectCurrencyAttributeValueTree(valueRequest);
        ca.setAttributeValues(list);
        return ca;
    }

    /**添加通用业务属性**/
    @Override
    @Transactional
    public R saveCurrencyAttribute(DwCurrencyAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeServiceImpl-->saveCurrencyAttribute 添加通用业务属性");
        }
        if(StringUtils.isEmpty(request.getAttributeName())){
            return Result.fail("添加通用业务属性时，通用业务属性的名称不能为空");
        }
        /**NO.1 判断名称信息是否存在**/
        List<DwCurrencyAttribute> lists = findDwCurrencyAttributeByName(request.getAttributeName(), request.getProjectId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加通用业务属性时名称已存在");
        }
        /**NO.2 插入业务属性信息数据**/
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
        String name = request.getAttributeName();
        /**NO.1 业务字段非空判断**/
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改通用业务属性时，通用业务属性的ID不能为空");
        }
        if(StringUtils.isEmpty(name)){
            return Result.fail("修改通用业务属性时，通用业务属性的名称不能为空");
        }

        List<DwCurrencyAttribute> lists = findDwCurrencyAttributeByName(name,request.getProjectId());

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

        //内置数据名称和类型不可修改
        if(this.isDefaultData(request.getId())){
            ds.setId(request.getId());
            ds.setAttributeLength(request.getAttributeLength());
        }else{
            BeanValueTrimUtil.beanValueTrim(request);
            BeanUtils.copyProperties(request,ds);
        }
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        int i = this.dwCurrencyAttributeMapper.updateById(ds);
        log.info("修改通用业务属性{}条数据",i);
        return Result.ok("修改通用业务属性操作成功");
    }

    /*是否是内置数据*/
    private Boolean isDefaultData(Long id){
        Boolean flag=false;
        DwCurrencyAttribute dwCurrencyAttribute = this.dwCurrencyAttributeMapper.selectById(id);
        if(dwCurrencyAttribute.getCreateUser().equals("内置")){
            flag=true;
        }
        return flag;
    }

    /**
     * 根据名称 查询名称是不是存在
     * @param name
     * @return
     */
    private List<DwCurrencyAttribute> findDwCurrencyAttributeByName(String name,Long projectId) {
        QueryWrapper<DwCurrencyAttribute> q = Wrappers.query();
        q.select("id,attribute_name").eq("delete_model",1)
                .eq("project_id",projectId)
                .eq("attribute_name",name);
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
