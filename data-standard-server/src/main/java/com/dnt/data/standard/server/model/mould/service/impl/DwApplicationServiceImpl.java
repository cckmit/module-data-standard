package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwApplicationMapper;
import com.dnt.data.standard.server.model.mould.entity.DwApplication;
import com.dnt.data.standard.server.model.mould.entity.request.DwApplicationRequest;
import com.dnt.data.standard.server.model.mould.service.DwApplicationService;
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
 * @description: 所属应用--服务接口实现层 <br>
 * @date: 2021/7/28 下午1:05 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwApplicationServiceImpl extends BaseServiceImpl<DwApplicationMapper, DwApplication> implements DwApplicationService {
    @Autowired
    private DwApplicationMapper dwApplicationMapper;
    //获取所属应用分页列表
    @Override
    public IPage<DwApplication> selectApplicationPage(DwApplicationRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwApplicationServiceImpl-->selectApplicationPage 获取所属应用分页列表");
        }
        /**页数**/
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwApplication> page = new Page<>(pn,ps);
        QueryWrapper<DwApplication> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");


        return this.dwApplicationMapper.selectApplicationPage(page,q);
    }
    /**查看详情**/
    @Override
    public DwApplication detailApplication(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwApplicationServiceImpl-->detailApplication 查看详情");
        }
        /**NO.1 根据ID查询数据并返回**/
        DwApplication da = this.dwApplicationMapper.selectById(id);
        Long categoryId = da.getCategoryId();
        da.setCategoryName(getCategoryNameById(categoryId));
        return da;
    }
    /**添加所属应用**/
    @Override
    public R saveApplication(DwApplicationRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwApplicationServiceImpl-->saveApplication 添加所属应用");
        }

        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加所属应用时，所属应用的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加所属应用时，所属应用的编号不能为空");
        }
        //NO.1 判断名称信息是否存在
        List<DwApplication> lists = findDwApplicationByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加所属应用时名称已存在");
        }
        DwApplication a = new DwApplication();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,a);
        a.setCreateTime(new Date());
        a.setCreateUser(userCode);
        int ac = this.dwApplicationMapper.insert(a);
        log.info("成功添加{}条所属应用数据",ac);

        return Result.ok("添加所属应用操作成功");
    }

    /**修改所属应用**/
    @Override
    public R updateApplication(DwApplicationRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwApplicationServiceImpl-->updateApplication 修改所属应用");
        }

        //NO.1 业务字段非空判断
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改所属应用时，所属应用的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改所属应用时，所属应用的名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改所属应用时，所属应用的编号不能为空");
        }

        //NO.2 构建数据
        DwApplication da = new DwApplication();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,da);
        da.setUpdateTime(new Date());
        da.setUpdateUser(userCode);
        int i = this.dwApplicationMapper.updateById(da);
        log.info("修改所属应用{}条数据",i);
        return Result.ok("修改所属应用操作成功");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwApplication> findDwApplicationByName(String name, Long categoryId) {
        QueryWrapper<DwApplication> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwApplicationMapper.selectList(q);
    }

    /**删除所属应用**/
    @Override
    public int deleteApplication(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwApplicationServiceImpl-->deleteApplication 删除所属应用");
        }
        //NO.1 构建数据
        DwApplication a = new DwApplication();
        a.setId(id);
        a.setDeleteModel(0);
        a.setUpdateTime(new Date());
        a.setUpdateUser(userCode);
        //NO.2 执行操作
        return this.dwApplicationMapper.updateById(a);
    }
}
