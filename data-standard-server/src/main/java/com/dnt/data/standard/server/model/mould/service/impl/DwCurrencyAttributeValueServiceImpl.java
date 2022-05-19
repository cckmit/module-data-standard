package com.dnt.data.standard.server.model.mould.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwCurrencyAttributeMapper;
import com.dnt.data.standard.server.model.mould.dao.DwCurrencyAttributeValueMapper;
import com.dnt.data.standard.server.model.mould.entity.*;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import com.dnt.data.standard.server.model.mould.entity.DwCurrencyAttributeValue;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwCurrencyAttributeValueRequest;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeValueService;
import com.dnt.data.standard.server.model.mould.service.DwCurrencyAttributeValueService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import com.google.gson.JsonArray;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* *
 * @desc    通用业务属性Value值服务接口实现层
 * @Return:
 * @author: ZZP
 * @date:  2022/5/18 15:37
 * @Version V2.1.0
 */
@Service
@Slf4j
public class DwCurrencyAttributeValueServiceImpl extends BaseServiceImpl<DwCurrencyAttributeValueMapper, DwCurrencyAttributeValue> implements DwCurrencyAttributeValueService {
    @Autowired
    private DwCurrencyAttributeValueMapper dwCurrencyAttributeValueMapper;

    @Autowired
    private DwCurrencyAttributeMapper dwCurrencyAttributeMapper;

    @Override
    public List selectCurrencyAttributeValueTree(DwCurrencyAttributeValueRequest request) {
        QueryWrapper<DwCurrencyAttributeValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("delete_model",1)
                .eq("attribute_id",request.getAttributeId());
        List<DwCurrencyAttributeValue> allList = this.dwCurrencyAttributeValueMapper.selectList(queryWrapper);

        Integer attributeType;
        if(request.getAttributeType()!=null && request.getAttributeType()>0){
            attributeType=request.getAttributeType();
        }else{
            DwCurrencyAttribute dwCurrencyAttribute = this.dwCurrencyAttributeMapper.selectById(request.getAttributeId());
            attributeType = dwCurrencyAttribute.getAttributeType();
        }

        List<DwCurrencyAttributeValue> backList =new ArrayList<>();

        switch (attributeType) {
            case 1: { // 枚举
                /*枚举直接返回*/
                backList=allList;
                break;
            }
            case 2: { // 树形
                /*把查询的数据构建成树型结构*/
                backList = buildTreeAtrributeValue(allList,1);
                break;
            }
            default: {
                break;
            }
        }
        return backList;
    }

    /**构建树型数据**/
    protected List<DwCurrencyAttributeValue> buildTreeAtrributeValue(List<DwCurrencyAttributeValue> list, Integer levelDefault) {
        List<DwCurrencyAttributeValue> dwCate = new ArrayList<>();
        // 把分类组成树结构
        list.forEach(parentCate->{
            Integer leaveValue = parentCate.getLevel();
            Long cId = parentCate.getId();
            if(leaveValue.intValue()==levelDefault.intValue()) {
                dwCate.add(parentCate);
            }
            list.forEach(childCate->{
                Long childFid = childCate.getId();
                Long childPid = childCate.getParentId();
                if(!cId.equals(childFid)){
                    if(cId.equals(childPid)){
                        List<DwCurrencyAttributeValue> childs = parentCate.getChilds();
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

    /**
     * 修改通用业务属性Value值
     **/
    @Override
    public R updateCurrencyAttributeValue(DwCurrencyAttributeValueRequest request, String userCode) {
        if (log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeValueServiceImpl-->updateCurrencyAttributeValue 修改通用业务属性Value值");
        }
        Long id = request.getId();
        String attributeValue = request.getAttributeValue();
        /**NO.1 业务字段非空判断**/
        if (!Optional.fromNullable(request.getId()).isPresent()) {
            return Result.fail("修改通用业务属性Value值时，通用业务属性Value值的ID不能为空");
        }
        if (StringUtils.isEmpty(attributeValue)) {
            return Result.fail("修改通用业务属性Value值时，通用业务属性Value值不能为空");
        }
        QueryWrapper<DwCurrencyAttributeValue> query = new QueryWrapper<>();
        query.select("id,project_id,attribute_value")
                .eq("delete_model", 1)
                .eq("project_id", request.getProjectId())
                .eq("attribute_id", request.getAttributeId())
                .eq("attribute_value", request.getAttributeValue());
        List<DwCurrencyAttributeValue> entitys = this.dwCurrencyAttributeValueMapper.selectList(query);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(entitys)) {
            return Result.fail("修改后的属性值已存在");
        }

        /**NO.2 构建数据**/
        DwCurrencyAttributeValue ds = new DwCurrencyAttributeValue();
        ds.setId(request.getId());
        ds.setAttributeValue(attributeValue);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        int i = this.dwCurrencyAttributeValueMapper.updateById(ds);
        log.info("修改通用业务属性Value值{}条数据", i);
        return Result.ok("修改通用业务属性Value值操作成功");
    }


    /**
     * 查看详情
     **/
    @Override
    public DwCurrencyAttributeValue detailCurrencyAttributeValue(Long id) {
        if (log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeValueServiceImpl-->detailCurrencyAttributeValue 查看详情");
        }
        DwCurrencyAttributeValue ca = this.dwCurrencyAttributeValueMapper.selectById(id);
        return ca;
    }

    /**
     * 添加通用业务属性Value值
     **/
    @Override
    public R saveCurrencyAttributeValue(DwCurrencyAttributeValueRequest request, String userCode) {
        if (log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeValueServiceImpl-->saveCurrencyAttributeValue 添加通用业务属性Value值");
        }
        if (StringUtils.isEmpty(request.getAttributeValue())) {
            return Result.fail("添加通用业务属性Value值时，通用业务属性Value值不能为空");
        }


        Long pid = Optional.fromNullable(request.getParentId()).isPresent() ? request.getParentId() : 0L;

        //NO.1 查询输入的名称是否存在
        QueryWrapper<DwCurrencyAttributeValue> query = new QueryWrapper<>();
        query.select("id,project_id,attribute_value")
                .eq("delete_model", 1)
                .eq("project_id", request.getProjectId())
                .eq("attribute_id", request.getAttributeId())
                .eq("attribute_value", request.getAttributeValue());
        List<DwCurrencyAttributeValue> entitys = this.dwCurrencyAttributeValueMapper.selectList(query);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(entitys)) {
            return Result.fail("添加的属性值已存在");
        }
        //NO.2 查询上级目录的数据信息

        // Long pid = request.getParentId();
        DwCurrencyAttributeValue mc = new DwCurrencyAttributeValue();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request, mc);
        if (pid.longValue() == 0L) {
            //为空
            mc.setParentId(0L);
            mc.setLevel(1);
        }
        //设置一些属性值
        mc.setCreateUser(userCode);
        mc.setCreateTime(new Date());
        //NO.3 插入数据信息
        this.dwCurrencyAttributeValueMapper.insert(mc);
        //获取保存后的ID   执行更新  路径  层级 中否为叶子节点信息
        Long id = mc.getId();
        if (pid.longValue() != 0L) {
            //不为空时根据ID查询父级的目录数据
            DwCurrencyAttributeValue tempAc = this.dwCurrencyAttributeValueMapper.selectById(pid);
            String pftPath = tempAc.getPath();
            //根据id更新 路径 是否为叶子节点 层级
            DwCurrencyAttributeValue mcNow = new DwCurrencyAttributeValue();
            mcNow.setId(id);
            mcNow.setPath(pftPath + "," + id);
            mcNow.setLevel(tempAc.getLevel() + 1);
            mcNow.setIsLeaf(1);
            mcNow.setUpdateTime(new Date());
            this.dwCurrencyAttributeValueMapper.updateById(mcNow);
            DwCurrencyAttributeValue mcP = new DwCurrencyAttributeValue();
            mcP.setId(pid);
            //把上一层级设置为非叶子节点
            mcP.setIsLeaf(0);
            mcP.setUpdateTime(new Date());
            this.dwCurrencyAttributeValueMapper.updateById(mcP);
        } else {
            DwCurrencyAttributeValue acNow = new DwCurrencyAttributeValue();
            acNow.setId(id);
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(id);
            acNow.setPath(id + "");
            acNow.setLevel(1);
            acNow.setUpdateTime(new Date());
            this.dwCurrencyAttributeValueMapper.updateById(acNow);
        }

        return Result.ok("添加通用业务属性Value值操作成功");
    }



    /**
     * 根据名称 查询名称是不是存在
     *
     * @param
     * @return
     */
    private List<DwCurrencyAttributeValue> findDwCurrencyAttributeValueByValue(String value, Long projectId) {
        QueryWrapper<DwCurrencyAttributeValue> q = Wrappers.query();
        q.select("id,attribute_value").eq("delete_model", 1)
                .eq("project_id", projectId)
                .eq("attribute_value", value);
        return this.dwCurrencyAttributeValueMapper.selectList(q);
    }

    /**
     * 删除通用业务属性Value值
     **/
    @Override
    public int deleteCurrencyAttributeValue(Long id, String userCode) {
        if (log.isInfoEnabled()) {
            log.info("DwCurrencyAttributeValueServiceImpl-->deleteCurrencyAttributeValue 删除通用业务属性Value值");
        }
        //NO.1 构建数据
        DwCurrencyAttributeValue ds = new DwCurrencyAttributeValue();
        ds.setId(id);
        ds.setDeleteModel(0);
        ds.setUpdateTime(new Date());
        ds.setUpdateUser(userCode);
        //NO.2执行操作
        return this.dwCurrencyAttributeValueMapper.updateById(ds);
    }
}
