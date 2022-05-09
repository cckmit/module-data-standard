package com.dnt.data.standard.server.model.resource.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.client.DataSourceClient;
import com.dnt.data.standard.server.model.resource.dao.DwMouldResourceMapper;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResource;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResourceOperatorLog;
import com.dnt.data.standard.server.model.resource.entity.request.*;
import com.dnt.data.standard.server.model.resource.entity.response.CategoryStatisticInfoResponse;
import com.dnt.data.standard.server.model.resource.entity.response.DataDistributionResponse;
import com.dnt.data.standard.server.model.resource.entity.response.DataPreviewResponse;
import com.dnt.data.standard.server.model.resource.entity.response.DwMouldResourceStatisticResponse;
import com.dnt.data.standard.server.model.resource.service.DwMouldResourceService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.dao.DwCategoryMapper;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.utils.MapCompare;
import com.dnt.data.standard.server.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @description: 模型资源--服务接口层实现 <br>
 * @date: 2021/10/12 上午9:52 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Service
@Slf4j
public class DwMouldResourceServiceImpl extends BaseServiceImpl<DwMouldResourceMapper, DwMouldResource> implements DwMouldResourceService {
    private String patchSplit=",";
    @Autowired
    private DwCategoryMapper dwCategoryMapper;
    @Autowired
    private DataSourceClient dataSourceClient;
    @Autowired
    private CacheService cacheService;
    /**
     * 获取资源分页列表
     * @param request
     * @return
     */
    @Override
    public IPage<DwMouldResource> selectMouldResourcePage(DwMouldResourceRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->selectMouldResourcePage 获取资源分页列表");
        }

        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwMouldResource> page = new Page<>(pn,ps);
        QueryWrapper<DwMouldResource> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq("a.project_id",request.getProjectId())
               // .eq(Optional.ofNullable(request.get()).isPresent(),"a.data_source_id",request.getProjectId())
                .eq(Optional.ofNullable(request.getDbId()).isPresent(),"a.db_id",request.getDbId())
                .eq(Optional.ofNullable(request.getType()).isPresent(),"a.type",request.getType())
                .eq(Optional.ofNullable(request.getStatus()).isPresent(),"a.status",request.getStatus())
                .like(StringUtils.isNotEmpty(request.getSearchContent()),"a.name",request.getSearchContent())
                .like(Optional.ofNullable(request.getCategoryId()).isPresent(),"c.path",request.getCategoryId())
                .orderByDesc("a.id");
        IPage<DwMouldResource> mouldResourceIPage =  baseMapper.selectMouldResourcePage(page,q);
        return mouldResourceIPage;
    }

    /**
     * 模型资源的上下线
     * @param request
     * @return
     */
    @Override
    public R updateResourceStatus(DwMouldResourceRequest request) {
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceServiceImpl-->updateResourceStatus 模型资源的上下线");
        }

        if(!Optional.ofNullable(request.getId()).isPresent()){
            return Result.fail("模型资源上下线的时资源ID不能为空");
        }
        if(!Optional.ofNullable(request.getStatus()).isPresent()){
            return Result.fail("模型资源上下线的时资源状态值不能为空");
        }
        //NO.1 构建数据
        DwMouldResource mr = new DwMouldResource();
        mr.setId(request.getId());
        mr.setStatus(request.getStatus());
        //NO.2执行更新
        int i = baseMapper.updateById(mr);
        log.info("模型资源上线/下线了 {}条数据",i);
        return Result.ok("操作成功");
    }


    /**
     * 删除模型资源
     * @param ids
     * @param userCode
     * @return
     */
    @Override
    public int deleteMouldResource(List<Long> ids, String userCode) {
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceServiceImpl-->deleteMouldResource 删除模型资源");
        }
        //NO.1 构建数据
        QueryWrapper<DwMouldResource> rq = Wrappers.query();
        rq.in("id",ids);
        rq.eq("delete_model",1);
        //NO.2 执行删除操作

        return baseMapper.deleteMouldResourceBatch(rq,userCode);
    }

    /**
     * 获取引入数据源类型的下拉列表
     * @return
     */
    @Override
    public JSONArray selectSourceTypeItem() {

        System.out.println("DwMouldResourceServiceImpl-->selectSourceTypeItem 获取引入数据源类型的下拉列表");

        JSONObject resJson = dataSourceClient.selectSourceTypeItem("6");
        if(ObjectUtils.isEmpty(resJson) || resJson.getInteger("code")!=200){
            log.error("DwMouldResourceServiceImpl-->selectSourceTypeItem 调用远程接口 失败");
            return new JSONArray();
        }

        JSONArray dataArray = resJson.getJSONArray("data");
        for(int i=0;i<dataArray.size();i++){
            JSONObject dObj = dataArray.getJSONObject(i);
            if(dObj==null || dObj.isEmpty()) {
                dataArray.remove(dObj);
            }
        }
        return dataArray;
    }


    /**
     * 根据数据源类型查询数据源信息
     * @param typeId
     * @return
     */
    @Override
    public JSONArray selectSourceItem(Long typeId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->selectSourceItem 根据数据源类型查询数据源信息");
        }
        if(typeId==-1){

            JSONObject resJson = dataSourceClient.selectSourceTypeItem("6");
            JSONArray typeJA = resJson.getJSONArray("data");
            log.info("数据源分类的JSON结果：{}",typeJA.toJSONString());
            JSONArray ja = new JSONArray();

            for (int i = 0; i < typeJA.size(); i++) {
                JSONObject jaObj = typeJA.getJSONObject(i);
                if(jaObj==null || jaObj.isEmpty()){
                    continue;
                }
                Long tId = jaObj.getLong("id");
                if(!Optional.ofNullable(tId).isPresent()){
                    continue;
                }
                JSONObject resDataJson = dataSourceClient.selectSourceItem("6",tId);

                JSONArray childResDataJson = resDataJson.getJSONArray("data");
                for(int ic =0;ic<childResDataJson.size();ic++){
                    ja.add(childResDataJson.get(ic));
                }
            }

            return ja;
        }
        //调用远程服务 查询引用数据源类型下对应的数据源
        JSONObject resJson = dataSourceClient.selectSourceItem("6",typeId);
        if(ObjectUtils.isEmpty(resJson) || resJson.getInteger("code")!=200){
            log.error("DwMouldResourceServiceImpl-->selectSourceTypeItem 调用远程接口 失败");
            return new JSONArray();
        }

        return resJson.getJSONArray("data");

        //return this.baseMapper.selectSourceItem(typeId);

    }

    /**
     * 根据项目信息查询数据库下拉列表
     * @param projectId
     * @return
     */
    @Override
    public List<Map<String,Object>> selectDbItem(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->selectDbItem 根据项目信息查询数据库下拉列表");
        }
        JSONObject dJson = dataSourceClient.getDataSourceById("6",projectId);
        Long randomDbId = IdWorker.getId();
        if(ObjectUtils.isEmpty(dJson) || dJson.getInteger("code")!=200){
            log.error("DwMouldResourceServiceImpl-->selectDbItem 调用远程接口 失败");
            return new ArrayList<>();
        }

        JSONArray jaData = dJson.getJSONArray("data");
        if(jaData.isEmpty()){
            log.error("DwMouldResourceServiceImpl-->selectDbItem 远程调用接口获取指定数据源的信息为空");
            return new ArrayList<>();
        }
        log.info("远程调用接口获取指定数据源的信息：{}",jaData.toJSONString());

        List<Map<String,Object>> ml = new ArrayList<>();
        Map<String,String> haveDbName = new HashMap<>();
        for(int ji =0 ;ji<jaData.size();ji++){

            JSONObject ja = jaData.getJSONObject(ji);

            JSONObject dj = ja.getJSONObject("dataJson");

            String driverName = ja.getString("driverName");
            if(StringUtils.isNotEmpty(driverName)){
                dj.put("driverName",driverName);
            }
            String url = dj.getString("jdbcUrl");
            String dbName = "";
            String regex = "[0-9]/\\w+(?=\\w*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(url);
            while (matcher.find()){
                dbName= matcher.group().substring(2);
                log.info("截取后的数据库名为：{}",dbName);
            }
            if(StringUtils.isEmpty(dbName)){
                log.error("DwMouldResourceServiceImpl-->selectDbItem 新建数据源时没有指定的库名");
            }

            if(haveDbName.containsKey(dbName)){
                continue;
            }
            cacheService.hmset("mould","selectDbItem:"+randomDbId,dj,3600);
            haveDbName.put(dbName,dbName);

            Map<String,Object> mm = new HashMap<>();
            mm.put("dbId",randomDbId);
            mm.put("dbName",dbName);
            ml.add(mm);
        }

        return ml;

        //return this.baseMapper.selectDbItem(projectId);
    }

    /**
     * 数据库表下拉列表
     * @param dbId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectTableItem(Long dbId)  {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->selectTableItem 数据库表下拉列表");
        }
        List<Map<String,Object>> tableList = new ArrayList<>();
        try {
            //获取数据源对应的配置信息
            Map<Object,Object> mm = cacheService.hmget("mould","selectDbItem:" + dbId);
            Map<String,Object> configMap = buildDbConfigMap(mm);
            log.info("连接数据库配置信息：{}",configMap);
            //根据数据库连接配置获取库下的表信息
            DataSource ds = DruidDataSourceFactory.createDataSource(configMap);

            JdbcTemplate jt = new JdbcTemplate(ds);
            List<Map<String,Object>> mlist = jt.queryForList("show tables");

            mlist.forEach(m->{
                Set<Map.Entry<String,Object>> msets = m.entrySet();
                for(Map.Entry entry:msets){
                    Object vObj = entry.getValue();
                    if(ObjectUtils.isEmpty(vObj)){
                        continue;
                    }
                    Long tid = IdWorker.getId();
                    Map<String,Object> rMap = new HashMap<>();
                    rMap.put("tableId",tid);
                    rMap.put("tableName",vObj);
                    tableList.add(rMap);
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tableList;
        //return this.baseMapper.selectTableItem(dbId);

    }

    /**
     * 构建数据库连接信息
     * @param mm
     * @return
     */
    private Map<String, Object> buildDbConfigMap(Map<Object, Object> mm) {
        Map<String,Object> m = new HashMap<>();
        m.put("url",mm.get("jdbcUrl"));
        m.put("driverClassName", mm.get("driverName"));
        m.put("username", mm.get("username"));
        m.put("password", mm.get("password"));

        return m;
    }

    /**
     * 模型资源发布操作
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R doResourceRelease(String userCode, DwMouldResourceReleaseRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->doResourceRelease 模型资源发布操作");
        }
        //NO.1 构建数据
        DwMouldResource mr = new DwMouldResource();
        BeanUtil.copyProperties(request,mr);

        if(!Optional.ofNullable(mr.getCategoryId()).isPresent()){
            return Result.fail("发布数据时分类目录不能为空");
        }
        if(!Optional.ofNullable(mr.getType()).isPresent()){
            return Result.fail("发布数据时类型不能为空");
        }

        if(StringUtils.isEmpty(mr.getName())){
            return Result.fail("发布数据时资源名(表名)不能为空");
        }
        List<DwMouldResource> lists = findDwMouldResourceByName(mr.getName(),mr.getCategoryId());
        if(com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(lists)){
            return Result.fail("发布数据时名称已存在");
        }

        //NO.2 构建分类路径与分类路径中文名称
        Map<String,String> mc = doBuildCategoryPatch(mr.getCategoryId());
        if(MapUtils.isNotEmpty(mc)){
            String path = mc.get("path");
            mr.setCategoryPath(path);
            String pathName = mc.get("pathName");
            mr.setCategoryPathName(pathName);
        }
        mr.setProjectId(request.getProjectId());
        mr.setCreateUser(userCode);
        mr.setOwnerName(userCode);
        mr.setCreateTime(new Date());

        //NO.3 写入发布信息的数据
        int i = this.baseMapper.insert(mr);
        log.info("发布成功了{}条数据",i);
        return Result.ok("发布资源操作成功");
    }


    private List<DwMouldResource> findDwMouldResourceByName(String name, Long categoryId) {
        QueryWrapper<DwMouldResource> q = Wrappers.query();
        q.select("id,name").eq("delete_model",1)
                .eq("name",name)
                .eq(com.google.common.base.Optional.fromNullable(categoryId).isPresent(),"category_id",categoryId);
        return this.baseMapper.selectList(q);
    }

    /**
     * 模型资源统计汇总信息
     * @return
     */
    @Override
    public R getResourceStatistic(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->getResourceStatistic 模型资源统计汇总信息");
        }


        Map<String, List<DwMouldResourceStatisticResponse>> mstatistic = new HashMap<>();
        mstatistic.put("metaData",buildMetaStatisticData(projectId));
        mstatistic.put("category",buildCategoryStatisticData(projectId));

        return Result.ok(mstatistic);
    }

    /**
     * 资产盘点查询已有资源的数据类型
     * @return
     */
    @Override
    public R getHaveDataDataSourceType(Long projectId){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->getHaveDataDataSourceType 资产盘点查询已有资源的数据类型");
        }

        List<Map<String,Object>> dstList = buildHaveDataSourceType(projectId);

        return Result.ok(dstList);
    }

    /**
     * 构建已发布资源的 数据源类型
     * @return
     */
    private List<Map<String,Object>> buildHaveDataSourceType(Long projectId) {
        QueryWrapper<DwMouldResource> q = Wrappers.query();
        q.eq("a.delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"a.project_id",projectId)
                .eq("a.status",1)
                .groupBy("a.data_source_type_id");
        return this.baseMapper.selectHaveDataDataSourceType(q);
    }


    /**
     * 资产盘点已接入数据源数据预览
     * @return
     */
    @Override
    public R getDataPreview(Long projectId) {
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceServiceImpl-->getDataPreview 资产盘点已接入数据源数据预览 ");
        }
        //NO.1 已有数据的数据源类型
        List<Map<String,Object>> list = buildHaveDataSourceType(projectId);
        //NO.2 昨日所有分类下的发布资源数据
        List<Map<String,Long>> yesterDayReleases = this.baseMapper.selectYesterdayRelease(projectId);

        //NO.3 查询出所有已发布的资源信息
        QueryWrapper<DwMouldResource> mr = Wrappers.query();
        mr.eq("delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"project_id",projectId)
                .eq("status",1);
        List<Map<String,Object>> resourceList = this.baseMapper.selectResourceReleases(mr);

        List<DataPreviewResponse> prResult = new ArrayList<>();

        //NO.4 构建返回数据
        list.forEach(m->{
            DataPreviewResponse pr = new DataPreviewResponse();
            if(MapUtils.isEmpty(m)){
                return;
            }
            if(!Optional.ofNullable(m.get("dataSourceTypeId")).isPresent()){
                return;
            }
            Long dataSourceTypeId =  Long.parseLong(m.get("dataSourceTypeId")+"");
            String dsName = m.get("dataSourceTypeName").toString();
            //根据数据源分类查询数据
//            Integer yesterdayCount = 0;
            List<Long> yesterdayCounts  = yesterDayReleases
                    .stream()
                    .filter(mt->Long.parseLong(mt.get("yesterdayCount")+"")>0 && Long.parseLong(mt.get("dataSourceTypeId")+"") == dataSourceTypeId)
                    .map(myc->myc.get("yesterdayCount")).collect(Collectors.toList());

            //昨日发布资源的数量
            if(CollectionUtils.isNotEmpty(yesterdayCounts)){

                pr.setYesterdayCount(yesterdayCounts.get(0));
            }else{
                pr.setYesterdayCount(0L);
            }


            pr.setDataSourceTypeId(dataSourceTypeId);
            pr.setDataSourceTypeName(dsName);
            //指定分类下的数据源
            Map<Object, List<Map<String, Object>>> dataSourceMap = resourceList
                    .stream()
                    .filter(mt->Optional.ofNullable(mt.get("dataSourceTypeId")).isPresent() && Long.parseLong(mt.get("dataSourceTypeId")+"")==dataSourceTypeId)
                    .collect(Collectors.groupingBy(b->b.get("dataSourceId")));
            //数据源数量
            if(MapUtils.isEmpty(dataSourceMap)){
                pr.setDataSourceCount(0);
            }else{
                pr.setDataSourceCount(dataSourceMap.size());
            }
            //数据源下库的数量
            Map<Object, List<Map<String, Object>>> dataDbMap = resourceList
                    .stream()
                    .filter(mt->Optional.ofNullable(mt.get("dataSourceTypeId")).isPresent() && Long.parseLong(mt.get("dataSourceTypeId")+"")==dataSourceTypeId)
                    .collect(Collectors.groupingBy(b->b.get("dbId")));
            //数据库数量
            if(MapUtils.isEmpty(dataDbMap)){
                pr.setDbCount(0);
            }else{
                pr.setDbCount(dataDbMap.size());
            }

            //数据源下表的数量
            Map<Object, List<Map<String, Object>>> dataTableMap = resourceList
                    .stream()
                    .filter(mt->Optional.ofNullable(mt.get("dataSourceTypeId")).isPresent() && Long.parseLong(mt.get("dataSourceTypeId")+"")==dataSourceTypeId)
                    .collect(Collectors.groupingBy(b->b.get("name")));
            //数据库数量
            if(MapUtils.isEmpty(dataTableMap)){
                pr.setTableCount(0);
            }else{
                pr.setTableCount(dataTableMap.size());
            }
            //存储大小 设置为0
            pr.setStorageSize(0.0d);
            prResult.add(pr);

        });
        return Result.ok(prResult);
    }

    /**
     * 资产盘点数据地图分布
     * @param request
     * @return
     */
    @Override
    public R getDataDistribution(DataDistributionRequest request) {
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceServiceImpl-->getDataPreview 资产盘点已接入数据源数据预览 ");
        }
        Long datasourceTypeId = request.getDataSourceType();
        Long projectId = request.getProjectId();
        //NO.1 查询出所有已发布的资源信息
        QueryWrapper<DwMouldResource> mr = Wrappers.query();
        mr.eq("delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"project_id",projectId)
                .eq("status",1)
                .eq("data_source_type_id",datasourceTypeId);
        List<Map<String,Object>> resourceList = this.baseMapper.selectResourceReleases(mr);
        int resourceSize =resourceList.size();
        Map<Object, List<Map<String, Object>>> dataMap = resourceList
                .stream()
                .collect(Collectors.groupingBy(b->b.get("ownerName")));
        //NO.2 构建数据
        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数

        List<DataDistributionResponse> mres = new ArrayList<>();
        for(Object o :dataMap.keySet()){
            String key = o.toString();
            List<Map<String,Object>> ownerData = dataMap.get(key);


            DataDistributionResponse dr = new DataDistributionResponse();
            dr.setOwnerName(key);
            float tCount = CollectionUtils.isEmpty(ownerData)?0:ownerData.size();
            dr.setTableCount(tCount);
            dr.setPercentage(tCount>0?df.format(tCount/resourceSize):df.format(0));

            mres.add(dr);

        }

        //NO.3 数据集合排序
        List<DataDistributionResponse>ll = mres.stream()
                .sorted(Comparator.comparing(DataDistributionResponse::getTableCount).reversed())
                .collect(Collectors.toList());

        return Result.ok(ll);
    }

    /**
     * 资产盘点数据资源分布
     * @param request
     * @return
     */
    @Override
    public R getCategoryStatisticInfo(DataDistributionRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getCategoryStatisticInfo 据资源分布 ");
        }
        //NO.1 查看根据条件所有的资源信息
        Long projectId = request.getProjectId();
        //NO.1 查询出所有已发布的资源信息
        QueryWrapper<DwMouldResource> mr = Wrappers.query();
        mr.eq("delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"project_id",projectId)
                .eq("status",1);
        List<Map<String,Object>> resourceList = this.baseMapper.selectResourceReleases(mr);
        int resourceSize =resourceList.size();


        CategoryStatisticInfoResponse csir = new CategoryStatisticInfoResponse();

        Map<Object, List<Map<String, Object>>> dataSourceMap = resourceList
                .stream()
                .collect(Collectors.groupingBy(b->b.get("dataSourceId")));
        int sourceCount = MapUtils.isEmpty(dataSourceMap)?0:dataSourceMap.size();

        Map<Object, List<Map<String, Object>>> dataDbMap = resourceList
                .stream()
                .collect(Collectors.groupingBy(b->b.get("dbId")));
        int dbCount = MapUtils.isEmpty(dataDbMap)?0:dataDbMap.size();

        Map<Object, List<Map<String, Object>>> dataTableMap = resourceList
                .stream()
                .collect(Collectors.groupingBy(b->b.get("name")));
        int tableCount = MapUtils.isEmpty(dataTableMap)?0:dataTableMap.size();

        Map<Object, List<Map<String, Object>>> dataSecondMap = resourceList
                .stream()
                .filter(m->(m.get("categoryPath")+"").split(patchSplit).length>=2)
                .collect(Collectors.groupingBy(b-> Arrays.asList((b.get("categoryPath")+"").split(patchSplit)).get(0)+patchSplit+Arrays.asList((b.get("categoryPath")+"").split(patchSplit)).get(1) ));
        float secondCount = MapUtils.isEmpty(dataSecondMap)?0:dataSecondMap.size();

        Map<Object, List<Map<String, Object>>> dataThirdMap = resourceList
                .stream()
                .filter(m->(m.get("categoryPath")+"").split(patchSplit).length>=3)
                .collect(Collectors.groupingBy(b->  Arrays.asList((b.get("categoryPath")+"").split(patchSplit)).get(0)+patchSplit+Arrays.asList((b.get("categoryPath")+"").split(patchSplit)).get(1)+Arrays.asList((b.get("categoryPath")+"").split(patchSplit)).get(2)  ));
        float thirdCount = MapUtils.isEmpty(dataThirdMap)?0:dataThirdMap.size();


        csir.setSourceCount(sourceCount);
        csir.setDbCount(dbCount);
        csir.setTableCount(tableCount);
        csir.setSecondCount(secondCount);
        csir.setThirdCount(thirdCount);
        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数
        csir.setPercentage(df.format(thirdCount/resourceSize));

        csir.setStorageSize(0d);
        return Result.ok(csir);
    }

    /**
     * 资产盘点数据价值排行
     * @param request
     * @return
     */
    @Override
    public R  getDataValueRank(DataValueRankRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getDataValueRank 资产盘点数据价值排行 ");
        }

        List<Map<String,Object>> mm = this.baseMapper.selectDataValueRank(request.getProjectId(),request.getDataSourceType());

        return Result.ok(mm);
    }

    /**
     * 资产盘点数据库Top10
     * @param request
     * @return
     */
    @Override
    public R getTop10Databases(DataValueRankRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getTop10Databases 资产盘点数据库Top10 ");
        }
        List<Map<String,Object>> mks = this.baseMapper.selectTop10Tables(request.getProjectId(),request.getDataSourceType());
        Double totalDiskSize = mks.stream().mapToDouble(m->(double)m.get("showCount")).sum();
        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数
        //增加表的占比
        mks.forEach(m->{
            Double sc = Double.parseDouble(m.get("showCount").toString());
            if(totalDiskSize.longValue()==0L || sc.longValue()==0L){
                m.put("radio",df.format(0));
            }else {
                m.put("radio", df.format(sc / totalDiskSize));
            }
        });

        Map<String,Object> mr = new HashMap<>();
        mr.put("totalStorage",totalDiskSize);
        mr.put("dbList",mks);
        return Result.ok(mr);
    }

    /**
     * 资产盘点数据表Top10
     * @param request
     * @return
     */
    @Override
    public R getTop10Tables(DataValueRankRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getTop10Tables 资产盘点数据表Top10");
        }
        List<Map<String,Object>> mk = this.baseMapper.selectTop10Tables(request.getProjectId(),request.getDataSourceType());
        return Result.ok(mk);
    }

    /**
     * 资产盘点元资产查询趋势
     * @param request
     * @return
     */
    @Override
    public R getDataIncrementTrend(DataDistributionRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getDataIncrementTrend 资产盘点元资产查询趋势 ");
        }
        Long dataSourceType = request.getDataSourceType();
        Integer interval = request.getInterval();
        Long projectId = request.getProjectId();

        //查询按修改日期分组的数量
        List<Map<String,Object>> result = new ArrayList<>();

        if(interval!=3) {
            //查询按创建日期分组的数量
            List<Map<String, Object>> incrementTrendInsertList = this.baseMapper.selectIncrementTrendDayInsertList(projectId,dataSourceType);

            List<Map<String, Object>> incrementTrendUpdateList = this.baseMapper.selectIncrementTrendDayUpdateList(projectId,dataSourceType);
            if(interval==1){
                //7天
                result = doBuild7DayResult(incrementTrendInsertList,incrementTrendUpdateList);
            }else if(interval==2){
                //30天
                result = doBuild30DayResult(incrementTrendInsertList,incrementTrendUpdateList);
            }
        }else{
            //365天
            result = doBuild365DayResult(projectId,dataSourceType);
        }


        return Result.ok(result);
    }

    /**
     * 构建7天的返回数据
     * @param insertList
     * @param updateList
     * @return
     */
    private List<Map<String, Object>> doBuild7DayResult(List<Map<String, Object>> insertList,
                                                        List<Map<String, Object>> updateList) {
        List<Map<String,Object>> result = new ArrayList<>();
        Date sevenDayAgo =  DateUtils.addDays(new Date(),-7);

        for(int i=0;i<7;i=i+2){
            Date nowD = DateUtils.addDays(sevenDayAgo,i);
            String strD = DateFormatUtils.format(nowD,"yyyy-MM-dd");
            int insertCount =0,updateCount=0;

            List<Object> insert =  insertList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("insertCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                insertCount= Integer.parseInt(insert.get(0)+"");
            }

            List<Object> update =  updateList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("updateCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(update)){
                updateCount= Integer.parseInt(update.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("day",strD);
            m.put("insertCount",insertCount);
            m.put("updateCount",updateCount);

           result.add(m);
        }
        return result;
    }

    /**
     * 构建30天的返回数据
     * @param insertList
     * @param updateList
     * @return
     */
    private List<Map<String, Object>> doBuild30DayResult(List<Map<String, Object>> insertList, List<Map<String, Object>> updateList) {
        List<Map<String,Object>> result = new ArrayList<>();
        Date oneMonthAgo =  DateUtils.addMonths(new Date(),-1);
        for(int i=0;i<36;i=i+6) {
            Date nowD = DateUtils.addDays(oneMonthAgo,i);
            String strD = DateFormatUtils.format(nowD,"yyyy-MM-dd");
            int insertCount =0,updateCount=0;

            List<Object> insert =  insertList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("insertCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                insertCount= Integer.parseInt(insert.get(0)+"");
            }

            List<Object> update =  updateList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("updateCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(update)){
                updateCount= Integer.parseInt(update.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("day",strD);
            m.put("insertCount",insertCount);
            m.put("updateCount",updateCount);

            result.add(m);
        }
        return result;
    }

    /**
     * 构建365天的返回数据
     * @param dataSourceType
     * @return
     */
    private List<Map<String, Object>> doBuild365DayResult(Long projectId,Long dataSourceType) {
        if(log.isInfoEnabled()){
            log.info("DwMouldResourceServiceImpl-->doBuild365DayResult 构建365天的返回数据");
        }

        List<Map<String,Object>> insertList = this.baseMapper.selectIncrementTrendMonthInsertList(projectId,dataSourceType);
        List<Map<String,Object>> updateList = this.baseMapper.selectIncrementTrendMonthUpdateList(projectId,dataSourceType);
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i =0;i<12;i=i+2){
            Date monthD = DateUtils.addMonths(new Date(),-i);
            String strD = DateFormatUtils.format(monthD,"yyyy-MM");
            int insertCount =0,updateCount=0;

            List<Object> insert =  insertList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("insertCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                insertCount= Integer.parseInt(insert.get(0)+"");
            }

            List<Object> update =  updateList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("updateCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(update)){
                updateCount= Integer.parseInt(update.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("id",12-i);
            m.put("day",strD);
            m.put("insertCount",insertCount);
            m.put("updateCount",updateCount);

            result.add(m);
        }


        List<Map<String,Object>> ll = result.stream()
                .sorted(Comparator.comparing(MapCompare::comparingById))
                .collect(Collectors.toList());
        return ll;
    }

    /**
     * 资产盘点资产查询趋势
     * @param request
     * @return
     */
    @Override
    public R getSearchTrend(DataDistributionRequest request) {
        if(log.isInfoEnabled()){
            System.out.println("DwMouldResourceServiceImpl-->getSearchTrend 资产盘点资产查询趋势 ");
        }
        Integer interval = request.getInterval();
        Integer searchType = request.getSearchType();
        Long projectId = request.getProjectId();
        //查询按修改日期分组的数量
        List<Map<String,Object>> result = new ArrayList<>();

        if(interval!=3) {
            //查询按创建日期分组的数量
            List<Map<String, Object>> searchTrends = this.baseMapper.selectDaySearchTrends(projectId,searchType);

            if(interval==1){
                //7天
                result = doBuild7DaySearchTrends(searchTrends);
            }else if(interval==2){
                //30天
                result = doBuild30DaySearchTrends(searchTrends);
            }
        }else{
            //365天
            result = doBuild365DaySearchTrends(projectId,searchType);
        }


        return Result.ok(result);
    }

    private List<Map<String, Object>> doBuild365DaySearchTrends(Long projectId,Integer searchType) {
        List<Map<String,Object>> rList = this.baseMapper.selectMonthSearchTrends(projectId,searchType);
        List<Map<String,Object>> result = new ArrayList<>();
        for(int i =0;i<12;i=i+2){
            Date monthD = DateUtils.addMonths(new Date(),-i);
            String strD = DateFormatUtils.format(monthD,"yyyy-MM");
            int sc =0;

            List<Object> insert =  rList.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("showCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                sc= Integer.parseInt(insert.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("id",12-i);
            m.put("day",strD);
            m.put("insertCount",sc);

            result.add(m);
        }


        List<Map<String,Object>> ll = result.stream()
                .sorted(Comparator.comparing(MapCompare::comparingById))
                .collect(Collectors.toList());
        return ll;
    }

    private List<Map<String, Object>> doBuild30DaySearchTrends(List<Map<String, Object>> searchTrends) {
        List<Map<String,Object>> result = new ArrayList<>();
        Date oneMonthAgo =  DateUtils.addMonths(new Date(),-1);
        for(int i=0;i<36;i=i+6) {
            Date nowD = DateUtils.addDays(oneMonthAgo,i);
            String strD = DateFormatUtils.format(nowD,"yyyy-MM-dd");
            int sc =0;

            List<Object> insert =  searchTrends.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("showCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                sc= Integer.parseInt(insert.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("day",strD);
            m.put("showCount",sc);

            result.add(m);
        }
        return result;
    }

    private List<Map<String, Object>> doBuild7DaySearchTrends(List<Map<String, Object>> searchTrends) {
        List<Map<String,Object>> result = new ArrayList<>();
        Date sevenDayAgo =  DateUtils.addDays(new Date(),-7);

        for(int i=0;i<7;i=i+2){
            Date nowD = DateUtils.addDays(sevenDayAgo,i);
            String strD = DateFormatUtils.format(nowD,"yyyy-MM-dd");
            int sCount =0;

            List<Object> insert =  searchTrends.stream().
                    filter(f->StringUtils.equals(strD,f.get("dayStr")+"")).
                    map(m->m.get("showCount")).
                    collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(insert)){
                sCount= Integer.parseInt(insert.get(0)+"");
            }

            Map<String,Object> m  = new LinkedHashMap<>();
            m.put("day",strD);
            m.put("showCount",sCount);

            result.add(m);
        }
        return result;
    }

    /**
     * 联想输入
     * @param projectId
     * @param searchContent
     * @return
     */
    @Override
    public List<Map<String,Object>> inputTips(Long projectId,String searchContent){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->inputTips 联想输入");
        }
        List<Map<String,Object>> ms = this.baseMapper.selectResourceTips(projectId,searchContent);
        return ms;
    }

    /**
     * 查看/搜索操作记录
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R doOperator(String userCode, OperatorRequest request){
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->doOperator 查看/搜索操作记录 ");
        }

        //NO.1 根据资源ID获取相应的数据信息
        DwMouldResource dr = this.baseMapper.selectById(request.getResourceId());

        //NO.2构建数据
        DwMouldResourceOperatorLog oLog = new DwMouldResourceOperatorLog();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,oLog);

        if(!Optional.ofNullable(dr).isPresent()){
            return Result.fail("指定的资源ID，没有对应的资源信息");
        }
        oLog.setId(IdWorker.getId());
        oLog.setProjectId(request.getProjectId());
        oLog.setResourceId(request.getResourceId());
        oLog.setOperatorFlag(request.getOperatorFlag());
        oLog.setDbId(dr.getDbId());
        oLog.setDbName(dr.getName());
        oLog.setTableName(dr.getName());
        oLog.setOperatorUser(userCode);
        oLog.setCreateUser(userCode);
        //NO.3 持久化操作
        int ii = baseMapper.insertResourceOperatorLog(oLog);
        log.warn("doOperator 成功添加了{}条操作记录",ii);
        return Result.ok("操作成功");
    }

    /**
     * 数据库下的字段下拉列表
     * @param tableId
     * @return
     */
    @Override
    public List<Map<String, Object>> selectTableFieldItem(Long tableId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldResourceServiceImpl-->selectTableFieldItem 数据库下的字段下拉列表");
        }


        return this.baseMapper.selectTableFieldItem(tableId);
    }

    /**
     * 构建资源分布（根据目录汇总）
     * @return
     */
    private List<DwMouldResourceStatisticResponse> buildCategoryStatisticData(Long projectId) {
        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数
        //查询发布的 资源
        QueryWrapper<DwMouldResource> mrq = Wrappers.query();
        mrq.eq("a.delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"a.project_id",projectId)
                .eq("a.status",1)
                .groupBy("a.category_id");
        List<Map<String,Object>> mCategoryList =this.baseMapper.selectCategoryStatisticData(mrq);
        //NO.2 发布资源的总条数
        double sumCount = mCategoryList.stream().mapToDouble(map->Double.parseDouble(map.get("value").toString())).sum();

        List<DwMouldResourceStatisticResponse> rsrList = new ArrayList<>();
        mCategoryList.forEach(m->{
            DwMouldResourceStatisticResponse sr = new DwMouldResourceStatisticResponse();
            Long t = Long.parseLong(m.get("value").toString());
            sr.setId(Long.parseLong(m.get("id").toString()));
            sr.setName((String)m.get("name"));
            sr.setValue(new BigDecimal(t));
            sr.setPercentage(t==0L||sumCount==0?df.format(0):df.format(t/sumCount) );
            rsrList.add(sr);
        });

        //NO.3 数据集合排序
        List<DwMouldResourceStatisticResponse> ll = rsrList.stream()
                .sorted(Comparator.comparing(DwMouldResourceStatisticResponse::getValue).reversed())
                .collect(Collectors.toList());

        return ll;
    }

    /**
     * 构建发布资源汇总信息
     * @return
     */
    private List<DwMouldResourceStatisticResponse> buildMetaStatisticData(Long projectId) {
        QueryWrapper<DwMouldResource> mrq = Wrappers.query();
        mrq.eq("a.delete_model",1)
                .eq(Optional.ofNullable(projectId).isPresent(),"a.project_id",projectId)
                .eq("a.status",1);
        Map<String,Object> mp = this.baseMapper.selectMetaStatisticData(mrq);

        List<DwMouldResourceStatisticResponse> res = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数
        DwMouldResourceStatisticResponse sr = new DwMouldResourceStatisticResponse();
        Long allLong = MapUtils.isNotEmpty(mp)&&mp.containsKey("ALL")? Long.parseLong(mp.get("ALL").toString()):0L;
        Long tcLong = MapUtils.isNotEmpty(mp)&&mp.containsKey("tableCount")? Long.parseLong(mp.get("tableCount").toString()):0L;
        Long qcLong = MapUtils.isNotEmpty(mp)&&mp.containsKey("quotaCount")? Long.parseLong(mp.get("quotaCount").toString()):0L;
        BigDecimal all = new BigDecimal(allLong).setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal tc = new BigDecimal(tcLong).setScale(2,BigDecimal.ROUND_HALF_UP);
        BigDecimal qc = new BigDecimal(qcLong).setScale(2,BigDecimal.ROUND_HALF_UP);
        sr.setId(0L);
        sr.setName("资源总量");
        sr.setValue(all);
        sr.setPercentage("");
        res.add(sr);

        sr = new DwMouldResourceStatisticResponse();
        sr.setId(1L);
        sr.setName("数据表");
        sr.setValue(tc);
        sr.setPercentage(df.format(allLong==0?0:tc.divide(all,2)));
        res.add(sr);

        sr = new DwMouldResourceStatisticResponse();
        sr.setId(2L);
        sr.setName("指标");
        sr.setValue(qc);
        sr.setPercentage(df.format(allLong==0?0:qc.divide(all,2)));
        res.add(sr);

        return res;
    }

    /**
     * 构建 选择分类目录的全路径与中文名称
     * @param categoryId
     * @return
     */
    private Map<String, String> doBuildCategoryPatch(Long categoryId) {
        String dwType="mould_resource";

        Map<String,String> mr = new HashMap<>();

        QueryWrapper<DwCategory> cq = Wrappers.query();
        cq.eq("delete_model",1)
                .eq("dw_type",dwType);
        List<DwCategory> categoryList = this.dwCategoryMapper.selectList(cq);

        List<String> pathList = categoryList.stream()
                .filter(a->categoryId.doubleValue() ==a.getId().doubleValue())
                .map(DwCategory::getPath)
                .collect(Collectors.toList());

        StringBuffer pathName = new StringBuffer();
        if(CollectionUtils.isNotEmpty(pathList)){
            //获取当前数据的全路径
            String path[] = pathList.get(0).split(",");
            //构建路径名
            for (String s : path) {
                List<String> pName = categoryList.stream().filter(c->s.equals(c.getId().toString()))
                        .map(DwCategory::getName).collect(Collectors.toList());

                if(CollectionUtils.isEmpty(pName)){
                    continue;
                }

                pathName.append(pName.get(0)).append("/");
            }
        }

        mr.put("path", CollectionUtils.isEmpty(pathList)?"":pathList.get(0));
        mr.put("pathName",pathName.length()==0?"":pathName.deleteCharAt(pathName.length()-1).toString());
        return mr;
    }

}
