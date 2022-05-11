package com.dnt.data.standard.server.model.mould.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.entity.response.ExcelImportErrorResponse;
import com.dnt.data.standard.server.model.entity.response.ExcelUploadResponse;
import com.dnt.data.standard.server.model.mould.dao.DwDbBaseMapper;
import com.dnt.data.standard.server.model.mould.entity.DwDbBase;
import com.dnt.data.standard.server.model.mould.entity.DwDbBaseField;
import com.dnt.data.standard.server.model.mould.entity.excel.DwDbBaseExcel;
import com.dnt.data.standard.server.model.mould.entity.request.DwDbBaseRequest;
import com.dnt.data.standard.server.model.mould.entity.response.DwDbBaseFieldResponse;
import com.dnt.data.standard.server.model.mould.entity.response.DwDbBaseResponse;
import com.dnt.data.standard.server.model.mould.service.DwDbBaseService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.version.dao.DwVersionDataMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: 数据基础库--服务接口实现层 <br>
 * @date: 2021/7/29 下午12:53 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwDbBaseServiceImpl extends BaseServiceImpl<DwDbBaseMapper, DwDbBase> implements DwDbBaseService {
    @Autowired
    private DwDbBaseMapper dwDbBaseMapper;
    @Autowired
    private CacheService cacheService;

    /**获取数据基础库分页列表**/
    @Override
    public IPage<DwDbBase> selectDbBasePage(DwDbBaseRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->selectDbBasePage 获取数据基础库分页列表");
        }
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwDbBase> page = new Page<>(pn,ps);
        QueryWrapper<DwDbBase> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        IPage<DwDbBase> res = this.dwDbBaseMapper.selectDbBasePage(page,q);
        res.getRecords().forEach(db0->{
            Integer releaseStatus = Optional.fromNullable(db0.getReleaseStatus()).isPresent()?db0.getReleaseStatus():0;

            db0.setReleaseStatusStr(ReleaseStatusEnum.getValue(releaseStatus));
        });
        return res;
    }
    /**查看详情**/
    @Override
    public DwDbBaseResponse detailDbBase(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->detailDbBase 查看详情");
        }
        DwDbBaseResponse res = new DwDbBaseResponse();
        //NO.1 根据ID执行查询
        DwDbBase db = this.dwDbBaseMapper.selectById(id);
        BeanValueTrimUtil.beanValueTrim(db);
        BeanUtils.copyProperties(db,res);

        List<DwDbBaseField> lst = this.dwDbBaseMapper.selectDwDbBaseFieldByDbId(id);
        List<DwDbBaseFieldResponse> listRes = new ArrayList<>();
        //构建数据
        lst.forEach(df->{
            DwDbBaseFieldResponse dbfr = new DwDbBaseFieldResponse();
            String s = df.getContentData();
            BeanValueTrimUtil.beanValueTrim(df);
            BeanUtils.copyProperties(df,dbfr);
            dbfr.setContentData(JSON.parseArray(s));
            listRes.add(dbfr);
        });

        Map<String,Object> rs = new HashMap<>();
        String headStr = db.getContentHeader();
        if(StringUtils.isNotEmpty(headStr)){

            rs.put("header",JSON.parseArray(headStr));
        }
        rs.put("data",listRes);
        res.setContentHeader(null);
        res.setLists(rs);
        //增加分类名称
        Long cid = db.getCategoryId();
        res.setCategoryName(getCategoryNameById(cid));
        return res;
    }
    /**添加数据基础库**/
    @Override
    public R saveDbBase(DwDbBaseRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->saveDbBase 添加数据基础库");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("添加数据基础库时，数据基础库的名称不能为空");
        }

        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加数据基础库时，数据基础库的编号不能为空");
        }
        //NO.1 判断名称信息是否存在
        List<DwDbBase> lists = findDwDbBaseByName(request.getName(),request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加数据基础库时名称已存在");
        }
        DwDbBase a = new DwDbBase();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,a);
        a.setCreateTime(new Date());
        a.setCreateUser(userCode);
        int ac = this.dwDbBaseMapper.insert(a);
        log.info("成功添加{}条数据基础库数据",ac);

        return Result.ok("添加数据基础库操作成功");
    }
    /**修改数据基础库**/
    @Override
    public R updateDbBase(DwDbBaseRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->updateDbBase 修改数据基础库");
        }
        //NO.1 业务字段非空判断
        if(!Optional.fromNullable(request.getId()).isPresent()){
            return Result.fail("修改数据基础库时，数据基础库的ID不能为空");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改数据基础库时，数据基础库的名称不能为空");
        }

        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改数据基础库时，数据基础库的编号不能为空");
        }

        Long id = request.getId();
        List<DwDbBase> lists = findDwDbBaseByName(request.getName(),request.getCategoryId());

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
            return Result.fail("修改数据基础库时，名称已存在");

        }
        DwDbBase dbBase = this.baseMapper.selectById(id);
        Integer dbReleaseStatus = Optional.fromNullable(dbBase.getReleaseStatus()).isPresent()?dbBase.getReleaseStatus():ReleaseStatusEnum.UNRELEASE.getCode();
        //NO.2 构建数据
        DwDbBase da = new DwDbBase();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,da);
        //当数据库中的数据发布状态为 未发布则不变撞他 如果是已发布则变为 已更新
        da.setReleaseStatus(dbReleaseStatus==ReleaseStatusEnum.UNRELEASE.getCode()?ReleaseStatusEnum.UNRELEASE.getCode():ReleaseStatusEnum.RELEASEUPDATE.getCode());
        da.setUpdateTime(new Date());
        da.setUpdateUser(userCode);
        int i = this.dwDbBaseMapper.updateById(da);
        log.info("修改数据基础库{}条数据",i);
        //使用Lambda表达式，实现多线程
        new Thread(()->{
            DwVersionDataMapper dwVersionDataMapper = applicationContext.getBean(DwVersionDataMapper.class);

            DwVersionData d = insertVersionHistoryLog("dw_db_base",da);
            log.info(Thread.currentThread().getName()+"另一个线程增加更新日志信息");
            dwVersionDataMapper.insert(d);
        }).start();
        return Result.ok("修改数据基础库操作成功");
    }

    private DwVersionData insertVersionHistoryLog(String tableName, DwDbBase dbMn) {
        DwVersionData d = new DwVersionData();
        List<DwDbBaseField> lst = this.dwDbBaseMapper.selectDwDbBaseFieldByDbId(dbMn.getId());

        List<DwDbBaseFieldResponse> listRes = new ArrayList<>();
        //构建数据
        lst.forEach(df->{
            DwDbBaseFieldResponse dbfr = new DwDbBaseFieldResponse();
            String s = df.getContentData();
            BeanValueTrimUtil.beanValueTrim(df);
            BeanUtils.copyProperties(df,dbfr);
            dbfr.setContentData(JSON.parseArray(s));
            listRes.add(dbfr);
        });
        Map<String,Object> rs = new HashMap<>();
        String headStr = dbMn.getContentHeader();
        if(StringUtils.isNotEmpty(headStr)){
            rs.put("header",JSON.parseArray(headStr));
        }
        rs.put("data",listRes);


        Long cid = dbMn.getCategoryId();
        String cName =getCategoryNameById(cid);

        dbMn.setCategoryName(cName);

        d.setProjectId(dbMn.getProjectId());
        d.setTableName(tableName);
        d.setDataId(dbMn.getId());
        d.setDataCategoryId(dbMn.getCategoryId());
        d.setDataName(dbMn.getName());
        d.setDataDescription(dbMn.getDescription());
        d.setDataAlias("");
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
    private List<DwDbBase> findDwDbBaseByName(String name, Long categoryId) {
        QueryWrapper<DwDbBase> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwDbBaseMapper.selectList(q);
    }
    /**删除数据基础库**/
    @Override
    public int deleteDbBase(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->deleteDbBase 删除数据基础库");
        }
        //NO.1 构建数据
        DwDbBase db = new DwDbBase();
        db.setId(id);
        db.setDeleteModel(0);
        db.setUpdateTime(new Date());
        db.setUpdateUser(userCode);
        //NO.2 执行删除操作
        return this.dwDbBaseMapper.updateById(db);
    }

    @Override
    public List<DwDbBaseExcel> selectDbBaseList(String name, Long categoryId) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->selectDbBaseList 查询基础库" );
        }
        QueryWrapper<DwDbBase> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .like(Optional.fromNullable(name).isPresent(),"a.name",name)
                .like(Optional.fromNullable(categoryId).isPresent(),"b.path",categoryId)
                .orderByDesc("a.id");

        return this.dwDbBaseMapper.selectDbBaseList(q);
    }

    /**文件上传的业务代码**/
    @Async
    @Override
    public R uploadExcel(String processCode,File uploadFile, String userCode,Long projectId, Long categoryId) {
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
            cacheService.hmset("db_base","excel_import_progress:"+processCode,resultMap,600);

            ExcelReader excelReader = ExcelUtil.getReader(new FileInputStream(uploadFile));
            List<List<Object>> ll = excelReader.read(1,excelReader.getRowCount());

            String dbName = ObjectUtils.isEmpty(ll.get(0).get(1))?"":ll.get(0).get(1).toString().trim();
            if(StringUtils.isEmpty(dbName)){
                excelErrResps.add(new ExcelImportErrorResponse(1,null,"基础库名称为空"));
            }
            //生命周期
            String dbCode = ObjectUtils.isEmpty(ll.get(1).get(1))?"0":ll.get(1).get(1).toString().trim();
            //描述
            String dbDescription = ObjectUtils.isEmpty(ll.get(2).get(1))?"":ll.get(2).get(1).toString().trim();


            DwDbBase mdb = new DwDbBase();
            mdb.setCategoryId(categoryId);
            mdb.setName(dbName);
            mdb.setCode(dbCode);
            mdb.setDescription(dbDescription);

            List<Object> headRow= ll.get(4);
            //不现的基础库有不同的表头，构建表头
            JSONArray ja = new JSONArray();

            for(int ii=0;ii<headRow.size();ii++){
                JSONObject jhead = new JSONObject();
                Object ihed = headRow.get(ii);
                if(ObjectUtils.isEmpty(ihed)){
                    continue;
                }
                String ih = ihed.toString();
                String key = PinyinUtil.getFirstLetter(ih,"");
                jhead.put("key",key);
                jhead.put("title",ih);
                ja.add(jhead);
            }

            mdb.setContentHeader(ja.toJSONString());
            QueryWrapper<DwDbBase> dq = Wrappers.query();
            dq.select("id,name")
                    .eq("delete_model",1)
                    .eq("name",dbName).eq("category_id",categoryId);
            List<DwDbBase> mouldList = this.baseMapper.selectList(dq);
            if(org.apache.commons.collections4.CollectionUtils.isEmpty(mouldList)){
                //增加
                insertNum++;
                mdb.setCreateUser(userCode);
                mdb.setCreateTime(new Date());
                //增加字典
                this.baseMapper.insert(mdb);
                Long mId = mdb.getId();

                //插入字典关联的字段
                Map<String,List> mFields = doBuildDbBaseFieldMap(processCode,userCode,projectId,mId,ll,"insert");
                if(MapUtils.isNotEmpty(mFields)){
                    List<DwDbBaseField> insertFields =  mFields.get("insert");
                    int ii = this.baseMapper.insertDbBaseFieldBatch(insertFields);
                    List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                    excelErrResps.addAll(errorMsg);
                }
            }else{
                //更新
                updateNum++;
                Long dbId = mouldList.get(0).getId();
                mdb.setId(dbId);
                mdb.setUpdateUser(userCode);
                mdb.setUpdateTime(new Date());
                //更新字典
                this.baseMapper.updateById(mdb);

                //插入 与更新 字典关联字段
                Map<String,List> mFields = doBuildDbBaseFieldMap(processCode,userCode,projectId,dbId,ll,"update");
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("insert")){
                    List<DwDbBaseField> insertFields =  mFields.get("insert");
                    int ii = this.baseMapper.insertDbBaseFieldBatch(insertFields);

                }
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("update")){
                    List<DwDbBaseField> updateFields =  mFields.get("update");
                    updateFields.forEach(uf->{
                        List<DwDbBaseField> dbField = new ArrayList<>();
                        dbField.add(uf);
                        this.baseMapper.updateDbBaseFieldBatch(dbField);
                    });
                }
                List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                excelErrResps.addAll(errorMsg);
            }

        } catch (Exception e) {
            log.error("DwDbBaseServiceImpl-->uploadExcel 上传数据excel解析异常 ",e);
            e.printStackTrace();
            resultMap.put("percent",-1);
            resultMap.put("message","批量导入完成：<br/>1. 导入成功"+(insertNum+ updateNum+keepNum)+"条数据：其中新增"+insertNum+"条，有内容更新"
                    +updateNum+"条，无内容更新"+keepNum+"条；<br/>2. 导入失败"+excelErrResps.size() +"条知识，失败列表如下：");
            resultMap.put("result",excelErrResps);
            //刷新进度值
            cacheService.hmset("db_base","excel_import_progress:"+processCode,resultMap,600);
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
        cacheService.hmset("db_base","excel_import_progress:"+processCode,resultMap,600);
        return Result.ok("导入成功");
    }
    /**获取导入文件进度**/
    @Override
    public R getImportProgress(String processCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDbBaseServiceImpl-->getImportProgress 获取导入文件进度 ");
        }
        //获取缓存中的数据
        Map<Object,Object> redisData = cacheService.hmget("db_base","excel_import_progress:"+processCode);

        if(MapUtils.isEmpty(redisData)){
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","查询上传信息批次号有问题");
            return Result.ok(mm);
        }

        return Result.ok(redisData);
    }


    /**构建更新与 添加的 字典关联字段**/
    private Map<String,List> doBuildDbBaseFieldMap(String processCode,
                                                 String userCode,
                                                 Long projectId,
                                                 Long dbBaseId,List<List<Object>> ll,
                                                 String operFlag) {

        List<DwDbBaseField> inFieldList = new ArrayList<>();
        List<DwDbBaseField> upFieldList = new ArrayList<>();
        List<ExcelImportErrorResponse> excelErrResps = new ArrayList<>();

        List<Object> headRow= ll.get(4);

        //数据基础库下的字段
        for(int i=5;i< ll.size();i++){
            List<Object> row= ll.get(i);
            DwDbBaseField df = new DwDbBaseField();
            df.setId(IdWorker.getId());
            df.setProjectId(projectId);

            JSONArray cjson= new JSONArray();
            for(int ik=0;ik<headRow.size();ik++){
                JSONObject jsa= new JSONObject();
                Object ikHead = headRow.get(ik);
                if(ObjectUtils.isEmpty(ikHead)){
                    continue;
                }
                String ikName = ikHead.toString();
                String key = PinyinUtil.getFirstLetter(ikName,"");
                jsa.put(key,ObjectUtils.isEmpty(row.get(ik)) ? "" : row.get(ik).toString().trim());

                cjson.add(jsa);
            }



            String dftn = ObjectUtils.isEmpty(row.get(0)) ? "" : row.get(0).toString().trim();

            if(StringUtils.isEmpty(dftn)){
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"数据基础库关联字段的表名为空"));
                continue;
            }

            df.setDbBaseId(dbBaseId);
            df.setTableName(dftn);
            df.setContentData(cjson.toJSONString());

            //刷新进度值
            int currentPercent = (i+1)*100/ll.size();
            int beforePercent = i*100/ll.size();
            int cp = currentPercent / 10;
            int bp = beforePercent / 10;
            if(bp<cp && currentPercent!=100){
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("percent",cp * 10);
                //刷新进度值
                cacheService.hmset("db_base","excel_import_progress:"+processCode,resultMap,600);
            }

            //新增加
            if(StringUtils.equals("insert",operFlag)){
                df.setCreateUser(userCode);
                df.setCreateTime(new Date());
                inFieldList.add(df);
                continue;
            }

            //根据 数据基础库ID   字段名 判断数据是否存在
            List<DwDbBaseField> dictFields = this.baseMapper.selectDbBaseFieldByIdName(dbBaseId,dftn);
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
}
