package com.dnt.data.standard.server.model.mould.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.entity.request.DwPublicMouldRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDataElementTreeResponse;
import com.dnt.data.standard.server.model.mould.dao.DwPublicMouldMapper;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMould;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMouldField;
import com.dnt.data.standard.server.model.mould.service.DwPublicMouldService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @description: 公共字段模型--服务接口实现层 <br>
 * @date: 2021/7/29 下午4:30 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwPublicMouldServiceImpl extends BaseServiceImpl<DwPublicMouldMapper, DwPublicMould> implements DwPublicMouldService {
    @Autowired
    private DwPublicMouldMapper dwPublicMouldMapper;

    /**查询公共字段模型下拉列表**/
    @Override
    public Map<String, List<Map<String, Object>>> selectPublicMouldItem(Long dataElementCategoryId) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->selectPublicMouldItem 查询公共字段模型下拉列表");
        }
        //NO.1 构建数据
        Map<String,List<Map<String, Object>>> msList =new HashMap<>();
        msList.put("dataElements",buildDataElements(dataElementCategoryId));
        msList.put("dataTypes",doBuildDataTypes()); /**数据类型**/
        return msList;
    }
    /**获取公共字段模型分页列表**/
    @Override
    public IPage<DwPublicMould> selectPublicMouldPage(DwPublicMouldRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->selectPublicMouldPage 获取公共字段模型分页列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwPublicMould> page = new Page<>(pn,ps);
        QueryWrapper<DwPublicMould> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .eq(Optional.fromNullable(request.getTypeId()).isPresent(),"mf.field_type",request.getTypeId())
                .orderByDesc("a.id");

        //执行查询
        return this.dwPublicMouldMapper.selectPublicMouldPage(page,q);
    }
    /**查看详情**/
    @Override
    public DwPublicMould detailPubicMould(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->detailPubicMould 查看详情");
        }

        DwPublicMould pm = this.dwPublicMouldMapper.selectById(id);
        //构建 公共字段模型 关联的字段信息

        QueryWrapper<DwPublicMouldField> q = Wrappers.query();
        q.eq("delete_model",1).eq("public_mould_id",id);
        List<DwPublicMouldField> fields = this.dwPublicMouldMapper.selectMouldFields(q);
        if(CollectionUtils.isEmpty(fields)){
            fields= new LinkedList<>();
        }
        pm.setFields(fields);

        Long cid = pm.getCategoryId();
        pm.setCategoryName(getCategoryNameById(cid));
        return pm;
    }

    /**
     * 添加公共字段模型
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R savePublicMould(DwPublicMouldRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->savePublicMould 添加公共字段模型");
        }

        //NO.1 判断名称是否为空
        //名称
        String name =request.getName();
        if(!Optional.fromNullable(name).isPresent()){
            return Result.fail("添加公共字段模型时字段集名称不能为空");
        }

        List<DwPublicMould> lists = findDwPublicMouldByName(name,request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加公共字段模型时字段集名称已存在");
        }
        //NO.2 构建数据
        DwPublicMould pm = new DwPublicMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,pm);
        pm.setCreateUser(userCode);
        pm.setCreateTime(new Date());

        //NO.3 数据入库
        int ii = this.dwPublicMouldMapper.insert(pm);
        //NO.4 构建公共字段模型关联字段
        int ik =0;
        Long pmId = pm.getId();
        List<DwPublicMouldField> pmfList = request.getFields();
        if(CollectionUtils.isNotEmpty(pmfList)){
            pmfList.forEach(f->{
                f.setId(IdWorker.getId());
                f.setPublicMouldId(pmId);
                f.setCreateUser(userCode);
                f.setCreateTime(new Date());
            });

            //NO.5 关联字段不为空的时候 批量添加
            ik = this.dwPublicMouldMapper.insertPublicMouldFieldBatch(pmfList);
        }
        return Result.ok("公共字段模型成功添加"+ii+
                "条数据\n 公共字段模型关联字段添加" +ik+
                "条数据");
    }
    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwPublicMould> findDwPublicMouldByName(String name, Long categoryId) {
        QueryWrapper<DwPublicMould> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwPublicMouldMapper.selectList(q);
    }

    /**修改公共字段模型**/
    @Override
    public R updatePublicMould(DwPublicMouldRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->updatePublicMould 修改公共字段模型");
        }

        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改信息时，名称不能为空");
        }
        //NO.1 初始化数据
        DwPublicMould pm = new DwPublicMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,pm);
        pm.setUpdateUser(userCode);
        pm.setUpdateTime(new Date());
        //NO.2 更新数据
        int uf = this.dwPublicMouldMapper.updateById(pm);
        List<DwPublicMouldField> pmfList = request.getFields();
        List<DwPublicMouldField> insertList = new ArrayList<>();
        List<DwPublicMouldField> updateList = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(pmfList)){
            //NO.3 处理更新与新增加数据
            pmfList.forEach(f -> {
                Long fid = f.getId();
                if (Optional.fromNullable(fid).isPresent()) {
                    f.setUpdateUser(userCode);
                    f.setUpdateTime(new Date());
                    updateList.add(f);
                }else{
                    f.setId(IdWorker.getId());
                    f.setPublicMouldId(request.getId());
                    f.setCreateUser(userCode);
                    f.setCreateTime(new Date());
                    insertList.add(f);
                }
            });
        }

        int ii = 0,iu=0;
        //批量插入公共字段模型关联字段
        if(CollectionUtils.isNotEmpty(insertList)){
            ii = this.dwPublicMouldMapper.insertPublicMouldFieldBatch(insertList);
        }
        //批量更新公共字段模型关联字段
        if(CollectionUtils.isNotEmpty(updateList)){
            iu = this.dwPublicMouldMapper.updatePublicMouldFieldBatch(updateList);
        }

        return Result.ok("更新公共字段模型数据记录为"+uf+
                "条 \n 更新公共字段模型关联字段数据记录为"+iu+
                "条 \n 插入公共字段模型关联字段数据记录为"+ii+"条");
    }
    /**删除公共字段模型**/
    @Override
    public int deletePublicMould(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwPublicMouldServiceImpl-->deletePublicMould 删除公共字段模型");
        }
        //NO.1 构建数据
        DwPublicMould pm = new DwPublicMould();
        pm.setId(id);
        pm.setDeleteModel(0);
        pm.setUpdateTime(new Date());
        pm.setUpdateUser(userCode);
        //NO.2 执行删除操作
        return this.dwPublicMouldMapper.updateById(pm);
    }

    /**查询数据元列表的目录树**/
    @Override
    public List<DwDataElementTreeResponse> selectDataElementCategoryTree() {
        //查询 数据元 的目录数据
        QueryWrapper<DwCategory> cq = Wrappers.query();
        cq.eq("a.delete_model",1).eq("a.dw_type","data_element");
        List<DwDataElementTreeResponse> tt = dwPublicMouldMapper.selectDataElementCategories(cq);
        //构建树型目录
        return buildDETreeCategory(tt);
    }

    /**构建树型数据**/
    List<DwDataElementTreeResponse> buildDETreeCategory(List<DwDataElementTreeResponse> list) {
        List<DwDataElementTreeResponse> dwCate = new ArrayList<>();
        // 把分类组成树结构
        list.forEach(parentCate->{
            Long parentId = ObjectUtils.isEmpty(parentCate.getParentId())?0:parentCate.getParentId();
            Long cId = parentCate.getId();
            if(parentId.longValue()==0L) {
                dwCate.add(parentCate);
            }
            list.forEach(childCate->{
                Long childFid = childCate.getId();
                Long childPid = childCate.getParentId();
                if(!cId.equals(childFid)){
                    if(cId.equals(childPid)){
                        List<DwDataElementTreeResponse> childs = parentCate.getChilds();
                        if (childs==null) {
                            childs = new ArrayList<>();
                        }
                        childs.add(childCate);
                        parentCate.setChilds(childs);
                    }
                }
            });

        });
        return dwCate;
    }

    /**构建 查询数据元下拉列表**/
    private List<Map<String, Object>> buildDataElements(Long dataElementCategoryId) {
        return this.dwPublicMouldMapper.selectDataElements(dataElementCategoryId);
    }

    /**
     * 构建数据类型
     * @return
     */
    private List<Map<String, Object>> doBuildDataTypes() {
        //NO.1 构建查询条件
        return this.dwPublicMouldMapper.selectMouldFieldTypes();
    }
}
