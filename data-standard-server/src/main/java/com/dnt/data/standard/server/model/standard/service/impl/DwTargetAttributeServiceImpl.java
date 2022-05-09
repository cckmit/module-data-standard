package com.dnt.data.standard.server.model.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.dao.DwTargetAttributeMapper;
import com.dnt.data.standard.server.model.standard.entity.DwTargetAttribute;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetAttributeRequest;
import com.dnt.data.standard.server.model.standard.service.DwTargetAttributeService;
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
 * @description: 指标属性--服务接口实现层 <br>
 * @date: 2021/7/15 下午3:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwTargetAttributeServiceImpl extends BaseServiceImpl<DwTargetAttributeMapper, DwTargetAttribute> implements DwTargetAttributeService {

    @Autowired
    private DwTargetAttributeMapper dwTargetAttributeMapper;
    /**查看分页数据**/
    @Override
    public IPage<DwTargetAttribute> selectTargetAttributePage(DwTargetAttributeRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetAttributeServiceImpl-->selectTargetAttributePage 获取指标属性分页数据列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwTargetAttribute> page = new Page<>(pn,ps);
        QueryWrapper<DwTargetAttribute> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .eq("a.type",request.getType())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        return this.dwTargetAttributeMapper.selectTargetAttributePage(page,q);
    }
    /**查看详情**/
    @Override
    public DwTargetAttribute detailTargetAttribute(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetAttributeServiceImpl-->detailTargetAttribute 查看指标属性详情信息");
        }

        DwTargetAttribute ta = this.dwTargetAttributeMapper.selectById(id);

        Long cid = ta.getCategoryId();
        ta.setCategoryName(getCategoryNameById(cid));
        //返回数据详情
        return ta;
    }
    /**保存指标属性**/
    @Override
    public R saveTargetAttribute(DwTargetAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetAttributeServiceImpl-->saveTargetAttribute 添加数据指标属性信息");
        }
        //NO.1 业务字段非空判断
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加指标属性时，指标属性的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加信息时，编号不能为空");
        }
        if(!Optional.fromNullable(request.getType()).isPresent()){
            return Result.fail("添加指标属性时，指标属性类型不能为空");
        }
        List<DwTargetAttribute> lists = findDwTargetAttributeByName(request.getName(),request.getCategoryId(),request.getType());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加指标属性时名称已存在");
        }

        //NO.2 构建数据信息
        DwTargetAttribute dta = new DwTargetAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dta);
        dta.setCreateTime(new Date());
        dta.setCreateUser(userCode);
        this.dwTargetAttributeMapper.insert(dta);
        return Result.ok("添加指标属性操作成功");
    }


    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwTargetAttribute> findDwTargetAttributeByName(String name, Long categoryId,Integer type) {
        QueryWrapper<DwTargetAttribute> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq("type",type)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwTargetAttributeMapper.selectList(q);
    }
    /**修改指标属性**/
    @Override
    public R updateTargetAttribute(DwTargetAttributeRequest request, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwTargetAttributeServiceImpl-->updateTargetAttribute 修改数据指标属性信息");
        }
        //NO.1 业务字段非空判断
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改指标属性时，指标属性的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改信息时，编号不能为空");
        }
        String name = request.getName();
        Long id = request.getId();

        List<DwTargetAttribute> lists = findDwTargetAttributeByName(name,request.getCategoryId(),request.getType());
        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lists)){
            //判断名称是否存在
            lists.forEach(na->{
                if(na.getId().longValue()!=id.longValue()){
                    haveNa.set(true);
                }

            });
        }
        if(haveNa.get()){
            return Result.fail("修改指标属性时，名称已存在");
        }


        //NO.2 数据初始化
        DwTargetAttribute dtau = new DwTargetAttribute();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dtau);
        dtau.setUpdateUser(userCode);
        dtau.setUpdateTime(new Date());
        //NO.3 更新指标属性
        this.dwTargetAttributeMapper.updateById(dtau);
        return Result.ok("更新指标属性操作成功");
    }
    /**删除指标属性**/
    @Override
    public int deleteTargetAttribute(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetAttributeServiceImpl.deleteTargetAttribute");
        }
        DwTargetAttribute dta = new DwTargetAttribute();
        dta.setId(id);
        dta.setDeleteModel(0);
        dta.setUpdateUser(userCode);
        dta.setUpdateTime(new Date());
        //执行删除操作
        return this.dwTargetAttributeMapper.updateById(dta);
    }
}
