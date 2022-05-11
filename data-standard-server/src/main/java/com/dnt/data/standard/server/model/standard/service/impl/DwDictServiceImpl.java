package com.dnt.data.standard.server.model.standard.service.impl;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.entity.response.ExcelImportErrorResponse;
import com.dnt.data.standard.server.model.entity.response.ExcelUploadResponse;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.standard.dao.DwDataElementMapper;
import com.dnt.data.standard.server.model.standard.dao.DwDictMapper;
import com.dnt.data.standard.server.model.standard.entity.DwDataElement;
import com.dnt.data.standard.server.model.standard.entity.DwDict;
import com.dnt.data.standard.server.model.standard.entity.DwDictField;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDictExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDictRequest;
import com.dnt.data.standard.server.model.standard.entity.response.DwDictResponse;
import com.dnt.data.standard.server.model.standard.service.DwDictService;
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

/**
 * @description: 数据字典--服务接口实现层 <br>
 * @date: 2021/7/12 下午1:35 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwDictServiceImpl extends BaseServiceImpl<DwDictMapper, DwDict> implements DwDictService {
    @Autowired
    private DwDictMapper dwDictMapper;
    @Autowired
    private DwDataElementMapper dwDataElementMapper;
    @Autowired
    private CacheService cacheService;
    //获取数据字典分页数据
    @Override
    public IPage<DwDict> selectDictPage(DwDictRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwDictServiceImpl-->selectDictPage 获取数据字典分页数据");
        }
        /**页数**/
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwDict> page = new Page<>(pn,ps);
        QueryWrapper<DwDict> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        IPage<DwDict> res = dwDictMapper.selectDictPage(page,q);
        res.getRecords().forEach(db0->{
            Integer releaseStatus = Optional.fromNullable(db0.getReleaseStatus()).isPresent()?db0.getReleaseStatus():0;

            db0.setReleaseStatusStr(ReleaseStatusEnum.getValue(releaseStatus));
        });
        return res;
    }


    @Override
    public DwDictResponse detailDict(Long id) {
        log.info("DwDictServiceImpl-->detailDict 查询数据详情{}",IdWorker.getId());
        DwDictResponse dr = new DwDictResponse();
        //NO.1 查询数据字典详情
        DwDict dd = this.dwDictMapper.selectById(id);
        BeanValueTrimUtil.beanValueTrim(dd);
        BeanUtils.copyProperties(dd,dr);
        Long categoryId = dd.getCategoryId();
        dr.setCategoryName(getCategoryNameById(categoryId));
        //NO.2 查询当前数据字典下关联的字段
        List<DwDictField> fieldList = this.dwDictMapper.selectDictFieldList(id);
        if(CollectionUtils.isEmpty(fieldList)){
            fieldList = new ArrayList<>();
        }
        dr.setFields(fieldList);
        //NO.3 查询数据元下 关联的数据字典的 数据元信息
        QueryWrapper<DwDataElement> q = Wrappers.query();
        q.select("id,code,name,alias,description")
                .eq("delete_model",1)
                .eq("dict_id",id);
        List<Map<String,Object>> deList = this.dwDataElementMapper.selectMaps(q);
        if(CollectionUtils.isEmpty(deList)){
            deList = new ArrayList<>();
        }
        dr.setDataElementList(deList);
        return dr;
    }

    /**添加数据字典**/
    @Override
    public R saveDict(DwDictRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDictServiceImpl-->saveDict 添加数据字典");
        }
        //NO.1 判断名称是否为空
        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("添加数据字典时参考数据集名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加信息时，编号不能为空");
        }
        List<DwDict> lists = findDictByName(name,request.getCategoryId());
        if(CollectionUtils.isNotEmpty(lists)){
            return Result.fail("添加数据字典时参考数据集名称已存在");
        }
        //NO.2 构建数据
        DwDict dict = new DwDict();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dict);
        dict.setCreateUser(userCode);
        dict.setCreateTime(new Date());
        //NO.3 数据入库
        int ii = this.dwDictMapper.insert(dict);

        int ik =0;
        //NO.4 构建数据字典关联字段
        Long dictId = dict.getId();
        List<DwDictField> fields = request.getDFieldList();
        if(CollectionUtils.isNotEmpty(fields)){
            fields.forEach(f->{
                f.setId(IdWorker.getId());
                f.setDictId(dictId);
                f.setCreateUser(userCode);
                f.setCreateTime(new Date());
            });
            //NO.5 关联字段不为空的时候 批量添加
            ik = this.dwDictMapper.insertDictFieldBatch(fields);
        }

        return Result.ok("数据字典成功添加"+ii+
                "条数据\n 数据字典关联字段添加" +ik+
                "条数据");
    }

    /**
     * 根据 姓名 与分类ID查询分类下名称是不是存在
     * @param name
     * @param categoryId
     * @return
     */
    private List<DwDict> findDictByName(String name, Long categoryId) {
        QueryWrapper<DwDict> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.dwDictMapper.selectList(q);
    }

    /**修改数据字典**/
    @Override
    public R updateDict(DwDictRequest request, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwDictServiceImpl-->updateDict 修改数据字典");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.fail("修改信息时，名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("修改信息时，编号不能为空");
        }
        //NO.1 初始化数据
        DwDict dict = new DwDict();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,dict);

        DwDict dictDb = this.baseMapper.selectById(request.getId());
        Integer dbReleaseStatus = Optional.fromNullable(dictDb.getReleaseStatus()).isPresent()?dictDb.getReleaseStatus():ReleaseStatusEnum.UNRELEASE.getCode();
        //当数据库中的数据发布状态为 未发布则不变撞他 如果是已发布则变为 已更新
        dict.setReleaseStatus(dbReleaseStatus==ReleaseStatusEnum.UNRELEASE.getCode()?ReleaseStatusEnum.UNRELEASE.getCode():ReleaseStatusEnum.RELEASEUPDATE.getCode());

        dict.setUpdateUser(userCode);
        dict.setUpdateTime(new Date());
        //NO.2 更新数据
        int uf = this.dwDictMapper.updateById(dict);
        List<DwDictField> fields = request.getDFieldList();
        List<DwDictField> insertFields = new ArrayList<>();
        List<DwDictField> updateFields = new ArrayList<>();
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(fields)) {
            //NO.3 处理更新与新增加数据
            fields.forEach(f -> {
                Long fid = f.getId();
                if (Optional.fromNullable(fid).isPresent()) {
                    f.setUpdateUser(userCode);
                    f.setUpdateTime(new Date());
                    updateFields.add(f);
                } else {
                    f.setId(IdWorker.getId());
                    f.setDictId(request.getId());
                    f.setCreateUser(userCode);
                    f.setCreateTime(new Date());
                    insertFields.add(f);
                }
            });
        }
        int ii = 0,iu=0;
        //批量插入数据字典关联字段
        if(CollectionUtils.isNotEmpty(insertFields)){
            ii = this.dwDictMapper.insertDictFieldBatch(insertFields);
        }
        //批量更新数据字典关联字段
        if(CollectionUtils.isNotEmpty(updateFields)){
            for(DwDictField upf:updateFields) {
                List<DwDictField> upfs = new ArrayList<>();
                upfs.add(upf);
                iu = this.dwDictMapper.updateDictFieldBatch(upfs);
            }
        }
        //使用Lambda表达式，实现多线程
        new Thread(()->{
            DwVersionDataMapper dwVersionDataMapper = applicationContext.getBean(DwVersionDataMapper.class);
            Long categoryId = dict.getCategoryId();
            dict.setCategoryName(getCategoryNameById(categoryId));
            DwVersionData d = insertVersionHistoryLog("dw_dict",dict);
            log.info(Thread.currentThread().getName()+"另一个线程增加更新日志信息");
            dwVersionDataMapper.insert(d);
        }).start();
        return Result.ok("更新字典数据记录为"+uf+
                "条 \n 更新字典关联字段数据记录为"+iu+
                "条 \n 插入字典关联字段数据记录为"+ii+"条");
    }

    private DwVersionData insertVersionHistoryLog(String tableName, DwDict dbMn) {
        Long id = dbMn.getId();
        DwVersionData d = new DwVersionData();
        d.setProjectId(dbMn.getProjectId());
        d.setTableName(tableName);
        d.setDataId(id);
        d.setDataCategoryId(dbMn.getCategoryId());
        d.setDataName(dbMn.getName());
        d.setDataDescription(dbMn.getDescription());
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


        //NO.2 查询当前数据字典下关联的字段
        List<DwDictField> fieldList = this.dwDictMapper.selectDictFieldList(id);
        if(CollectionUtils.isEmpty(fieldList)){
            fieldList = new ArrayList<>();
        }
        d.setDataField1(JSON.toJSONString(fieldList));
        //NO.3 查询数据元下 关联的数据字典的 数据元信息
        QueryWrapper<DwDataElement> q = Wrappers.query();
        q.select("id,code,name,alias,description")
                .eq("delete_model",1)
                .eq("dict_id",id);
        List<Map<String,Object>> deList = this.dwDataElementMapper.selectMaps(q);
        if(CollectionUtils.isEmpty(deList)){
            deList = new ArrayList<>();
        }
        d.setDataField2(JSON.toJSONString(deList));
        return d;
    }

    /**删除数据字典**/
    @Override
    public int deleteDict(Long id, String userCode) {

        if(log.isDebugEnabled()){
            log.debug("DwDictServiceImpl-->deleteDict 删除数据字典");
        }
        //NO.1 构建删除数据信息
        DwDict dict = new DwDict();
        dict.setId(id);
        dict.setDeleteModel(0);
        dict.setUpdateTime(new Date());
        dict.setUpdateUser(userCode);
        //NO.2 执行删除操作
        return this.dwDictMapper.updateById(dict);
    }

    /**删除数据字典**/
    @Override
    public int deleteDictField(Long id, String userCode) {

        if(log.isDebugEnabled()){
            log.debug("DwDictServiceImpl-->deleteDictField 删除数据字典关联字段");
        }
        //NO.1 执行删除操作
        return this.dwDictMapper.deleteDictField(id,userCode);
    }
    /**导出数据业务代码**/
    @Override
    public List<DwDictExcel> selectDictList() {
        if(log.isInfoEnabled()) {
            log.info("DwDictServiceImpl-->selectDictList 导出数据字典数据");
        }
        QueryWrapper<DwDict> qd = Wrappers.query();
        qd.eq("a.delete_model",1);
        return this.dwDictMapper.selectDictList(qd);
    }

    /**上传操作**/
    @Async
    @Override
    public R uploadExcel(String processCode, File uploadFile, String userCode,Long projectId, Long categoryId){
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
            cacheService.hmset("dict","excel_import_progress:"+processCode,resultMap,600);

            ExcelReader excelReader = ExcelUtil.getReader(new FileInputStream(uploadFile));
            List<List<Object>> ll = excelReader.read(1,excelReader.getRowCount());

            String dicName = ObjectUtils.isEmpty(ll.get(0).get(1))?"":ll.get(0).get(1).toString().trim();
            if(StringUtils.isEmpty(dicName)){
                excelErrResps.add(new ExcelImportErrorResponse(1,null,"参考数据集名称为空"));
            }

            String dicAlias = ObjectUtils.isEmpty(ll.get(1).get(1))?"":ll.get(1).get(1).toString().trim();
            String dicCode = ObjectUtils.isEmpty(ll.get(2).get(1))?"":ll.get(2).get(1).toString().trim();
            if(StringUtils.isEmpty(dicCode)){
                excelErrResps.add(new ExcelImportErrorResponse(1,null,"数据字典标识编码为空"));
            }

            String dicDescription = ObjectUtils.isEmpty(ll.get(3).get(1))?"":ll.get(3).get(1).toString().trim();

            DwDict dict = new DwDict();
            dict.setCategoryId(categoryId);
            dict.setName(dicName);
            dict.setCode(dicCode);
            dict.setAlias(dicAlias);
            dict.setDescription(dicDescription);


            QueryWrapper<DwDict> dq = Wrappers.query();
            dq.select("id,name")
                    .eq("delete_model",1)
                    .eq("name",dicName).eq("category_id",categoryId);
            List<DwDict> dictList = this.baseMapper.selectList(dq);
            if(org.apache.commons.collections4.CollectionUtils.isEmpty(dictList)){
                //增加
                insertNum++;
                dict.setCreateUser(userCode);
                dict.setCreateTime(new Date());
                //增加字典
                this.dwDictMapper.insert(dict);
                Long dictId = dict.getId();
                //插入字典关联的字段
                Map<String,List> mFields = doBuildDictFieldMap(processCode,userCode,projectId,dictId,ll,"insert");
                if(MapUtils.isNotEmpty(mFields)){
                    List<DwDictField> insertFields =  mFields.get("insert");
                    this.dwDictMapper.insertDictFieldBatch(insertFields);
                    List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                    excelErrResps.addAll(errorMsg);
                }


            }else{
                //更新
                updateNum++;
                Long dictId = dictList.get(0).getId();
                dict.setId(dictId);
                dict.setUpdateUser(userCode);
                dict.setUpdateTime(new Date());
                //更新字典
                this.baseMapper.updateById(dict);
                //插入 与更新 字典关联字段
                Map<String,List> mFields = doBuildDictFieldMap(processCode,userCode,projectId,dictId,ll,"update");
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("insert")){
                    List<DwDictField> insertFields =  mFields.get("insert");
                    this.dwDictMapper.insertDictFieldBatch(insertFields);

                }
                if(MapUtils.isNotEmpty(mFields)&& mFields.containsKey("update")){
                    List<DwDictField> updateFields =  mFields.get("update");
                    this.dwDictMapper.updateDictFieldBatch(updateFields);
                }
                List<ExcelImportErrorResponse> errorMsg = mFields.get("error");
                excelErrResps.addAll(errorMsg);
            }

        } catch (Exception e) {
            log.error("DwDictServiceImpl-->uploadExcel 上传数据excel解析异常 ",e);
            e.printStackTrace();
            resultMap.put("percent",-1);
            resultMap.put("message","批量导入完成：<br/>1. 导入成功"+(insertNum+ updateNum+keepNum)+"条数据：其中新增"+insertNum+"条，有内容更新"
                    +updateNum+"条，无内容更新"+keepNum+"条；<br/>2. 导入失败"+excelErrResps.size() +"条知识，失败列表如下：");
            resultMap.put("result",excelErrResps);
            //刷新进度值
            cacheService.hmset("dict","excel_import_progress:"+processCode,resultMap,600);
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
        cacheService.hmset("dict","excel_import_progress:"+processCode,resultMap,600);
        return Result.ok("导入成功");

    }

    /**构建更新与 添加的 字典关联字段**/
    private Map<String,List> doBuildDictFieldMap(String processCode,
                                                              String userCode,
                                                              Long projectId,
                                                              Long dictId,List<List<Object>> ll,
                                                              String operFlag) {

        List<DwDictField> inFieldList = new ArrayList<>();
        List<DwDictField> upFieldList = new ArrayList<>();
        List<ExcelImportErrorResponse> excelErrResps = new ArrayList<>();

        //数据字典下的字段
        for(int i=6;i< ll.size();i++){
            List<Object> row= ll.get(i);
            DwDictField df = new DwDictField();
            df.setId(IdWorker.getId());
            df.setProjectId(projectId);
            String dfCode = ObjectUtils.isEmpty(row.get(0)) ? "" : row.get(0).toString().trim();
            String dfName = ObjectUtils.isEmpty(row.get(1)) ? "" : row.get(1).toString().trim();
            if(StringUtils.isEmpty(dfName)){
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"数据字典关联字段的字段名为空"));
                continue;
            }
            String dfDescription = ObjectUtils.isEmpty(row.get(2)) ? "" : row.get(2).toString().trim();

            if (StringUtils.isEmpty(dfCode) && StringUtils.isEmpty(dfName) && StringUtils.isEmpty(dfDescription)) {
                log.info("DwDictServiceImpl-->uploadExcel======插入数据时有一条全空的字段信息======");
                excelErrResps.add(new ExcelImportErrorResponse(i,null,"excel数据模板中有一条空数据"));
                continue;
            }
            df.setDictId(dictId);
            df.setKeyCode(dfCode);
            df.setKeyName(dfName);
            df.setDescription(dfDescription);

            //刷新进度值
            int currentPercent = (i+1)*100/ll.size();
            int beforePercent = i*100/ll.size();
            int cp = currentPercent / 10;
            int bp = beforePercent / 10;
            if(bp<cp && currentPercent!=100){
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("percent",cp * 10);
                //刷新进度值
                cacheService.hmset("dict","excel_import_progress:"+processCode,resultMap,600);
            }

            //新增加
            if(StringUtils.equals("insert",operFlag)){
                df.setCreateUser(userCode);
                df.setCreateTime(new Date());
                inFieldList.add(df);
                continue;
            }

            //根据 数据字典ID   字段名 判断数据是否存在
            List<DwDictField> dictFields = this.baseMapper.selectDictFieldByIdName(dictId,dfName);
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

    /**
     * 获取导入文件进度
     * @param processCode
     * @return
     */
    @Override
    public R getImportProgress(String processCode){
        if(log.isInfoEnabled()) {
            log.info("DwDictServiceImpl-->getImportProgress 获取导入文件进度 ");
        }
        //获取缓存中的数据
        Map<Object,Object> redisData = cacheService.hmget("dict","excel_import_progress:"+processCode);

        if(MapUtils.isEmpty(redisData)){
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","查询上传信息批次号有问题");
            return Result.ok(mm);
        }
        return Result.ok(redisData);

    }
}
