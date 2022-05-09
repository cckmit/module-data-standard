package com.dnt.data.standard.server.model.mould.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwMouldCategoryMapper;
import com.dnt.data.standard.server.model.mould.dao.DwMouldNameMapper;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.entity.DwMouldName;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldNameRequest;
import com.dnt.data.standard.server.model.mould.service.DwMouldNameService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.version.dao.DwVersionDataMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
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
import java.util.Map;

/**
 * @description: 模型命名规则--服务接口实现层 <br>
 * @date: 2021/8/4 下午3:35 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMouldNameServiceImpl extends BaseServiceImpl<DwMouldNameMapper, DwMouldName> implements DwMouldNameService {

    @Autowired
    private DwMouldCategoryMapper dwMouldCategoryMapper;

    /**模型命名规则的目录下拉列表**/
    @Override
    public List<Map<String, Object>> selectCatalogueItem() {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->selectCatalogueItem 模型命名规则的目录下拉列表");
        }
        return this.baseMapper.selectCatalogueItem();
    }
    /**模型命名规则的二级目录下拉列表**/
    @Override
    public List<Map<String, Object>> selectTwoCatalogueItem(Long oneCatalogueId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->selectTwoCatalogueItem 模型命名规则的二级目录下拉列表");
        }
        return this.baseMapper.selectTwoCatalogueItem(oneCatalogueId);
    }

    /**模型规则页自定义下拉列表**/
    @Override
    public List<DwMouldCategory> selectCustomMouldNameItem() {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->selectCustomMouldNameItem 模型规则页自定义下拉列表");
        }
        /**NO.1 查询一级目录与二级目录的数据**/
        QueryWrapper<DwMouldCategory> q = Wrappers.query();
        q.select("id","name","parent_id","is_leaf","path","level")
                .eq("delete_model",1);
        List<DwMouldCategory> list = this.dwMouldCategoryMapper.selectList(q);
        /**一级二级目录**/
        List<DwMouldCategory> res = buildTreeCategory(list,1);


        return res;

    }

    /**获取模型命名规则分页列表**/
    @Override
    public IPage<DwMouldName> selectMouldNamePage(DwMouldNameRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->selectMouldNamePage 获取模型命名规则分页列表");
        }
        /**页数**/
        Integer pn = request.getPageNum();
        /**每页显示的记录数**/
        Integer ps = request.getPageSize();
        Page<DwMouldName> page = new Page<>(pn,ps);
        QueryWrapper<DwMouldName> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");
        IPage<DwMouldName> mnList = this.baseMapper.selectMouldNamePage(page,q);
        mnList.getRecords().forEach(mn->{
            Integer rs = mn.getReleaseStatus();
            mn.setReleaseStatusStr(ReleaseStatusEnum.getValue(rs));
        });

        return mnList;
    }
    /**查看详情**/
    @Override
    public DwMouldName detailMouldName(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->detailMouldName  查看详情");
        }
        DwMouldName res = baseMapper.selectById(id);
        if(!Optional.fromNullable(res).isPresent()){
            return new DwMouldName();
        }
        Long cid = res.getCategoryId();
        res.setCategoryName(getCategoryNameById(cid));
        return res;
    }
    /**添加模型命名规则**/
    @Override
    public R saveMouldName(DwMouldNameRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->saveMouldName 添加模型命名规则");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加模型命名规则时，模型命名规则的名称不能为空");
        }
        /**NO.1 检查名称是否重复**/
        List<DwMouldName> lists = findDwMouldNameByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加模型命名规则时名称已存在");
        }
        List<Object> selectItems = request.getSelectItem();
        List<Object> selectItemNames = request.getSelectItemName();
        /**NO.2 构建数据**/
        DwMouldName mn = new DwMouldName();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,mn);
        if(CollectionUtils.isNotEmpty(selectItems)) {
            mn.setSelectItem(StringUtils.join(selectItems, ","));
        }
        if(CollectionUtils.isNotEmpty(selectItemNames)) {
            mn.setSelectItemName(StringUtils.join(selectItemNames, ","));
        }
        mn.setCreateTime(new Date());
        mn.setCreateUser(userCode);

        int i= this.baseMapper.insert(mn);
        log.info("成功添加{}条模型命名规则数据",i);

        return Result.ok("添加模型命名规则操作成功");
    }
    /**修改模型命名规则**/
    @Override
    public R updateMouldName(DwMouldNameRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->updateMouldName 修改模型命名规则");
        }
        /**NO.1 判断数据是否为空**/
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型命名规则时，模型命名规则的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改模型命名规则时，模型命名规则的名称不能为空");
        }

        DwMouldName na = new DwMouldName();
        List<Object> sItems = request.getSelectItem();
        List<Object> sItemNames = request.getSelectItemName();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,na);
        if(CollectionUtils.isNotEmpty(sItems)) {
            na.setSelectItem(StringUtils.join(sItems, ","));
        }
        if(CollectionUtils.isNotEmpty(sItemNames)) {
            na.setSelectItemName(StringUtils.join(sItemNames, ","));
        }
        DwMouldName dbMn = this.baseMapper.selectById(request.getId());
        Integer dbReleaseStatus = Optional.fromNullable(dbMn.getReleaseStatus()).isPresent()?dbMn.getReleaseStatus():ReleaseStatusEnum.UNRELEASE.getCode();
        //当数据库中的数据发布状态为 未发布则不变撞他 如果是已发布则变为 已更新
        na.setReleaseStatus(dbReleaseStatus==ReleaseStatusEnum.UNRELEASE.getCode()?ReleaseStatusEnum.UNRELEASE.getCode():ReleaseStatusEnum.RELEASEUPDATE.getCode());
        na.setUpdateTime(new Date());
        na.setUpdateUser(userCode);
        int i = this.baseMapper.updateById(na);
        log.info("修改模型命名规则{}条数据",i);
        //使用Lambda表达式，实现多线程
        new Thread(()->{
            DwVersionDataMapper dwVersionDataMapper = applicationContext.getBean(DwVersionDataMapper.class);
            DwVersionData d = insertVersionHistoryLog("dw_mould_name",na);
            log.info(Thread.currentThread().getName()+"另一个线程增加更新日志信息");
            dwVersionDataMapper.insert(d);
        }).start();
        return Result.ok("修改模型命名规则操作成功");
    }

    private DwVersionData insertVersionHistoryLog(String tableName, DwMouldName dbMn) {
        DwVersionData d = new DwVersionData();
        d.setProjectId(dbMn.getProjectId());
        d.setTableName(tableName);
        d.setDataId(dbMn.getId());
        d.setDataCategoryId(dbMn.getCategoryId());
        d.setDataName(dbMn.getMouldName());
        d.setDataDescription(dbMn.getDescription());
        d.setDataAlias("");
        d.setDataCode("");
        d.setDataReleaseStatus(2);
        d.setOperationFlag("update");
        d.setOperationInfo("更新数据操作成功");
        d.setDataJson(JSON.toJSONString(dbMn));
        d.setDataCreateUser(dbMn.getCreateUser());
        d.setDataCreateTime(dbMn.getCreateTime());
        d.setDataUpdateUser(dbMn.getUpdateUser());
        d.setDataUpdateTime(dbMn.getUpdateTime());
        d.setCreateTime(new Date());
        return d;
    }

    /**删除模型命名规则**/
    @Override
    public int deleteMouldName(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldNameServiceImpl-->deleteMouldName 删除模型命名规则 id为{}的数据",id);
        }
        /**NO.1 构建数据信息**/
        DwMouldName mn = new DwMouldName();
        mn.setId(id);
        mn.setDeleteModel(0);
        mn.setUpdateUser(userCode);
        mn.setUpdateTime(new Date());
        /**NO.2 执行删除数据**/
        return this.baseMapper.updateById(mn);
    }


    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwMouldName> findDwMouldNameByName(String name, Long categoryId) {
        QueryWrapper<DwMouldName> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.baseMapper.selectList(q);
    }



}
