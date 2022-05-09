package com.dnt.data.standard.server.model.standard.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.standard.entity.response.DwDataElementResponse;
import com.dnt.data.standard.server.model.entity.response.ExcelImportErrorResponse;
import com.dnt.data.standard.server.model.entity.response.ExcelUploadResponse;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.standard.dao.DwCategoryMapper;
import com.dnt.data.standard.server.model.standard.dao.DwDataElementMapper;
import com.dnt.data.standard.server.model.standard.dao.DwDictMapper;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.standard.entity.DwDataElement;
import com.dnt.data.standard.server.model.standard.entity.DwDict;
import com.dnt.data.standard.server.model.standard.entity.excel.DwDataElementExcel;
import com.dnt.data.standard.server.model.standard.entity.request.DwDataElementRequest;
import com.dnt.data.standard.server.model.standard.service.DwDataElementService;
import com.dnt.data.standard.server.model.version.dao.DwVersionDataMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.utils.BigDataParseExcelUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.stream.Collectors;

/**
 * @description: 据元-服务接口实现层 <br>
 * @date: 2021/7/21 下午3:23 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwDataElementServiceImpl extends BaseServiceImpl<DwDataElementMapper, DwDataElement> implements DwDataElementService {
    @Autowired
    private DwDataElementMapper dataElementMapper;

    @Autowired
    private DwCategoryMapper dwCategoryMapper;
    @Autowired
    private DwDictMapper dwDictMapper;

    @Autowired
    private CacheService cacheService;
    /**分页查询列表**/
    @Override
    public IPage<DwDataElement> selectDataElementPage(DwDataElementRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->selectDataElementPage 获取分页数据列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwDataElement> page = new Page<>(pn,ps);
        QueryWrapper<DwDataElement> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
                .like(Optional.fromNullable(request.getName()).isPresent(),"a.name",request.getName())
                .like(Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId())
                .orderByDesc("a.id");

        IPage<DwDataElement> res = this.dataElementMapper.selectDataElementPage(page,q);
        res.getRecords().forEach(db0->{
            Integer releaseStatus = Optional.fromNullable(db0.getReleaseStatus()).isPresent()?db0.getReleaseStatus():0;

            db0.setReleaseStatusStr(ReleaseStatusEnum.getValue(releaseStatus));
        });
        return res;
    }
    /**返回数据元详情信息**/
    @Override
    public DwDataElementResponse detailDataElement(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->detailDataElement 查看数据元详细信息");
        }
        DwDataElementResponse er = new DwDataElementResponse();
        DwDataElement de= this.dataElementMapper.selectById(id);
        BeanValueTrimUtil.beanValueTrim(de);
        BeanUtils.copyProperties(de,er);
        //查询 模型的信息 等模型开 完成后完善
        Long categoryId = de.getCategoryId();
        er.setCategoryName(getCategoryNameById(categoryId));
        Integer typeId = de.getTypeId();
        er.setTypeName(DataElementCode.getValue(typeId));

        List<Map<String,Object>> mouldList = this.dataElementMapper.selectMouldById(id);
        er.setMouldList(mouldList);

        return er;

    }
    /**查看数据源分类下拉列表**/
    @Override
    public List<Map<String, Object>> selectDataElementTypeItem() {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->selectDataElementTypeItem 数据源分类下拉列表");
        }
        return this.dataElementMapper.selectDataElementTypeItem();
    }
    /**查看数据字典下拉列表**/
    @Override
    public List<Map<String, Object>> selectDictItem() {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->selectDataElementItem 数据字典下拉列表");
        }
        //构建  名称与目录结构的信息
        List<Map<String, Object>> mmList = this.dataElementMapper.selectDictItem();

        List<DwCategory> dictCategoryList = this.dataElementMapper.selectDictCategoryList("dict");

        mmList.forEach(mm->{
            Object mapCategoryId = mm.get("categoryId");
            if(ObjectUtils.isEmpty(mapCategoryId)){
                return;
            }
            String cName = ObjectUtils.isEmpty(mm.get("name"))?"":mm.get("name").toString();
            //分类ID
            Long categoryId = Long.parseLong(mm.get("categoryId").toString());
            String categoryPath = buildCategoryName(categoryId,dictCategoryList);

            mm.put("name",categoryPath+cName);

        });

        return  mmList;
    }

    /**
     * 构建目录的路径
     * @param categoryId
     * @param dictCategoryList
     * @return
     */
    private String buildCategoryName(Long categoryId, List<DwCategory> dictCategoryList) {
        StringBuffer sf = new StringBuffer();
        if(CollectionUtils.isEmpty(dictCategoryList)){
            return sf.toString();
        }


        List<String> pathList = dictCategoryList.stream().filter(c->categoryId.equals(c.getId()))
                .map(DwCategory::getPath).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(pathList)){
            return sf.toString();
        }
        /**获取当前数据的全路径**/
        String path[] = pathList.get(0).split(",");

        //构建路径名
        for (String s : path) {

            List<String> pName = dictCategoryList.stream().filter(c->s.equals(c.getId().toString()))
                    .map(DwCategory::getName).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(pName)){
                continue;
            }
            sf.append(pName.get(0)).append("/");
        }
        return sf.toString();
    }

    /**查看平缓目录的目录信息**/
    @Override
    public List<Map<String, Object>> selectParallelCatalogue(Long id) {
        //NO.1 根据ID 查询当前目录平缓目录的数据
        DwCategory  dc = this.dwCategoryMapper.selectById(id);
        Long pid = dc.getParentId();
        String dt = dc.getDwType();

        QueryWrapper<DwCategory> q = Wrappers.query();
        q.select("id","name","parent_id","is_leaf","path","level")
                .eq("delete_model",1)
                .eq("dw_type",dt)
                .eq("parent_id",pid);
        List<Map<String,Object>> lists = this.dwCategoryMapper.selectMaps(q);

        return lists;
    }

    /**添加数据元信息**/
    @Override
    public R saveDataElement(DwDataElementRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->saveDataElement 保存数据元信息");
        }

        //NO.1 判断名称是否为空
        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("添加数据元时数据元名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("添加数据元时数据元编号不能为空");
        }
        Long cid = request.getCategoryId();
        if(!Optional.fromNullable(cid).isPresent()){
            return Result.fail("添加数据元时所属类目ID不能为空");
        }

        QueryWrapper<DwDataElement> q = Wrappers.query();
        q.select("id,code,name")
                .eq("project_id",request.getProjectId())
                .eq("delete_model",1)
                .eq("category_id",cid)
                .eq("name",name);
        List<DwDataElement> eList = this.dataElementMapper.selectList(q);
        if(CollectionUtils.isNotEmpty(eList)){
            return Result.fail("添加数据元在指定的分类下名称已存在");
        }
        //NO.2 构建数据
        DwDataElement de = new DwDataElement();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,de);

        Long dictId = request.getDictId();
        QueryWrapper<DwDict> dq = Wrappers.query();
        dq.select("id,name")
                .eq("id",dictId)
                .eq("delete_model",1);
        DwDict dictObj = this.dwDictMapper.selectOne(dq);
        if(ObjectUtils.isNotEmpty(dictObj)){
            de.setDictName(dictObj.getName());
        }
        de.setCreateUser(userCode);
        de.setCreateTime(new Date());
        //NO.3 数据入库
        this.dataElementMapper.insert(de);

        return Result.ok("添加数据元数据信息成功");
    }
    /**编辑数据元信息**/
    @Override
    public R updateDataElement(DwDataElementRequest request, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwDataElementServiceImpl-->updateDataElement 编辑数据元信息成功");
        }

        //NO.1 判断名称是否为空
        //ID
        Long id = request.getId();
        if(!Optional.fromNullable(id).isPresent()){
            return Result.fail("编辑数据元时ID不能为空");
        }
        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("编辑数据元时数据元名称不能为空");
        }
        if(StringUtils.isEmpty(request.getCode())){
            return Result.fail("编辑数据元时数据元编号不能为空");
        }
        Long cid = request.getCategoryId();
        if(!Optional.fromNullable(cid).isPresent()){
            return Result.fail("编辑数据元时所属类目ID不能为空");
        }

        //NO.2 构建数据
        DwDataElement de = new DwDataElement();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,de);
        Long dictId = request.getDictId();
        QueryWrapper<DwDict> dq = Wrappers.query();
        dq.select("id,name")
                .eq("id",dictId)
                .eq("delete_model",1);
        DwDict dictObj = this.dwDictMapper.selectOne(dq);
        if(ObjectUtils.isNotEmpty(dictObj)){
            de.setDictName(dictObj.getName());
        }

        DwDataElement deMn = this.baseMapper.selectById(request.getId());
        Integer dbReleaseStatus = Optional.fromNullable(deMn.getReleaseStatus()).isPresent()?deMn.getReleaseStatus():ReleaseStatusEnum.UNRELEASE.getCode();
        //当数据库中的数据发布状态为 未发布则不变撞他 如果是已发布则变为 已更新
        de.setReleaseStatus(dbReleaseStatus==ReleaseStatusEnum.UNRELEASE.getCode()?ReleaseStatusEnum.UNRELEASE.getCode():ReleaseStatusEnum.RELEASEUPDATE.getCode());

        de.setUpdateUser(userCode);
        de.setUpdateTime(new Date());
        //NO.3 更新数据入库
        this.dataElementMapper.updateById(de);

        //使用Lambda表达式，实现多线程
        new Thread(()->{
            DwVersionDataMapper dwVersionDataMapper = applicationContext.getBean(DwVersionDataMapper.class);
            DwVersionData d = insertVersionHistoryLog("dw_data_element",de);
            log.info(Thread.currentThread().getName()+"另一个线程增加更新日志信息");
            dwVersionDataMapper.insert(d);
        }).start();
        return Result.ok("编辑数据元数据信息成功");
    }

    private DwVersionData insertVersionHistoryLog(String tableName, DwDataElement dbMn) {
        DwVersionData d = new DwVersionData();
        d.setProjectId(dbMn.getProjectId());
        d.setTableName(tableName);
        d.setDataId(dbMn.getId());
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
        return d;
    }

    /**删除数据元信息**/
    @Override
    public int deleteDataElement(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->deleteDataElement 删除数据元信息");
        }
        //NO.1 构建数据信息
        DwDataElement de = new DwDataElement();
        de.setId(id);
        de.setDeleteModel(0);
        de.setUpdateTime(new Date());
        de.setUpdateUser(userCode);
        //NO.2执行删除操作
        return this.dataElementMapper.updateById(de);
    }

    /**根据名称与分类过滤数据元信息**/
    @Override
    public List<DwDataElementExcel> selectDataElementList(String name, Long categoryId) {
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl.selectDataElementList 根据分类ID 与名称查询 数据元信息");
        }

        QueryWrapper<DwDataElementExcel> q = Wrappers.query();

        q.eq("a.delete_model",1)
                .like(Optional.fromNullable(name).isPresent(),"a.name",name)
                .eq(Optional.fromNullable(categoryId).isPresent(),"a.category_id",categoryId);

        return this.dataElementMapper.selectDataElementList(q);
    }
    /**文件批量导入操作**/
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

            BigDataParseExcelUtil bigExcel = new BigDataParseExcelUtil();
            //获取excel中所有的数据
            List<ArrayList<Object>> excelData = bigExcel.process(new FileInputStream(uploadFile));

            System.out.println("DwDataElementServiceImpl.uploadExcel " + excelData.size());
            //去掉单元格式 为空的记录
            Iterator<ArrayList<Object>> iteratorRow = excelData.iterator();
            while (iteratorRow.hasNext()) {
                ArrayList<Object> next = iteratorRow.next();
                //判断row中所有的数据是否都null或空字符串，如果是就移出该条数据
                boolean rowIsNull = true;
                for (Object cellVal : next) {
                    if (cellVal == null) {
                        continue;
                    } else if (cellVal instanceof String && StringUtils.isBlank((String) cellVal)) {
                        continue;
                    } else {
                        rowIsNull = false;
                        break;
                    }
                }
                if (rowIsNull) {
                    iteratorRow.remove();
                }
            }
            //进度条
            resultMap.put("percent",0);
            //刷新进度值
            cacheService.hmset("data_element","excel_import_progress:"+processCode,resultMap,600);

            List<Map<String,Object>> elementTypes = this.baseMapper.selectDataElementTypeItem();
            List<Map<String,Object>> elementDicts = this.baseMapper.selectDictItem();
            //移除空行的数据
            for (int i = 0; i < excelData.size(); i++) {
                //单行数据
                List<Object> row = excelData.get(i);
                if(row.size()<5){
                    excelErrResps.add(new ExcelImportErrorResponse(i+1,null,"数据元信息为空"));
                    continue;
                }

                String exCel0 = row.get(0) == null ? "" : (row.get(0) + "").trim();
                if(StringUtils.isEmpty(exCel0)){
                    excelErrResps.add(new  ExcelImportErrorResponse(i+1,null,"数据元名称为空") );
                    continue;
                }


                String exCel1 = row.get(1) == null ? "" : (row.get(1) + "").trim();
                if(StringUtils.isEmpty(exCel1)){
                    excelErrResps.add(new ExcelImportErrorResponse(i+1,null,"数据元编号为空"));
                    continue;
                }


                String exCel4 = row.get(4) == null ? "" : (row.get(4) + "").trim();
                if(StringUtils.isEmpty(exCel4)){
                    excelErrResps.add(new ExcelImportErrorResponse(i+1,null,"数据元类型为空"));
                    continue;

                }

                String exCel7 = row.get(7) == null ? "" : (row.get(7) + "").trim();
                if(StringUtils.isEmpty(exCel7)){
                    excelErrResps.add(new ExcelImportErrorResponse(i+1,null,"数据元引用数据字典为空"));
                    continue;
                }

                //当元数据名与分类 相同时，其他字段不同则更新，如果其他字段值全部相同则保持不变
                //当元数据名与分类下不存在此数据则增加
                QueryWrapper<DwDataElement> deq = Wrappers.query();
                deq.select("id,name").eq("delete_model",1)
                        .eq("name",exCel0)
                        .eq("category_id",categoryId);
                List<DwDataElement> delementList = this.baseMapper.selectList(deq);

                if(CollectionUtils.isEmpty(delementList)){
                    insertNum++;
                    DwDataElement de = doBuildDataElement(projectId,row,elementTypes,elementDicts);
                    de.setCategoryId(categoryId);
                    de.setCreateUser(userCode);
                    de.setCreateTime(new Date());
                    this.baseMapper.insert(de);
                }else{
                    updateNum++;
                    DwDataElement de = doBuildDataElement(projectId,row,elementTypes,elementDicts);
                    de.setId(delementList.get(0).getId());
                    de.setCategoryId(categoryId);
                    this.baseMapper.updateById(de);
                }
                //刷新进度值
                int currentPercent = (i+1)*100/excelData.size();
                int beforePercent = i*100/excelData.size();
                int cp = currentPercent / 10;
                int bp = beforePercent / 10;
                if(bp<cp && currentPercent!=100){
                    resultMap.put("percent",cp * 10);
                    //刷新进度值
                    cacheService.hmset("data_element","excel_import_progress:"+processCode,resultMap,600);
                }
            }
        } catch (Exception e) {
            log.error("DwDataElementServiceImpl-->uploadExcel 上传数据excel解析异常 ",e);
            e.printStackTrace();
            resultMap.put("percent",-1);
            resultMap.put("message","批量导入完成：<br/>1. 导入成功"+(insertNum+ updateNum+keepNum)+"条数据：其中新增"+insertNum+"条，有内容更新"
                    +updateNum+"条，无内容更新"+keepNum+"条；<br/>2. 导入失败"+excelErrResps.size() +"条知识，失败列表如下：");
            resultMap.put("result",excelErrResps);

            //刷新进度值
            cacheService.hmset("data_element","excel_import_progress:"+processCode,resultMap,600);
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
        cacheService.hmset("data_element","excel_import_progress:"+processCode,resultMap,600);
        return Result.ok("导入成功");
    }

    /**构建数据对象信息**/
    private DwDataElement doBuildDataElement(Long projectId,
                                             List<Object> row,
                                             List<Map<String,Object>> elementTypes,
                                             List<Map<String,Object>> elementDicts) {

        DwDataElement et = new DwDataElement();
        et.setProjectId(projectId);
        String exCel0 = row.get(0) == null ? "" : (row.get(0) + "").trim();
        String exCel1 = row.get(1) == null ? "" : (row.get(1) + "").trim();
        String exCel2 = row.get(2) == null ? "" : (row.get(2) + "").trim();
        String exCel3 = row.get(3) == null ? "" : (row.get(3) + "").trim();
        String exCel4 = row.get(4) == null ? "" : (row.get(4) + "").trim();
        String exCel5 = row.get(5) == null ? "" : (row.get(5) + "").trim();
        String exCel6 = row.get(6) == null ? "" : (row.get(6) + "").trim();
        String exCel7 = row.get(7) == null ? "" : (row.get(7) + "").trim();
        String exCel8 = row.get(8) == null ? "" : (row.get(8) + "").trim();
        et.setName(exCel0);
        et.setCode(exCel1);
        et.setAlias(exCel2);
        et.setDescription(exCel3);
        List<Map<String,Object>> typeStream = elementTypes.stream().filter(m->StringUtils.equals(exCel4,m.get("name")+"")).collect(Collectors.toList());

        if(CollectionUtils.isNotEmpty(typeStream)){
            et.setTypeId(Integer.parseInt(typeStream.get(0).get("id")+""));
        }

        et.setLength(Integer.parseInt(exCel5));
        et.setBusinessRules(exCel6);
        //遍历出 名称相同的数据集合
        List<Map<String,Object>> mstream = elementDicts.stream().filter(m->StringUtils.equals(exCel7,m.get("name")+"")).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(mstream)){
            et.setDictId(Long.parseLong(mstream.get(0).get("id")+""));
        }
        et.setDictName(exCel7);
        et.setCustomerRules(exCel8);
        return et;
    }


    /**获取导入文件进度**/
    @Override
    public R getImportProgress(String processCode){
        if(log.isInfoEnabled()) {
            log.info("DwDataElementServiceImpl-->getImportProgress 获取导入文件进度 ");
        }
        //获取缓存中的数据
        Map<Object,Object> redisData = cacheService.hmget("data_element","excel_import_progress:"+processCode);

        if(MapUtils.isEmpty(redisData)){
            Map<String,Object> mm = new HashMap<>();
            mm.put("percent",-1);
            mm.put("message","查询上传信息批次号有问题");
            return Result.ok(mm);
        }
        return Result.ok(redisData);

    }
}
