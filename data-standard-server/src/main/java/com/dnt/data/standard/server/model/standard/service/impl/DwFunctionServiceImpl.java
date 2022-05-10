package com.dnt.data.standard.server.model.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.dao.DwFunctionMapper;
import com.dnt.data.standard.server.model.standard.entity.DwFunction;
import com.dnt.data.standard.server.model.standard.entity.request.DwFunctionRequest;
import com.dnt.data.standard.server.model.standard.service.DwFunctionService;
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
 * @description: 函数--服务接口实现层 <br>
 * @date: 2021/7/19 下午2:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwFunctionServiceImpl extends BaseServiceImpl<DwFunctionMapper, DwFunction> implements DwFunctionService {
    @Autowired
    private DwFunctionMapper dwFunctionMapper;

    /**获取函数分页数据**/
    @Override
    public IPage<DwFunction> selectFunctionPage(DwFunctionRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwFunctionServiceImpl.selectFunctionPage");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwFunction> page = new Page<>(pn,ps);
        QueryWrapper<DwFunction> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");


        return this.dwFunctionMapper.selectFunctionPage(page,q);
    }
    /**函数详情**/
    @Override
    public DwFunction detailFunction(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwFunctionServiceImpl-->detailFunction 查看函数详情信息");
        }
        DwFunction dwFunction = this.dwFunctionMapper.selectById(id);
        Long categoryId = dwFunction.getCategoryId();
        dwFunction.setCategoryName(getCategoryNameById(categoryId));
        return dwFunction;
    }
    /**保存函数**/
    @Override
    public R saveFunction(DwFunctionRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            System.out.println("DwFunctionServiceImpl-->saveFunction 添加函数信息");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加信息时，编号不能为空");
        }
        //NO.1 初始化数据
        DwFunction df = new DwFunction();
        BeanValueTrimUtil.beanValueTrim(df);
        BeanUtils.copyProperties(request,df);

        List<DwFunction> lists = findDwFunctionByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加函数时名称已存在");
        }
        //NO.2 更新数据
        df.setCreateUser(userCode);
        this.dwFunctionMapper.insert(df);
        return Result.ok("添加函数成功");
    }
    /**更新函数信息**/
    @Override
    public R updateFunction(DwFunctionRequest request, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwFunctionServiceImpl-->updateFunction 更新函数信息");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改信息时，编号不能为空");
        }
        //NO.1 初始化数据
        DwFunction df = new DwFunction();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,df);
        //NO.2 更新数据
        df.setUpdateUser(userCode);
        df.setUpdateTime(new Date());
        int dfu = this.dwFunctionMapper.updateById(df);
        return Result.ok("更新成功了"+dfu+"条数据");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwFunction> findDwFunctionByName(String name, Long categoryId) {
        QueryWrapper<DwFunction> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwFunctionMapper.selectList(q);
    }
    /**删除函数信息**/
    @Override
    public int deleteFunction(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwFunctionServiceImpl-->deleteFunction 删除函数信息");
        }
        //NO.1 构建删除函数对象
        DwFunction df = new DwFunction();
        df.setId(id);
        df.setDeleteModel(0);
        df.setUpdateUser(userCode);
        df.setUpdateTime(new Date());
        //NO.2 执行删除操作
        return this.dwFunctionMapper.updateById(df);
    }
}
