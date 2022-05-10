package com.dnt.data.standard.server.model.standard.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.standard.dao.DwTargetMapper;
import com.dnt.data.standard.server.model.standard.entity.DwTarget;
import com.dnt.data.standard.server.model.standard.entity.request.DwTargetRequest;
import com.dnt.data.standard.server.model.standard.service.DwTargetService;
import com.dnt.data.standard.server.model.version.dao.DwVersionDataMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 指标--服务接口实现层 <br>
 * @date: 2021/7/19 下午3:01 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwTargetServiceImpl extends BaseServiceImpl<DwTargetMapper, DwTarget> implements DwTargetService {
    @Autowired
    private DwTargetMapper dwTargetMapper;

    /**
     * 获取指标分页数据
     * @param request
     * @return
     */
    @Override
    public IPage<DwTarget> selectTargetPage(DwTargetRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetServiceImpl-->selectTargetPage 查询指标分页数据信息");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwTarget> page = new Page<>(pn,ps);
        QueryWrapper<DwTarget> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        IPage<DwTarget> res = this.dwTargetMapper.selectTargetPage(page,q);
        res.getRecords().forEach(db0->{
            Integer releaseStatus = Optional.fromNullable(db0.getReleaseStatus()).isPresent()?db0.getReleaseStatus():0;

            db0.setReleaseStatusStr(ReleaseStatusEnum.getValue(releaseStatus));
        });
        return res;
    }

    /**
     * 指标数据详情
     * @param id
     * @return
     */
    @Override
    public DwTarget detailTarget(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetServiceImpl-->detailTarget 查看指标数据详情");
        }
        DwTarget dt = this.dwTargetMapper.selectById(id);
        if(ObjectUtils.isNotEmpty(dt)) {
            Long cid = dt.getCategoryId();
            dt.setCategoryName(getCategoryNameById(cid));
        }
        return dt;
    }

    /**
     * 查询时间周期修饰
     * @param projectId
     * @return
     */
    @Override
    public Map<String,List<Map<String,Object>>> selectAttributeItem(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetServiceImpl-->selectAttributeItem 查询时间周期修饰");
        }
        log.info("查询指定项目下的数据信息：{}",projectId);
        //时间
        List<Map<String,Object>> timeAttributes = this.dwTargetMapper.selectAttributeByType(1,projectId);
        //业务
        List<Map<String,Object>> serviceAttributes = this.dwTargetMapper.selectAttributeByType(2,projectId);
        //原子
        List<Map<String,Object>> atomAttributes = this.dwTargetMapper.selectAttributeByType(3,projectId);
        Map<String,List<Map<String,Object>>> res = new LinkedHashMap<>();
        res.put("timeAttribute",timeAttributes);
        res.put("serviceAttribute",serviceAttributes);
        res.put("atomAttribute",atomAttributes);
        return res;
    }

    /**
     * 查询质量校验函数下拉列表
     * @param projectId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectFunctionItem(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetServiceImpl-->selectFunctionItem 查询质量校验函数下拉列表");
        }

        return this.dwTargetMapper.selectFunctionItem(projectId);
    }

    /**
     * 保存指标信息
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R saveTarget(DwTargetRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwTargetServiceImpl-->saveTarget 添加指标信息");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加信息时，编号不能为空");
        }

        List<DwTarget> lists = findDwTargetByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加指标信息时名称已存在");
        }
        //NO.1 初始化数据
        DwTarget dt = new DwTarget();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dt);
        dt.setCreateUser(userCode);
        //NO.2 更新数据
        this.dwTargetMapper.insert(dt);
        return Result.ok("添加数据成功");
    }

    /**
     * 更新指标信息
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R updateTarget(DwTargetRequest request, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwTargetServiceImpl-->updateTarget 修改指标信息");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改信息时，编号不能为空");
        }

        //NO.1 初始化数据
        DwTarget dt = new DwTarget();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dt);
        //NO.2 更新数据
        DwTarget targetDb = this.baseMapper.selectById(request.getId());
        Integer dbReleaseStatus = Optional.fromNullable(targetDb.getReleaseStatus()).isPresent()?targetDb.getReleaseStatus():ReleaseStatusEnum.UNRELEASE.getCode();
        //当数据库中的数据发布状态为 未发布则不变撞他 如果是已发布则变为 已更新
        dt.setReleaseStatus(dbReleaseStatus==ReleaseStatusEnum.UNRELEASE.getCode()?ReleaseStatusEnum.UNRELEASE.getCode():ReleaseStatusEnum.RELEASEUPDATE.getCode());
        dt.setUpdateUser(userCode);
        dt.setUpdateTime(new Date());
        int dtu = this.dwTargetMapper.updateById(dt);
        //使用Lambda表达式，实现多线程
        new Thread(()->{
            DwVersionDataMapper dwVersionDataMapper = applicationContext.getBean(DwVersionDataMapper.class);
            DwVersionData d = insertVersionHistoryLog("dw_target",dt);
            log.info(Thread.currentThread().getName()+"另一个线程增加更新日志信息");
            dwVersionDataMapper.insert(d);
        }).start();

        return Result.ok("更新成功了"+dtu+"条数据");
    }

    private DwVersionData insertVersionHistoryLog(String tableName, DwTarget dbMn) {
        DwVersionData d = new DwVersionData();
        d.setProjectId(dbMn.getProjectId());
        d.setTableName(tableName);
        d.setDataId(dbMn.getId());
        d.setDataCategoryId(dbMn.getCategoryId());
        d.setDataName(dbMn.getName());
        d.setDataDescription("");
        d.setDataAlias(dbMn.getAlias());
        d.setDataCode(dbMn.getCode());
        d.setOperationFlag("update");
        d.setOperationInfo("更新数据操作成功");
        d.setDataJson(JSON.toJSONString(dbMn));
        d.setDataCreateUser(dbMn.getCreateUser());
        d.setDataCreateTime(dbMn.getCreateTime());
        d.setDataUpdateUser(dbMn.getUpdateUser());
        d.setDataUpdateTime(dbMn.getUpdateTime());
        d.setCreateTime(new Date());
        d.setDataReleaseStatus(2);
        return d;
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwTarget> findDwTargetByName(String name, Long categoryId) {
        QueryWrapper<DwTarget> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwTargetMapper.selectList(q);
    }

    /**
     * 删除指标
     * @param id
     * @param userCode
     * @return
     */
    @Override
    public int deleteTarget(Long id, String userCode) {
        //NO.1 构建数据
        DwTarget t = new DwTarget();
        t.setId(id);
        t.setDeleteModel(0);
        t.setUpdateUser(userCode);
        t.setUpdateTime(new Date());
        //NO.2 执行删除操作
        return this.dwTargetMapper.updateById(t);
    }
}
