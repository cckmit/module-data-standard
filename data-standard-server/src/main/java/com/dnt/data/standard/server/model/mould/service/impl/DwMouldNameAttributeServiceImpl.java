package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwMouldNameAttributeMapper;
import com.dnt.data.standard.server.model.mould.entity.DwMouldNameAttribute;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameAttributeRequest;
import com.dnt.data.standard.server.model.mould.service.DwMouldNameAttributeService;
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
 * @description:模型命名属性--服务接口实现层 <br>
 * @date: 2021/7/27 下午12:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMouldNameAttributeServiceImpl extends BaseServiceImpl<DwMouldNameAttributeMapper, DwMouldNameAttribute> implements DwMouldNameAttributeService {
    @Autowired
    private DwMouldNameAttributeMapper dwMouldNameAttributeMapper;
    /**获数模型命名属性分页列表**/
    @Override
    public IPage<DwMouldNameAttribute> selectMouldNameAttributePage(DwMouldNameAttributeRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameAttributeServiceImpl-->selectMouldNameAttributePage 分页查询指标命名属性信息");
        }
        //页数
        Integer pn = request.getPageNum();
        /**每页显示的记录数**/
        Integer ps = request.getPageSize();
        Page<DwMouldNameAttribute> page = new Page<>(pn,ps);
        QueryWrapper<DwMouldNameAttribute> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.type",request.getType())
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        return this.dwMouldNameAttributeMapper.selectMouldNameAttributePage(page,q);
    }

    /**查看详情**/
    @Override
    public DwMouldNameAttribute detailMouldNameAttribute(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameAttributeServiceImpl-->detailMouldNameAttribute 查看指标命名属性详情信息");
        }

        DwMouldNameAttribute res = dwMouldNameAttributeMapper.selectById(id);
        Long cid = res.getCategoryId();
        res.setCategoryName(getCategoryNameById(cid));
        return res;
    }

    /**添加模型命名属性**/
    @Override
    public R saveMouldNameAttribute(DwMouldNameAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameAttributeServiceImpl-->saveMouldNameAttribute 添加模型命名属性 信息");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加模型命名属性时，模型命名属性的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加模型命名属性时，模型命名属性的编号不能为空");
        }
        List<DwMouldNameAttribute> lists = findDwMouldNameAttributeByName(request.getName(),request.getCategoryId(),request.getType());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加模型命名名属性时名称已存在");
        }
        if(!Optional.fromNullable(request.getType()).isPresent()){
            return Result.fail("添加模型命名名属性时，模型命名类型不能为空");
        }
        /**分类下的名称校验唯一 统一处理**/
        DwMouldNameAttribute na = new DwMouldNameAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,na);
        na.setCreateTime(new Date());
        na.setCreateUser(userCode);

        int i= this.dwMouldNameAttributeMapper.insert(na);
        log.info("成功添加{}条模型命名名属性数据",i);

        return Result.ok("添加模型命名名属性操作成功");
    }

    /**修改模型命名属性**/
    @Override
    public R updateMouldNameAttribute(DwMouldNameAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameAttributeServiceImpl-->updateMouldNameAttribute 修改模型命名属性 信息");
        }
        /**NO.1 业务字段非空判断**/
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型命名属性时，模型命名属性的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改模型命名属性时，模型命名属性的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改模型命名属性时，模型命名属性的编号不能为空");
        }

        List<DwMouldNameAttribute> lists = findDwMouldNameAttributeByName(request.getName(),request.getCategoryId(),request.getType());
        Long nowNAID = request.getId();
        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lists)){
            /**判断名称是否存在**/
            lists.forEach(na->{
              if(na.getId().longValue()!=nowNAID.longValue()){
                  haveNa.set(true);
              }

            });
        }

        if(haveNa.get()){
            return Result.fail("修改模型命名属性时，名称已存在");

        }

        DwMouldNameAttribute na = new DwMouldNameAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,na);
        na.setUpdateTime(new Date());
        na.setUpdateUser(userCode);
        int i = this.dwMouldNameAttributeMapper.updateById(na);
        log.info("修改模型命名属性{}条数据",i);
        return Result.ok("修改模型命名属性操作成功");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwMouldNameAttribute> findDwMouldNameAttributeByName(String name, Long categoryId,Integer type) {
        QueryWrapper<DwMouldNameAttribute> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq("type",type)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwMouldNameAttributeMapper.selectList(q);
    }
    /**删除模型命名属性**/
    @Override
    public int deleteMouldNameAttribute(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameAttributeServiceImpl-->deleteMouldNameAttribute 删除模型命名属性 数据l");
        }
        /**NO.1 构建数据**/
        DwMouldNameAttribute mna = new DwMouldNameAttribute();
        mna.setId(id);
        mna.setDeleteModel(0);
        mna.setUpdateTime(new Date());
        mna.setUpdateUser(userCode);
        //NO.2 执行删除
        return this.dwMouldNameAttributeMapper.updateById(mna);
    }


}
