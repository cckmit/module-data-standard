package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwSourceMapper;
import com.dnt.data.standard.server.model.mould.entity.DwSource;
import com.dnt.data.standard.server.model.mould.entity.request.DwSourceRequest;
import com.dnt.data.standard.server.model.mould.service.DwSourceService;
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

/**
 * @description: 来源系统--服务接口实现层 <br>
 * @date: 2021/7/28 下午1:05 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwSourceServiceImpl extends BaseServiceImpl<DwSourceMapper, DwSource> implements DwSourceService {
    @Autowired
    private DwSourceMapper dwSourceMapper;
    /**获取数来源系统分页列表**/
    @Override
    public IPage<DwSource> selectSourcePage(DwSourceRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwSourceServiceImpl-->selectSourcePage 获取数来源系统分页列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwSource> page = new Page<>(pn,ps);
        QueryWrapper<DwSource> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        return this.dwSourceMapper.selectSourcePage(page,q);
    }
    /**查看详情**/
    @Override
    public DwSource detailSource(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwSourceServiceImpl-->detailSource 查看详情");
        }
        DwSource res = this.dwSourceMapper.selectById(id);
        Long cid = res.getCategoryId();
        res.setCategoryName(getCategoryNameById(cid));
        return res;
    }
    /**添加来源系统**/
    @Override
    public R saveSource(DwSourceRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwSourceServiceImpl-->saveSource 添加来源系统");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加来源系统时，来源系统的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加来源系统时，来源系统的编号不能为空");
        }

        //NO.1 判断名称信息是否存在
        List<DwSource> lists = findDwSourceByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加来源系统时名称已存在");
        }
        DwSource ds = new DwSource();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ds);
        ds.setCreateTime(new Date());
        ds.setCreateUser(userCode);
        int ac = this.dwSourceMapper.insert(ds);
        log.info("成功添加{}条来源系统数据",ac);

        return Result.ok("添加来源系统操作成功");
    }
    /**修改来源系统**/
    @Override
    public R updateSource(DwSourceRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwSourceServiceImpl-->updateSource 修改来源系统");
        }

        //NO.1 业务字段非空判断
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改来源系统时，来源系统的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改来源系统时，来源系统的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改来源系统时，来源系统的编号不能为空");
        }
        //NO.2 构建数据
        DwSource ds = new DwSource();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ds);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        int i = this.dwSourceMapper.updateById(ds);
        log.info("修改来源系统{}条数据",i);
        return Result.ok("修改来源系统操作成功");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwSource> findDwSourceByName(String name, Long categoryId) {
        QueryWrapper<DwSource> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwSourceMapper.selectList(q);
    }

    /**删除来源系统**/
    @Override
    public int deleteSource(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwSourceServiceImpl-->deleteSource 删除来源系统");
        }
        //NO.1 构建数据
        DwSource ds = new DwSource();
        ds.setId(id);
        ds.setDeleteModel(0);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        //NO.2执行操作
        return this.dwSourceMapper.updateById(ds);
    }
}
