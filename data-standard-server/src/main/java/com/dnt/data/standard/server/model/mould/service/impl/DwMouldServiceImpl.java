package com.dnt.data.standard.server.model.mould.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.client.*;
import com.dnt.data.standard.server.model.entity.response.ExcelImportErrorResponse;
import com.dnt.data.standard.server.model.entity.response.ExcelUploadResponse;
import com.dnt.data.standard.server.model.mould.dao.*;
import com.dnt.data.standard.server.model.mould.entity.*;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldPhysicsRequest;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDataElementTreeResponse;
import com.dnt.data.standard.server.model.mould.entity.response.DwMouldResponse;
import com.dnt.data.standard.server.model.mould.service.DwMouldService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.dnt.data.standard.server.web.ResultCode;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @description: 模型管理--服务接口实现层 <br>
 * @date: 2021/8/4 下午4:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMouldServiceImpl extends BaseServiceImpl<DwMouldMapper, DwMould> implements DwMouldService {

    @Autowired
    private DataSourceDevClient dataSourceDevClient;
    @Autowired
    private DataSourceTestClient dataSourceTestClient;
    @Autowired
    private DataSourceProdClient dataSourceProdClient;

    @Autowired
    private ProjectDevClient projectDevClient;
    @Autowired
    private ProjectProdClient projectProdClient;
    @Autowired
    private ProjectTestClient projectTestClient;
    @Autowired
    private DwOperationLogMapper dwOperationLogMapper;
    @Autowired
    private DwMouldPhysicsMapper dwMouldPhysicsMapper;

    @Autowired
    private DwMouldCategoryMapper dwMouldCategoryMapper;

    @Autowired
    private CacheService cacheService;
    @Autowired
    private DwPublicMouldMapper dwPublicMouldMapper;


    /**获取模型分页列表接口**/
    @Override
    public IPage<DwMould> selectDwMouldPage(DwMouldRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectDwMouldPage 获取模型分页列表");
        }

        //NO.1 构建查询条件
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwMould> page = new Page<>(pn,ps);
        QueryWrapper<DwMould> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .eq(Optional.fromNullable(request.getMouldStatus()).isPresent(),"a.mould_status",request.getMouldStatus())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");
        //NO.2 执行查询
        return this.baseMapper.selectDwMouldPage(page,q);
    }

    /**删除模型**/
    @Override
    public R deleteMould(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->deleteMould 删除模型");
        }

        //NO.1 构建数据
        DwMould db = new DwMould();
        db.setId(id);
        db.setDeleteModel(0);
        db.setUpdateTime(new Date());
        db.setUpdateUser(userCode);
        //NO.2 执行删除操作
        int ii= this.baseMapper.updateById(db);
        return Result.ok(String.format("删除模型管理 %s条数据",ii));
    }

    /**选择公共字段集的字段信息**/
    @Override
    public List<Map<String, Object>> selectPublicMouldField( List<Long> publicMouldIds ) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectPublicMouldField 选择公共字段集的字段信息");
        }
        //NO.1 根据选择的公共字段集ID 查询关联的字段
        QueryWrapper<DwPublicMouldField> q = Wrappers.query();
        q.eq("delete_model",1).in("public_mould_id",publicMouldIds);
        return this.baseMapper.selectPublicMouldField(q);
    }
    /**添加手工新建模型**/
    @Override
    public R saveMould(DwMouldRequest request, String userCode) {
        String met="saveMould";
        Long lotNumber =IdWorker.getId();
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->saveMould 添加手工新建模型");
        }

        //NO.1 判断名称是否为空
        if(!Optional.fromNullable(request.getCategoryId()).isPresent()){
            return Result.fail("添加模型时分类目录不能为空");
        }
        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("添加模型时名称不能为空");
        }

        List<DwMould> lists = findDwMouldByName(name,request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加模型时名称已存在");
        }

        //NO.2 构建数据
        DwMould dm = new DwMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dm);
        dm.setMouldType(1); //手工新建模型
        dm.setCreateUser(userCode);
        dm.setCreateTime(new Date());
        //NO.3 数据入库
        int ii = this.baseMapper.insert(dm);
        //NO.4 构建模型关联字段  模型关联分区字段
        int ik =0 ,ip=0;

        Long pmId = dm.getId();
        List<DwMouldField> mfList = request.getFields();

        if(CollectionUtils.isNotEmpty(mfList)){
            mfList.forEach(f->{
                f.setId(IdWorker.getId());
                f.setMouldId(pmId);
                f.setCreateUser(userCode);
                f.setCreateTime(new Date());
            });

            //NO.5 关联字段不为空的时候 批量添加
            ik = this.baseMapper.insertMouldFieldBatch(mfList);
        }

        List<DwMouldFieldPartition> mfpList = request.getFieldPartitions();
        if(CollectionUtils.isNotEmpty(mfpList)){
            mfpList.forEach(f->{
                f.setId(IdWorker.getId());
                f.setMouldId(pmId);
                f.setCreateUser(userCode);
                f.setCreateTime(new Date());
            });

            //批量增加模型关联分区字段
            ip = this.baseMapper.insertMouldFieldPartitionsBatch(mfpList);
        }
        String r = "模型成功添加"+ii+
                "条数据\n 模型关联字段添加" +ik+
                "条数据 \n 模型关联分区字段添加"+ip +"条数据";
        asyncWriteLog(lotNumber,met,1,"DEBUGE",request.getId(),r,userCode,1);
        return Result.ok(r);
    }



    /**修改手工新建模型**/
    @Override
    public R updateMould(DwMouldRequest request, String userCode) {
        String met="updateMould";
        Long lotNumber =IdWorker.getId();
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->updateMould 修改手工新建模型");
        }

        //NO.1 业务字段非空判断
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改模型时，模型的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改模型时，模型名称不能为空");
        }

        Long id = request.getId();
        List<DwMould> lists = findDwMouldByName(request.getName(),request.getCategoryId());

        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lists)){
            //判断名称是否存在
            lists.forEach(na->{
                if(na.getId().longValue()!=id.longValue()){
                    haveNa.set(true);
                    return;
                }

            });
        }
        if(haveNa.get()){
            return Result.fail("修改模型时，名称已存在");

        }


        //NO.2 初始化数据
        DwMould pm = new DwMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,pm);
        pm.setMouldType(1);
        pm.setUpdateUser(userCode);
        pm.setUpdateTime(new Date());
        //NO.2 更新数据
        int uf = this.baseMapper.updateById(pm);

        //模型关联字段
        List<DwMouldField> mfList = request.getFields();
        List<DwMouldField> insertList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(mfList)){
            //NO.3 处理更新与新增加数据
            mfList.forEach(f -> {
                f.setId(IdWorker.getId());
                f.setMouldId(id);
                f.setCreateUser(userCode);
                f.setCreateTime(new Date());
                insertList.add(f);
            });
        }

        //模型关联分区
        List<DwMouldFieldPartition> mfpList = request.getFieldPartitions();
        List<DwMouldFieldPartition> insertPList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(mfpList)){
            //NO.3 处理更新与新增加数据
            mfpList.forEach(ff -> {
                ff.setId(IdWorker.getId());
                ff.setMouldId(id);
                ff.setCreateUser(userCode);
                ff.setCreateTime(new Date());
                insertPList.add(ff);

            });
        }


        //先删除
        if(Optional.fromNullable(id).isPresent()) {
            baseMapper.deleteMouldField(id);
        }
        int ifC=0,ifpC=0;
        if(CollectionUtils.isNotEmpty(insertList)){
            //再插入
            ifC = this.baseMapper.insertMouldFieldBatch(insertList);
        }

        //先删除
        if(Optional.fromNullable(id).isPresent()) {
            baseMapper.deleteMouldFieldPartitions(id);
        }

        if(CollectionUtils.isNotEmpty(insertPList)){
            //再插入
            ifpC = this.baseMapper.insertMouldFieldPartitionsBatch(insertPList);
        }

        String r = String.format("模型成功修改%s条数据，关联字段增加了%s条数据，关联分区增加了%s条数据\n",uf,ifC,ifpC);
        asyncWriteLog(lotNumber,met,1,"DEBUGE",request.getId(),r,userCode,1);

        return Result.ok(r);
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwMould> findDwMouldByName(String name, Long categoryId) {

        QueryWrapper<DwMould> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.baseMapper.selectList(q);
    }

    /**==========================================ddl操作接口=======================================**/
    /**ddl建模数据源类型下拉列表接口**/
    @Override
    public JSONArray selectDDLSourceTypeItem(String envFlag) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectDDLSourceTypeItem ddl建模数据源类型下拉列表接口");
        }
        //根据环境与项目信息获取 项目下引入数据源的分类
        JSONObject o = null;
        //NO.1 根据标识调用 不同环境的数据
        if(StringUtils.equals("test",envFlag)){
            o = dataSourceTestClient.selectSourceTypeItem("6");
        }else if(StringUtils.equals("prod",envFlag)){
            o = dataSourceProdClient.selectSourceTypeItem("6");
        }else{
            o = dataSourceDevClient.selectSourceTypeItem("6");
        }
        if(o!=null && o.getInteger("code")==200){
            return o.getJSONArray("data");
        }
        return new JSONArray();
    }
    /**添加ddl建模**/
    @Override
    public R saveDDLMould(DwMouldRequest request, String userCode) {
        String met="saveDDLMould";
        Long lotNumber =IdWorker.getId();
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->saveDDLMould 添加ddl建模");
        }

        String name = request.getName();
        Long categoryId = request.getCategoryId();

        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("添加ddl建模时分类目录不能为空");
        }

        if(StringUtils.isEmpty(name)){
            return Result.fail("添加ddl建模时名称不能为空");
        }

        //NO.1 构建数据
        DwMould m = new DwMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,m);
        //构建模型名称
        m.setMouldType(2);
        m.setCreateUser(userCode);
        m.setCreateTime(new Date());

        String mName = m.getName();
        QueryWrapper<DwMould> wrap = Wrappers.query();
        wrap.select("id","name")
                .eq("category_id",m.getCategoryId())
                .eq("name",mName);
        List<DwMould> moulds = this.baseMapper.selectList(wrap);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(moulds)){
            DwMould dbMould = moulds.get(0);
            m.setId(dbMould.getId());
            this.baseMapper.updateById(m);
        }else {
            int ii = this.baseMapper.insert(m);
            log.info("ddl建模新建了{}条数据", ii);
            asyncWriteLog(lotNumber, met, 1, "DEBUGE", request.getId(), "ddl建模新建了" + ii + "条数据", userCode, 1);
        }

        return Result.ok("DDL语句建模操作成功");
    }
    /**修改ddl建模**/
    @Override
    public R updateDDLMould(DwMouldRequest request, String userCode) {
        String met="updateDDLMould";
        Long lotNumber =IdWorker.getId();
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->updateDDLMould 修改ddl建模");
        }
        Long id = request.getId();
        //编辑的时候名称不修改
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("编辑ddl语句建模时ID不能为空");
        }
        String name = request.getName();
        Long categoryId = request.getCategoryId();

        if(!Optional.fromNullable(categoryId).isPresent()){
            return Result.fail("添加ddl建模时分类目录不能为空");
        }

        if(StringUtils.isEmpty(name)){
            return Result.fail("添加ddl建模时名称不能为空");
        }

        //NO.1 构建数据
        DwMould m = new DwMould();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,m);
        m.setMouldType(2);
        m.setUpdateUser(userCode);
        m.setUpdateTime(new Date());

        int ik = this.baseMapper.updateById(m);
        log.info("ddl语句建模修改了{}条数据",ik);

        asyncWriteLog(lotNumber,met,1,"DEBUGE",request.getId(),"ddl语句建模修改了"+ik+"条数据",userCode,1);
        return Result.ok("修改DDL语句建模操作成功");
    }

    /**==========================================物理化操作=======================================**/
    /**查看默认数据源接口**/
    @Override
    public R detailDefaultDatasource(Long projectId) {
        if(log.isInfoEnabled()){
            log.info("DwMouldServiceImpl-->detailDefaultDatasource 查看默认数据源接口");
        }
        Object o = dataSourceDevClient.getDefaultDataSource("6",projectId);
        return Result.ok(o);
    }
    /**查看指定存储资源下的 项目下拉列表**/
    @Override
    public R selectProjectItem(String envFlag,Long sourceTypeId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectProjectItem 查看指定存储资源下的 项目下拉列表");
        }
        Object o = null;
        //NO.1 根据标识调用 不同环境的数据
        if(StringUtils.equals("test",envFlag)){
            o = projectTestClient.selectProjectItem("6",sourceTypeId);
        }else if(StringUtils.equals("prod",envFlag)){
            o = projectProdClient.selectProjectItem("6",sourceTypeId);
        }else{
            o = projectDevClient.selectProjectItem("6",sourceTypeId);
        }
        return Result.ok(o);
    }
    /**模型物理化**/
    @Override
    public R doMouldPhysics(Long lotNumber,String userCode, DwMouldPhysicsRequest request) {
        String m ="doMouldPhysics";
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->doMouldPhysics 模型物理化操作");
        }
        //NO.1 物理化操作开始
        asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"模型物理化业务操作开始",userCode,1);
        if(!Optional.fromNullable(request.getId()).isPresent()){
            asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"模型物理化时模型ID不能为空",userCode,2);
            return resR("模型物理化时模型ID不能为空");
        }
        //根据不同的环境与项目查询默认的hive库信息
        String envFlag = request.getEnvFlag();
        Long pid = request.getProjectId();
        if(!Optional.fromNullable(pid).isPresent()){
            asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"模型物理化时必须选择项目",userCode,3);
            return resR("模型物理化时必须选择项目");
        }

        JSONObject hiveRes= null;
        if(StringUtils.equals("dev",envFlag)){
            hiveRes = dataSourceDevClient.getDefaultDataSource("6",pid);
        }else if(StringUtils.equals("test",envFlag)){
            hiveRes = dataSourceTestClient.getDefaultDataSource("6",pid);
        }else if(StringUtils.equals("prod",envFlag)){
            hiveRes = dataSourceProdClient.getDefaultDataSource("6",pid);
        }
        log.warn("根据项目信息获取对应的仓库信息为：{}",hiveRes.toString());
        if(!Optional.fromNullable(hiveRes).isPresent()){
            asyncWriteLog(lotNumber,m,3,"ERROR",request.getId(),"查询当前项目下的数据源信息失败，请检查网络",userCode,4);
            log.warn("查询当前项目下的数据源信息失败，请检查网络");
            return resR("查询当前项目下的数据源信息失败，请检查网络");
        }
        Integer code = hiveRes.getInteger("code");
        if(200!=code){
            asyncWriteLog(lotNumber,m,3,"ERROR",request.getId(),"获取当前项目下的数据源失败",userCode,5);
            log.warn("获取当前项目下的数据源失败");
            return resR("获取当前项目下的数据源失败");
        }

        JSONObject souceDataJson = hiveRes.getJSONObject("data");
        if(!Optional.fromNullable(souceDataJson).isPresent()){
            asyncWriteLog(lotNumber,m,3,"ERROR",request.getId(),"获取项目下的数据源配置信息为空",userCode,6);
            log.warn("获取项目下的数据源配置信息为空");
            return resR("获取项目下的数据源配置信息为空");
        }
        
        //项目ID
        Long projectId = souceDataJson.getLong("projectId");
        //项目名
        String projectName = souceDataJson.getString("dataName");
        String projectDesc = souceDataJson.getString("dataDesc");

        JSONObject dataJson = souceDataJson.getJSONObject("dataJson");
        if(dataJson.isEmpty()){
            log.warn("项目没有配置数据仓库");
            return resR("项目没有配置数据仓库");
        }
        String userName = dataJson.getString("username");
        String password = dataJson.getString("password");
        String jdbcUrl = dataJson.getString("jdbcUrl");
        String r = String.format("项目ID：%s 数据源名称：%s HIVE用户名：%s HIVE密码：%s HIVE连接方式：%s",
                projectId,projectName,userName,password,jdbcUrl);

        log.warn("项目下的默认仓库为：{}",r);
        asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"项目下的默认仓库为：" + r,userCode,7);

        //hive 数据驱动
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(jdbcUrl,userName,password));
        asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"构建hive数据连接成功",userCode,8);

        //获取已发布模型的sql
        QueryWrapper<DwMould> mq = Wrappers.query();
        mq.select("id,name,mould_status,mould_type as mouldType,ddl_statement as ddlStatment,release_sql as releaseSql")
                .eq("delete_model",1)
                .eq("mould_status",1)
                .eq("id",request.getId());
        List<Map<String,Object>> mouldMapList = this.baseMapper.selectMaps(mq);
        if(CollectionUtils.isEmpty(mouldMapList)){
            log.warn("模型信息不存在");
            return resR("模型信息不存在");
        }
        Map<String,Object> mouldMap = mouldMapList.get(0);
        String hiveSql = null;
        if(Integer.parseInt(mouldMap.get("mouldType").toString())==1){
            hiveSql= mouldMap.get("releaseSql")+"";
            asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"手工新建模型对应的sql:" +hiveSql ,userCode,9);
        }else{
            hiveSql= mouldMap.get("ddlStatment")+"";
            asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"DDL新建模型对应的sql:" +hiveSql ,userCode,10);
        }
        if(StringUtils.equals("null",hiveSql)){
            asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"构建的模型sql有问题，请检查配置" ,userCode,11);
            log.warn("构建的模型sql有问题，请检查配置");
            return resR("构建的模型sql有问题，请检查配置");
        }

        try {
            //先删除库中已存在的表
            jdbcTemplate.execute("DROP table " + mouldMap.get("name") );
            //再次新建表
            jdbcTemplate.execute(hiveSql);
        } catch (DataAccessException e) {
            e.printStackTrace();
            log.error("执行sql：{}异常",hiveSql);
            return resR("执行sql异常，请检查sql格式");
        }

        asyncWriteLog(lotNumber,m,3,"DEBUGE",request.getId(),"模型物理化业务操作完成",userCode,12);
        log.warn("执行物理化操作完成 ");
        //NO.2 更新模型状态
        Long mouldId = request.getId();
        DwMould mould = new DwMould();
        mould.setId(mouldId);
        mould.setPhysicsStatus(1);
        mould.setUpdateUser(userCode);
        mould.setUpdateTime(new Date());

        int uMould = this.baseMapper.updateById(mould);
        log.warn("更新了模型发布信息记录为{}条", uMould);

        //NO.3 记录物理化模型记录
        // DwMouldPhysics
        DwMouldPhysics physics = new DwMouldPhysics();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,physics);
        physics.setId(IdWorker.getId());
        //模型ID
        physics.setMouldId(mouldId);
        //模型名称
        physics.setMouldName(mouldMap.get("name").toString());
        //jdbc:hive2://172.24.15.4:10000/test_0101
        if(StringUtils.contains(jdbcUrl,":10000/")) {
            String[] spStr =jdbcUrl.split(":10000/");
            if(spStr.length>1) {
                String dbName = spStr[1];
                //物理化时记录库名
                physics.setDbName(dbName);
            }else{
                return resR("数据源中URL中没有配置库名，URL为：" + jdbcUrl);
            }
        }

        //项目ID
        physics.setProjectId(projectId);
        physics.setProjectName(projectName);
        physics.setDescription(projectDesc);
        physics.setHiveJson(dataJson.toJSONString());
        physics.setCreateUser(userCode);
        physics.setCreateTime(new Date());
        QueryWrapper<DwMouldPhysics> phyq = Wrappers.query();
        phyq.select("id,env_flag,mould_id").eq("delete_model",1)
                .eq("mould_id",mouldId)
                .eq("project_id",pid);
        List<Map<String,Object>> lists = this.dwMouldPhysicsMapper.selectMaps(phyq);
        if(CollectionUtils.isNotEmpty(lists)){

            int dphys = this.dwMouldPhysicsMapper.delete(phyq);
            log.warn("模型再次发布时会删除旧的已发布信息，删除了{}条发布信息",dphys);
        }
        //先删除旧的发布信息再插入新的发布信息
        int iik = this.dwMouldPhysicsMapper.insert(physics);


        log.warn("成功添加了物理化数据信息{}条",iik);
        return Result.ok("物理化操作成功");
    }
    /**返回数据**/
    private R resR(String d){
        return new R().setCode(ResultCode.SUCCESS.code()).setMsg("ERROR").setData(d);
    }
    /**
     * 异步写操作日志信息
     * @param methodName
     * @param mouldFlag
     * @param type
     * @param mouldId
     * @param mdesc
     * @param userCode
     */
    private void asyncWriteLog(Long lotNumber,String methodName ,Integer mouldFlag,String type,Long mouldId,String mdesc,String userCode,Integer logOrder){
        DwOperationLog ole = buildOperationLog(lotNumber,methodName,mouldFlag,type,mouldId,mdesc,userCode,logOrder);
        //写日志
        ((DwMouldService)(AopContext.currentProxy())).writeOperationLog(ole);
    }
    /**
     * 构建操作日志信息
     * @param type
     * @param mouldId
     * @param mdesc
     * @param userCode
     * @return
     */
    private DwOperationLog buildOperationLog(Long lotNumber,String methodName ,Integer mouldFlag,String type,Long mouldId,String mdesc,String userCode,Integer logOrder){

        //构建日志信息
        DwOperationLog gg = new DwOperationLog();
        gg.setLotNumber(lotNumber);
        gg.setPlatform("数仓");
        gg.setTitle("模型物理化");
        gg.setClassName("DwMouldServiceImpl");
        gg.setMethodName(methodName);
        gg.setMouldFlag(mouldFlag);
        gg.setType(type);
        gg.setKeyId(mouldId); // 模型ID
        gg.setProtocol("http");
        gg.setProtocolVersion("v1");
        gg.setRequestUrl("/v1/dw/mould/doMouldPhysics");
        gg.setResultDesc(mdesc);
        gg.setStatus(1);
        gg.setLogOrder(logOrder);
        gg.setTimeConsuming(0L);
        gg.setOperationTime(new Date());
        gg.setCreateUser(userCode);
        return gg;
    }

    /**记录操作日志**/
    @Async
    @Override
    @Transactional
    public void writeOperationLog(DwOperationLog ol) {
        if (log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->writeOperationLog 异步记录日志操作");
        }
        if (!Optional.fromNullable(ol).isPresent()) {
            return;
        }

        //执行写日志到数据库表的操作
        ol.setOperationTime(new Date());
        if(StringUtils.isEmpty(ol.getCreateUser())) {
            ol.setCreateUser("auto");
        }
        this.dwOperationLogMapper.insert(ol);
        log.info("DwMouldServiceImpl-->writeOperationLog 异步记录日志操作结束");

    }

    /**
     * 模型发布记录模型相关数据的参数
     * @param dm
     * @param id
     */
    @Async
    @Override
    @Transactional
    public void writeMouldReleaseLog(DwMould dm,Long id) {

    }

    /**根据模型查询物理化的日志信息**/
    @Override
    public List<DwOperationLog> selectMouldPhysicsLog(Long mouldId,Integer mouldFlag) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectMouldPhysicsLog 根据模型查询物理化的日志信息");
        }
        QueryWrapper<DwOperationLog> q =Wrappers.query();
        q.eq("delete_model",1)
                .eq("key_id",mouldId)
                .eq("mould_flag",mouldFlag)
                .groupBy("lot_number")
                .orderByDesc(" lot_number");
        return this.dwOperationLogMapper.selectList(q);
    }

    /**查询物理化操作日志 批次下的详细信息**/
    @Override
    public List<DwOperationLog> selectMouldPhysicsChildLog(Long lotNumber, Integer mouldFlag) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectMouldPhysicsChildLog 查询物理化操作日志 批次下的详细信息");
        }

        QueryWrapper<DwOperationLog> q =Wrappers.query();
        q.eq("delete_model",1)
                .eq("lot_number",lotNumber)
                .eq("mould_flag",mouldFlag)
                .orderByDesc("log_order");
        return this.dwOperationLogMapper.selectList(q);
    }

    /**====================================通用业务属性======================================================**/
    /**通用业务属性通用下拉列表**/
    @Override
    public Map<String, Object> selectMouldCurrencyAttributeItem() {
        if(log.isInfoEnabled()){
            log.info("DwMouldServiceImpl-->selectMouldCurrencyAttributeItem 通用业务属性通用下拉列表");
        }
        Map<String,Object> m = new HashMap<>();
        List<Map<String,Object>>  sources = this.baseMapper.selectCurrencyAttributeList(1);
        List<Map<String,Object>>  applications = this.baseMapper.selectCurrencyAttributeList(2);
        List<Map<String,Object>>  partitions = this.baseMapper.selectCurrencyAttributeList(3);
        List<Map<String,Object>>  tables = this.baseMapper.selectCurrencyAttributeList(4);
        m.put("sources",sources);
        m.put("applications",applications);
        m.put("partitions",partitions);
        m.put("tables",tables);
        return m;
    }
    /**通用业务属性负责人下拉列表**/
    @Override
    public List<Map<String, Object>> selectMouldBossheadItem() {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl.selectMouldBossheadItem 通用业务属性负责人下拉列表");
        }

        return this.baseMapper.selectMouldBossheadItem();
    }
    /**查看模型物理化表数据信息**/
    @Override
    public List<Map<String, Object>> selectMouldTableStructure(Long mouldId) {
        if(log.isInfoEnabled()){
            log.info("DwMouldServiceImpl-->selectMouldTableStructure 查看模型物理化表数据信息 ");
        }
        QueryWrapper<DwMouldPhysics> phyq = Wrappers.query();
        phyq.select("id,env_flag as envFlag,mould_id as mouldId,mould_name as mouldName,project_id as projectId,project_name as projectName ,create_time as createTime ").eq("delete_model",1)
                .eq("mould_id",mouldId);
        List<Map<String,Object>> lists = this.dwMouldPhysicsMapper.selectMaps(phyq);
        log.info("查看模型物理化表对应的项目为：{} 条数据",CollectionUtils.isEmpty(lists)?0:lists.size());
        return lists;
    }
    /**文件批量导入操作**/
    @Async
    @Override
    public R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId) {
        //插入数量
        int insertNum = 0;
        //更新数量
        int updateNum = 0;
        //保持数量
        int keepNum = 0;

        List<ExcelImportErrorResponse> excelErrResps = new ArrayList<>();
        Map<String,Object> resultMap = new HashMap<>();

        try {
            //进度条
            resultMap.put("percent",0);
            //刷新进度值
            cacheService.hmset("mould","excel_import_progress:"+processCode,resultMap,600);

            ExcelReader excelReader = ExcelUtil.getReader(new FileInputStream(uploadFile));
            List<List<Object>> ll = excelReader.read(1,excelReader.getRowCount());

            String mouldName = ObjectUtils.isEmpty(ll.get(0).get(1))?"":ll.get(0).get(1).toString().trim();
            if(StringUtils.isEmpty(mouldName)){
                excelErrResps.add(new ExcelImportErrorResponse(1,null,"模型名称为空"));
            }
            //描述
            String mouldDescription = ObjectUtils.isEmpty(ll.get(2).get(1))?"":ll.get(2).get(1).toString().trim();


            DwMould mouldE = new DwMould();
            mouldE.setCategoryId(categoryId);
            mouldE.setName(mouldName);
            //生命周期
            try {
                Date mouldStorageLifecycle = ObjectUtils.isEmpty(ll.get(1).get(1)) ? null : DateUtils.parseDate(ll.get(1).get(1).toString().trim(), "yyyy-MM-dd HH:mm:ss");
                mouldE.setStorageLifecycle(mouldStorageLifecycle);
            }catch (Exception e){
                excelErrResps.add(new ExcelImportErrorResponse(2,null,"存储生命周期为日期格式(yyyy-MM-dd HH:mm:ss)"));

                log.error("DwMouldServiceImpl-->uploadExcel 上传数据excel解析异常 ",e);
                e.printStackTrace();
                resultMap.put("percent",-1);
                resultMap.put("message","批量导入完成：<br/>1. 导入成功"+(insertNum+ updateNum+keepNum)+"条数据：其中新增"+insertNum+"条，有内容更新"
                        +updateNum+"条，无内容更新"+keepNum+"条；<br/>2. 导入失败"+excelErrResps.size() +"条知识，失败列表如下：");
                resultMap.put("result",excelErrResps);
                //刷新进度值
                cacheService.hmset("mould","excel_import_progress:"+processCode,resultMap,600);

                return Result.fail("导入失败");
            }
            mouldE.setDescription(mouldDescription);


            QueryWrapper<DwMould> dq = Wrappers.query();
            dq.select("id,name")
                    .eq("delete_model",1)
                    .eq("name",mouldName).eq("category_id",categoryId);
            List<DwMould> mouldList = this.baseMapper.selectList(dq);
            if(org.apache.commons.collections4.CollectionUtils.isEmpty(mouldList)){
                //增加
                insertNum++;
                mouldE.setCreateUser(userCode);
                mouldE.setCreateTime(new Date());
                //增加字典
                this.baseMapper.insert(mouldE);
                Long mId = mouldE.getId();
                //插入字典关联的字段
                Map<String,List> mFields = doBuildMouldFieldMap(processCode,userCode,projectId,mId,ll,"insert");
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("insert")){
                    List<DwMouldField> insertFields =  mFields.get("insert");
                    this.baseMapper.insertMouldFieldBatch(insertFields);

                }
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("update")){
                    List<DwMouldField> updateFields =  mFields.get("update");
                    updateFields.forEach(uf->{
                        List<DwMouldField> mfs = new ArrayList<>();
                        mfs.add(uf);
                        this.baseMapper.updateMouldFieldBatch(mfs);
                    });
                }
                if(MapUtils.isNotEmpty(mFields)){
                    List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                    excelErrResps.addAll(errorMsg);
                }


            }else{
                //更新
                updateNum++;
                Long dictId = mouldList.get(0).getId();
                mouldE.setId(dictId);
                mouldE.setUpdateUser(userCode);
                mouldE.setUpdateTime(new Date());
                //更新字典
                this.baseMapper.updateById(mouldE);


                //插入 与更新 字典关联字段
                Map<String,List> mFields = doBuildMouldFieldMap(processCode,userCode,projectId,dictId,ll,"update");
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("insert")){
                    List<DwMouldField> insertFields =  mFields.get("insert");
                    this.baseMapper.insertMouldFieldBatch(insertFields);

                }
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("update")){
                    List<DwMouldField> updateFields =  mFields.get("update");
                    updateFields.forEach(uf->{
                        List<DwMouldField> mfs = new ArrayList<>();
                        mfs.add(uf);
                        this.baseMapper.updateMouldFieldBatch(mfs);
                    });
                }
                List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                excelErrResps.addAll(errorMsg);
            }

        } catch (Exception e) {
            log.error("DwMouldServiceImpl-->uploadExcel 上传数据excel解析异常 ",e);
            e.printStackTrace();
            resultMap.put("percent",-1);
            resultMap.put("message","批量导入完成：<br/>1. 导入成功"+(insertNum+ updateNum+keepNum)+"条数据：其中新增"+insertNum+"条，有内容更新"
                            +updateNum+"条，无内容更新"+keepNum+"条；<br/>2. 导入失败"+excelErrResps.size() +"条知识，失败列表如下：");
            resultMap.put("result",excelErrResps);
            //刷新进度值
            cacheService.hmset("mould","excel_import_progress:"+processCode,resultMap,600);
        }

        ExcelUploadResponse eur = new ExcelUploadResponse();
        eur.setErrList(excelErrResps);
        eur.setInsertNum(insertNum);
        eur.setUpdateNum(updateNum);
        eur.setKeepNum(keepNum);
        eur.setAllNum(insertNum+ updateNum+keepNum);

        resultMap.put("percent",100);
        int successNum = eur.getAllNum();
        resultMap.put("message","批量导入完成：<br/>1. 导入成功"+successNum+"条数据：其中新增"+eur.getInsertNum()+"条，有内容更新"
                +eur.getUpdateNum()+"条，无内容更新"+eur.getKeepNum()+"条；<br/>2. 导入失败"+eur.getErrList().size()
                +"条知识，失败列表如下：");
        resultMap.put("result",eur.getErrList());
        //刷新进度值
        cacheService.hmset("mould","excel_import_progress:"+processCode,resultMap,600);
        return Result.ok("导入成功");
    }
    /**构建更新与 添加的模型关联字段**/
    private Map<String, List> doBuildMouldFieldMap(String processCode, String userCode,Long projectId, Long mId, List<List<Object>> ll, String operFlag) {

        List<DwMouldField> inFieldList = new ArrayList<>();
        List<DwMouldField> upFieldList = new ArrayList<>();
        List<ExcelImportErrorResponse> excelErrResps = new ArrayList<>();
        //查询模型字段分类信息
        List<Map<String,Object>> mouldFieldTypeList = this.baseMapper.selectMouldFieldTypeList();
        //数据字典下的字段
        for(int i=5;i< ll.size();i++){
            List<Object> row= ll.get(i);
            DwMouldField df = new DwMouldField();
            df.setId(IdWorker.getId());
            df.setProjectId(projectId);
            String dfName = ObjectUtils.isEmpty(row.get(0)) ? "" : row.get(0).toString().trim();
            String dfType = ObjectUtils.isEmpty(row.get(1)) ? "" : row.get(1).toString().trim();
            String dfDescription = ObjectUtils.isEmpty(row.get(2)) ? "" : row.get(2).toString().trim();
            String dfPrim = ObjectUtils.isEmpty(row.get(3)) ? "" : row.get(3).toString().trim();
            String dfEmpty = ObjectUtils.isEmpty(row.get(4)) ? "" : row.get(4).toString().trim();
            Integer dfLength = Integer.parseInt(ObjectUtils.isEmpty(row.get(5)) ? "0" : row.get(5).toString().trim());

            if(StringUtils.isEmpty(dfName)){
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"数据模型关联字段的字段名为空"));
                continue;
            }
            if(StringUtils.isEmpty(dfType)){
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"数据模型关联字段类型为空"));
                continue;
            }

            if (StringUtils.isEmpty(dfName) && StringUtils.isEmpty(dfType)) {
                log.info("DwMouldServiceImpl-->uploadExcel======插入数据时有一条全空的字段信息======");
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"excel数据模板中有一条空数据"));
                continue;
            }

            df.setMouldId(mId);
            df.setKey(mId+"_"+i);  //前端使用的key
            df.setName(dfName);
            List<Object> lt =  mouldFieldTypeList.stream().filter(f-> StringUtils.equals(dfType,f.get("name")+"")).map(m->m.get("id")).collect(Collectors.toList());
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lt)) {
                df.setFieldType(lt.get(0)+"");
            }
            df.setFieldTypeName(dfType);
            df.setDescription(dfDescription);
            df.setPrimaryFlag(StringUtils.isEmpty(dfPrim) || StringUtils.equals("否",dfPrim)?0:1);
            df.setEmptyFlag(StringUtils.isEmpty(dfEmpty) || StringUtils.equals("否",dfEmpty)?0:1);
            df.setLength(dfLength);

            //刷新进度值
            int currentPercent = (i+1)*100/ll.size();
            int beforePercent = i*100/ll.size();
            int cp = currentPercent / 10;
            int bp = beforePercent / 10;
            if(bp<cp && currentPercent!=100){
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("percent",cp * 10);
                //刷新进度值
                cacheService.hmset("mould","excel_import_progress:"+processCode,resultMap,600);
            }

            //新增加
            if(StringUtils.equals("insert",operFlag)){
                df.setCreateUser(userCode);
                df.setCreateTime(new Date());
                inFieldList.add(df);
                continue;
            }

            //根据 数据字典ID   字段名 判断数据是否存在
            List<DwMouldField> dictFields = this.baseMapper.selectMouldFieldByIdName(mId,dfName);
            if(CollectionUtils.isEmpty(dictFields)){
                df.setCreateUser(userCode);
                df.setCreateTime(new Date());
                inFieldList.add(df);
            }else{
                df.setId(dictFields.get(0).getId());
                df.setUpdateUser(userCode);
                df.setUpdateTime(new Date());
                upFieldList.add(df);
            }
        }

        Map<String,List> mf = new HashMap<>();
        if(CollectionUtils.isNotEmpty(inFieldList)){
            mf.put("insert",inFieldList);
        }
        if(CollectionUtils.isNotEmpty(upFieldList)){
            mf.put("update",upFieldList);
        }
        mf.put("error",excelErrResps);
        return mf;
    }

    /**获取导入文件进度**/
    @Override
    public R getImportProgress(String processCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->getImportProgress 获取导入文件进度 ");
        }
        //获取缓存中的数据
        Map<Object,Object> redisData = cacheService.hmget("mould","excel_import_progress:"+processCode);

        if(MapUtils.isEmpty(redisData)){
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","查询上传信息批次号有问题");
            return Result.ok(mm);
        }
        return Result.ok(redisData);
    }

    @Async
    @Override
    public void uploadBigFile(MultipartFile uploadFile, String userCode) {
        log.info("上传大文件");
        String newFileName = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + uploadFile.getOriginalFilename() ;
        String fileName = "/tmp" + File.separatorChar+"dnt" +File.separatorChar +"uploadFile"+ File.separatorChar  +DateFormatUtils.format(new Date(), "yyyyMMdd") +File.separatorChar+"mould"+File.separatorChar+ newFileName;
        File uFile = new File(fileName);
        try {
            //获取父目录
            File fileParent = uFile.getParentFile();
            if (!uFile.getParentFile().exists()) {
                //创建目录
                fileParent.mkdirs();
            }
            //创建文件
            boolean flag = uFile.createNewFile();
            log.debug("创建文件操作："+flag);
            uploadFile.transferTo(uFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**查看详情**/
    @Override
    public DwMouldResponse detailMould(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->detailMould 查看详情");
        }
        //NO.1 查看模型的基础信息
        DwMould dwm = this.baseMapper.selectById(id);
        if(!Optional.fromNullable(dwm).isPresent()){
            //模型基础数据不存在
            log.info("模型基础数据不存在请检查模型ID");
            return  new DwMouldResponse();
        }
        //NO.2 构建数据
        DwMouldResponse mr = new DwMouldResponse();
        BeanValueTrimUtil.beanValueTrim(dwm);
        BeanUtils.copyProperties(dwm,mr);
        //处理生命周期
        Date slf = dwm.getStorageLifecycle();
        if(slf==null){
            mr.setStorageLifecycle("");
        }else{
            mr.setStorageLifecycle(DateUtil.formatDate(slf));
        }
        //NO.3 根据模型ID 查询 模型关联字段
        List<DwMouldField> mFields = this.baseMapper.selectMouldFields(id);
        if(CollectionUtils.isEmpty(mFields)){
            mFields = new ArrayList<>();
        }else{
            //字段类型
            List<Map<String, Object>> fileTypes = this.dwPublicMouldMapper.selectMouldFieldTypes();

            //查询 数据元 的目录数据
            QueryWrapper<DwCategory> cq = Wrappers.query();
            cq.eq("a.delete_model",1).eq("a.dw_type","data_element");
            List<DwDataElementTreeResponse> list = dwPublicMouldMapper.selectDataElementCategories(cq);
            //数据元
            List<Map<String,Object>> dataElements = dwPublicMouldMapper.selectDataElements(null);
            for (DwMouldField mField : mFields) {
                String typeName="";
                if(StringUtils.isNotEmpty(mField.getFieldType())) {
                    List<Object> typeNames = fileTypes.stream()
                            .filter(f -> f.get("id").toString().equals(mField.getFieldType()))
                            .map(m->m.get("name")).collect(Collectors.toList());
                    typeName = CollectionUtils.isEmpty(typeNames)?"":typeNames.get(0)+"";
                }
                String cname="";
                if(Optional.fromNullable(mField.getStandardCategoryId()).isPresent()) {
                    List<String> cnames = list.stream()
                            .filter(f -> f.getId().longValue()==mField.getStandardCategoryId().longValue())
                            .map(m->m.getName()).collect(Collectors.toList());
                    cname = CollectionUtils.isEmpty(cnames)?"":cnames.get(0);
                }
                String fsn="";
                if(StringUtils.isNotEmpty(mField.getFieldStandard())) {
                    List<Object> fsns = dataElements.stream()
                            .filter(f -> f.get("id").toString().equals(mField.getFieldStandard()))
                            .map(m->m.get("name")).collect(Collectors.toList());
                    fsn = CollectionUtils.isEmpty(fsns)?"":fsns.get(0)+"";
                }
                mField.setFieldTypeName(typeName);
                mField.setStandardCategoryName(cname);
                mField.setFieldStandardName(fsn);
            }


        }
        //NO.4 根据模型ID 查询模型关联分区
        List<DwMouldFieldPartition> mFieldPartitions = this.baseMapper.selectMouldFieldPartitions(id);
        if(CollectionUtils.isEmpty(mFieldPartitions)){
            mFieldPartitions =new ArrayList<>();
        }
        mr.setFields(mFields);
        mr.setFieldPartitions(mFieldPartitions);

        //构建模型的分类目录
        Long nowMouldCategoryId = dwm.getCategoryId();
        //NO.5 查看所有的模型分类
        QueryWrapper<DwMouldCategory> qc = Wrappers.query();
        qc.select("id,name,path").eq("delete_model",1);
        List<DwMouldCategory> categoryList = this.dwMouldCategoryMapper.selectList(qc);
        String categoryName = buildCategoryName(nowMouldCategoryId,categoryList);
        mr.setCategoryName(categoryName);

        //状态值
        mr.setMouldStatusName(MouldStatusCode.getValue(dwm.getMouldStatus()));
        mr.setMouldTypeName(MouldTypeCode.getValue(dwm.getMouldType()));
        return mr;
    }



    /**发布模型操作**/
    @Override
    public R doMouldRelease(Long id,Integer mouldStatus,String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->doMouldRelease 发布模型操作");
        }
        String m="doMouldRelease";
        Long lotNumber =IdWorker.getId();
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->doMouldRelease 发布模型操作");
        }

        asyncWriteLog(lotNumber,m,2,"DEBUGE",id,"发布模型操作开始",userCode,1);
        DwMould dm = this.baseMapper.selectById(id);
        if(!Optional.fromNullable(dm).isPresent()){
            asyncWriteLog(lotNumber,m,2,"ERROR",id,"查询模型id为 "+id+" 的数据不存在",userCode,2);
            return Result.fail("查询模型id为 "+id+" 的数据不存在");
        }
        mouldStatus =!Optional.fromNullable(mouldStatus).isPresent()?1:mouldStatus;
        Integer mStatus = dm.getMouldStatus();
        if(1==mStatus){
            asyncWriteLog(lotNumber,m,2,"ERROR",id,"模型已发布不能重复发布",userCode,3);
            return Result.fail("模型已发布不能重复发布");
        }
        Integer mType = dm.getMouldType();
        DwMould dm1 = new DwMould();
        dm1.setId(id);
        dm1.setMouldStatus(mouldStatus);
        dm1.setUpdateTime(new Date());
        if(1==mType){
            //根据模型信息 构建sql
            String sql = doReleaseMouldSql(dm);
            if(StringUtils.isEmpty(sql)){
                asyncWriteLog(lotNumber,m,2,"ERROR",id,"模型发布失败，模型没有设置字段信息",userCode,4);
                return Result.fail("模型发布失败，模型没有设置字段信息");
            }


            asyncWriteLog(lotNumber,m,2,"INFO",id,"模型发布的语句为：" + sql,userCode,5);
            //标识成已发布状态的时候构建发布用的sql语句
            dm1.setReleaseSql(sql);

        }else if(2==mType){
            dm1.setReleaseSql(dm.getDdlStatement());
        }
        int ii = this.baseMapper.updateById(dm1);
        log.info("发布了{}条数据",ii);

        asyncWriteLog(lotNumber,m,2,"DEBUGE",id,"成功发布了"+ii+"条数据",userCode,6);
        //记录发布模型时 模型的对应的数据参数
        ((DwMouldService)(AopContext.currentProxy())).writeMouldReleaseLog(dm,id);
        return Result.ok("模型发布成功");
    }
    /**查看模型物理化的DDL语句**/
    @Override
    public R selectMouldPhysicsDDL(Long mouldId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectMouldPhysicsDDL 查看模型物理化的DDL语句");
        }
        //获取已发布模型的sql
        QueryWrapper<DwMould> mq = Wrappers.query();
        mq.select("id,name,mould_status as mouldStatus,mould_type as mouldType,ddl_statement as ddlStatment,release_sql as releaseSql")
                .eq("delete_model",1)
                .eq("id",mouldId);
        List<Map<String,Object>> mouldMapList = this.baseMapper.selectMaps(mq);
        if(CollectionUtils.isEmpty(mouldMapList)){
            log.warn("模型信息不存在");
            return Result.fail("模型信息不存在");
        }
        Map<String,Object> mouldMap = mouldMapList.get(0);
        if(Integer.parseInt(mouldMap.get("mouldStatus").toString())!=1){
            return Result.fail("模型没有发布不能查看DDL语句");
        }
        String ddlSql = null;
        if(Integer.parseInt(mouldMap.get("mouldType").toString())==1){
            ddlSql= mouldMap.get("releaseSql")+"";
        }else{
            ddlSql= mouldMap.get("ddlStatment")+"";
        }
        log.warn("模型的DDL语句为：{}",ddlSql);
        Map<String,String> m = new HashMap<>();
        m.put("ddlSql",ddlSql);
        return Result.ok(m);
    }
    /**查看模型物理化表结构**/
    @Override
    public R selectMouldPhysicsStructure(Long mouldId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectMouldPhysicsStructure 查看模型物理化表结构 ");
        }
        QueryWrapper<DwMould> mq = Wrappers.query();
        mq.select("id,name")
                .eq("delete_model",1)
                .eq("mould_status",1)
                .eq("id",mouldId);
        List<Map<String,Object>> maps = this.baseMapper.selectMaps(mq);

        if(CollectionUtils.isEmpty(maps)){
            return Result.fail("查询的模型信息不存在");
        }

        Map<String,Object> mouldMap = maps.get(0);

        List<DwMouldField> mouldFields= this.baseMapper.selectMouldFields(mouldId);
        mouldMap.put("mouldFields",mouldFields);
        return Result.ok(mouldMap);
    }
    /**查看模型物理化表数据**/
    @Override
    public R selectMouldPhysicsTable(Long physicsId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldServiceImpl-->selectMouldPhysicsTable 查看模型物理化表数据");
        }
        //NO.1 根据模型ID查询模型物理化的信息
        QueryWrapper<DwMouldPhysics> mp = Wrappers.query();
        mp.eq("delete_model",1)
                .eq("id",physicsId)
            .orderByDesc("id");
        DwMouldPhysics mouldPhysic = this.dwMouldPhysicsMapper.selectOne(mp);

        if(!Optional.fromNullable(mouldPhysic).isPresent()){
            return Result.fail("模型没有物理化");
        }


        List<DwMouldField> mouldFields= this.baseMapper.selectMouldFields(mouldPhysic.getMouldId());

        List<String> fields = new ArrayList<>();

        mouldFields.forEach(f->{
            fields.add(f.getName());
        });

        String s1 = fields.stream().map(s -> "`" + s + "`").collect(Collectors.joining(", "));
        s1 = StringUtils.isEmpty(s1)?"*":s1;

        String sql ="select " + s1+ " from "+ mouldPhysic.getMouldName();
        log.warn("查询hive的sql语句为：{}",sql);

        JSONObject dataJson = JSONObject.parseObject(mouldPhysic.getHiveJson()) ;
        if(dataJson.isEmpty()){
            return Result.fail("没有配置数据仓信息");
        }
        String userName = dataJson.getString("username");
        String password = dataJson.getString("password");
        String jdbcUrl = dataJson.getString("jdbcUrl");

        String r = String.format("HIVE用户名：%s HIVE密码：%s HIVE连接方式：%s",userName,password,jdbcUrl);
        log.warn("项目下的默认仓库为：{}",r);
        //hive 数据驱动
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource(jdbcUrl,userName,password));

        Map<String,Object> mm = new HashMap<>();
        mm.put("fields",fields);

        List<Map<String,Object>> maps = jdbcTemplate.queryForList(sql);
        mm.put("datas",maps);
        return Result.ok(mm);
    }


    /**================================本模块中使用的自定义业务方法========================================**/
    /**
     * 根据模型信息 构建 发布模型用的sql
     * @param dm
     * @return
     */
    private String doReleaseMouldSql(DwMould dm) {
        Long mid = dm.getId();
        String mName = dm.getName();
        //模型字段
        List<DwMouldField> mouldFields = this.baseMapper.selectMouldFields(mid);
        //分区
        List<DwMouldFieldPartition> mouldFieldPartitions = this.baseMapper.selectMouldFieldPartitions(mid);

        StringBuffer sql = new StringBuffer();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(mouldFields)){

            sql.append("CREATE TABLE IF NOT EXISTS ").append(mName).append("(");
            //构建字段信息
            StringBuffer sf = new StringBuffer();
            mouldFields.forEach(mf->{
                if(StringUtils.isEmpty(mf.getName())){
                    return;
                }
                String p = "",e="",comment="";
                System.out.println(Optional.fromNullable(mf.getPrimaryFlag()).isPresent());

                if(Optional.fromNullable(mf.getPrimaryFlag()).isPresent()&&1==mf.getPrimaryFlag()){
                    p = "primary key";
                }
                if(Optional.fromNullable(mf.getEmptyFlag()).isPresent() && 1==mf.getEmptyFlag()){
                    e = "not null ";
                }
                if(StringUtils.isNotEmpty(mf.getDescription())){
                    comment=" comment '" + mf.getDescription()+"'";
                }
                Integer length = mf.getLength();
                String sFieldName = mf.getFieldTypeName();

                //当为  字符时  增加长度信息默认100
                if(StringUtils.equalsIgnoreCase("CHAR",mf.getFieldTypeName())){
                    if(Optional.fromNullable(length).isPresent()){
                        sFieldName = mf.getFieldTypeName() +"("+length+")";
                    }else{
                        sFieldName = mf.getFieldTypeName()+"(100)";
                    }
                }
                sf.append(mf.getName()).append("  ").append(sFieldName).append(" "+p).append(" " + comment).append(" ,");
            });
            //去掉最后一个逗号
            sql.append(sf.deleteCharAt(sf.length()-1));

            StringBuffer sb = new StringBuffer();
            if(CollectionUtils.isNotEmpty(mouldFieldPartitions)){
                sb.append(" PARTITIONED BY (");
                mouldFieldPartitions.forEach(p->{
                    sb.append(p.getName()).append("  ").append(p.getFieldTypeName()).append(",");
                });
                sb.deleteCharAt(sb.length()-1);

                sb.append(") ");
            }

            sql.append(") ").append(sb).append(" ROW FORMAT DELIMITED FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n' "); // 定义分隔符
            sql.append(" STORED AS TEXTFILE"); // 作为文本存储
        }

        return sql.toString();
    }


    /**构建 当前模型的名称信息**/
    private String buildCategoryName(Long nowMouldCategoryId, List<DwMouldCategory> categoryList) {
        StringBuffer cn = new StringBuffer();

        if(CollectionUtils.isEmpty(categoryList)){
            return cn.toString();
        }
        List<String> pathList = categoryList.stream().filter(c->nowMouldCategoryId.equals(c.getId()))
                .map(DwMouldCategory::getPath).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(pathList)){
            return cn.toString();
        }
        //获取当前数据的全路径
        String path[] = pathList.get(0).split(",");
        //构建路径名
        for (String s : path) {

            List<String> pName = categoryList.stream().filter(c->s.equals(c.getId().toString()))
                    .map(DwMouldCategory::getName).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(pName)){
                continue;
            }
            cn.append(pName.get(0)).append("/");
        }
        return cn.deleteCharAt(cn.length()-1).toString();
    }
}
