package com.dnt.data.standard.server.model.version.service.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.service.impl.ReleaseStatusEnum;
import com.dnt.data.standard.server.model.standard.dao.DwCategoryMapper;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.version.dao.DwVersionMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersion;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseSelectDataRequest;
import com.dnt.data.standard.server.model.version.entity.response.DwVersionResponse;
import com.dnt.data.standard.server.model.version.service.DwVersionDataService;
import com.dnt.data.standard.server.model.version.service.DwVersionService;
import com.dnt.data.standard.server.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description: 管理管理-服务接口实现层 <br>
 * @date: 2022/4/14 上午9:53 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@Service
public class DwVersionServiceImpl extends BaseServiceImpl<DwVersionMapper,DwVersion> implements DwVersionService {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DwCategoryMapper dwCategoryMapper;
    @Autowired
    private DwVersionDataService dwVersionDataService;
    /**
     * 获取版本管理分页列表
     * @param request
     * @return
     */
    @Override
    public IPage<DwVersion> selectVersionPage(DwVersionRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->selectVersionPage 获取版本管理分页列表");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwVersion> page = new Page<>(pn,ps);

        QueryWrapper<DwVersion> query = Wrappers.query();
        query.like(StringUtils.isNotEmpty(request.getVersionName()),"version_name",request.getVersionName());

        return this.baseMapper.selectPage(page,query);
    }

    /**
     * 查看版本管理详情
     * @param id
     * @return
     */
    @Override
    public DwVersionResponse detailVersion(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->detailVersion 查看版本管理详情");
        }
        DwVersion dv = this.baseMapper.selectById(id);
        DwVersionResponse dr = DwVersionResponse.builder().build();
        BeanUtils.copyProperties(dv,dr);

        //查询 版本关联的  数据元  数据字典 指标、数据基础库信息
        QueryWrapper<DwVersionData> wq = Wrappers.query();
        wq.eq("delete_model",1).eq("version_id",id);
        List<DwVersionData> list = this.dwVersionDataService.list(wq);
        Map<String, List<Map<String,Object>>> m = new HashMap<>();

        for(DwVersionData vd:list){
            String tName = vd.getTableName();
            List<Map<String,Object>> lt = new ArrayList<>();
            for(DwVersionData cvd:list){
                String ctName = cvd.getTableName();
                if(StringUtils.equals(tName,ctName)){
                    String jsonStr = cvd.getDataJson();
                    Map<String,Object> jsonMap = JSON.parseObject(jsonStr,Map.class);
                    jsonMap.put("releaseStatusStr","已发布");
                    jsonMap.put("releaseStatus",1);
                    lt.add(jsonMap);
                }

            }

            if(CollectionUtils.isNotEmpty(lt)){
                m.put(StringUtils.substring(tName,3),lt);
            }
        }

        dr.setReleaseData(m);

        return dr;
    }

    /**
     * 删除版本管理
     * @param id
     * @return
     */
    @Override
    public R deleteVersion(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->deleteVersion 删除版本管理");
        }
        if(!Optional.ofNullable(id).isPresent()){
            return Result.fail("删除版本管理时ID不能为空");
        }
        //NO.1 执行逻辑删除操作
        int i = this.baseMapper.deleteById(id);
        return Result.ok("成功删除了"+i+"条版本管理信息");
    }

    /**
     * 根据项目ID 获取发布数据的目录树
     * @param projectId
     * @return
     */
    @Override
    public List<DwCategory> selectReleaseTree(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->selectReleaseTree 根据项目ID 获取发布数据的目录树");
        }


        QueryWrapper<DwCategory> q = Wrappers.query();
        q.select("id","name","parent_id","is_leaf","path","level","dw_type")
                .eq("delete_model",1)
                .eq("project_id",projectId);
        List<DwCategory> list = this.dwCategoryMapper.selectList(q);
        //NO.2 把查询的数据构建成树型结构
        List<DwCategory> lists = buildReleaseTree(list);
        return lists;
    }

    /**
     * 查询不同数据类型下的分页列表
     * @param request
     * @return
     */
    @Override
    public IPage<Map<String, Object>> selectCategoryPageList(CategoryPageListRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->selectCategoryPageList 查询不同数据类型下的分页列表 ");
        }
        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<CategoryPageListRequest> page = new Page<>(pn,ps);
        //分类的类型标识
        String dwType = request.getDwType();

        QueryWrapper<CategoryPageListRequest> wq = Wrappers.query();

        wq.eq("a.delete_model",1)
                .eq(Optional.ofNullable(request.getProjectId()).isPresent(),"a.project_id",request.getProjectId())
                .like(com.google.common.base.Optional.fromNullable(request.getCategoryId()).isPresent(),"b.path",request.getCategoryId());
        VersionCategoryDataList vcDataList = applicationContext.getBean(dwType,VersionCategoryDataList.class);

        IPage<Map<String,Object>> pp =vcDataList.selectCategoryDataPageList(page,wq);
        pp.getRecords().forEach(m->{
            Integer rStatus =0;
            if(m.containsKey("releaseStatus")){
                if(Optional.ofNullable(m.get("releaseStatus")).isPresent()){
                    rStatus = Integer.parseInt(m.get("releaseStatus").toString());
                }
            }

            m.put("releaseStatusStr", ReleaseStatusEnum.getValue(rStatus));
        });

        return pp;
    }

    /**
     * 查询不同类型下分页列表的表头
     * @return
     */
    @Override
    public Map<Integer, List<Map<Object, Object>>> selectCategoryPageHeader() {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->selectCategoryPageHeader 查询不同类型下分页列表的表头");
        }
        Map<Integer,List<Map<Object,Object>>> mHeader = MapUtil.newHashMap();
        List<Map<Object,Object>> m1 = ListUtil.list(false);
        Map<Object, Object> m11 = MapUtil.builder()
                //.put("id","id")
                .put("name","数据元名称")
                .put("code","标识编码")
                .put("releaseStatusStr","发布状态")
                .put("createUser","创建人")
                .put("description","描述")
                .build();
        m1.add(m11);

        mHeader.put(1,m1);
        List<Map<Object,Object>> m2 = ListUtil.list(false);
        Map<Object, Object> m21 = MapUtil.builder()
                //.put("id","id")
                .put("name","数据字典名称")
                .put("code","标识编码")
                .put("alias","别名")
                .put("releaseStatus","发布状态")
                .put("description","描述")
                .build();
        m2.add(m21);
        mHeader.put(2,m2);
        List<Map<Object,Object>> m3 = ListUtil.list(false);
        Map<Object, Object> m31 = MapUtil.builder()
                //.put("id","id")
                .put("name","指标名称")
                .put("code","编码")
                .put("alias","指标别名")
                .put("releaseStatus","发布状态")
                .put("source","来源系统")
                .build();
        m3.add(m31);
        mHeader.put(3,m3);
        List<Map<Object,Object>> m4 = ListUtil.list(false);
        Map<Object, Object> m41 = MapUtil.builder()
                //.put("id","id")
                .put("name","规则名称")
                .put("description","规则描述")
                .put("releaseStatus","发布状态")
                .put("mouldName","模型名")
                .build();
        m4.add(m41);

        mHeader.put(4,m4);
        List<Map<Object,Object>> m5 = ListUtil.list(false);
        Map<Object, Object> m51 = MapUtil.builder()
                //.put("id","id")
                .put("name","基础库名称")
                .put("code","基础库标识")
                .put("releaseStatus","发布状态")
                .put("description","描述")
                .build();
        m5.add(m51);
        mHeader.put(5,m5);

        return mHeader;
    }

    /**
     * 版本的发布功能
     * @param request
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R doVersionRelease(VersionReleaseRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->doVersionRelease 版本的发布功能");
        }
        //版本名
        String vName = request.getVersionName();
        //版本编号
        String vCode = request.getVersionCode();
        String referenceStandard = request.getReferenceStandard();

        if(StringUtils.isEmpty(vName)){
            return Result.fail("版本名称不能为空");
        }

        if(StringUtils.isEmpty(vCode)){
            return Result.fail("版本编号不能为空");
        }

        if(StringUtils.isEmpty(referenceStandard)){
            return Result.fail("版本参考标准不能为空");
        }
        Long pid = request.getProjectId();
        QueryWrapper<DwVersion> wq = Wrappers.query();
        wq.eq(Optional.ofNullable(pid).isPresent(),"project_id",pid)
                .eq(StringUtils.isNotEmpty(vName),"version_name",vName);
        List<DwVersion> vList = this.baseMapper.selectList(wq);
        if(CollectionUtils.isNotEmpty(vList)){
            return Result.fail("发布版本对应的版本名称已存在");
        }
        Map<String, List<VersionReleaseSelectDataRequest>> releaseData = request.getReleaseData();

        if(MapUtil.isEmpty(releaseData)){
            return Result.fail("版本发布时必须选择发布的数据");
        }
        //NO.1 记录发布的版本信息
        DwVersion v = new DwVersion();
        BeanUtils.copyProperties(request,v);
        int ii = baseMapper.insert(v);
        log.info("#####成功添加了{}条版本信息#####",ii);
        //NO.2 版本与知道点的关联关系
        Long vId = v.getId();
        if(!Optional.ofNullable(vId).isPresent()){
            return Result.fail("添加版本信息后ID不能为空");
        }

        //NO.3 更新相应的知识点的发布状态值
        /**把 数据元 数据字典 数据基础库 指标 模型命名规则 表中数据的是否发布更新为发布状态**/
        int rStatus =1;
        AtomicInteger ik= new AtomicInteger();
        for(String key:releaseData.keySet()){
            String tableName = "dw_"+key;
            List<Long> uIdList = new ArrayList<>();
            List<VersionReleaseSelectDataRequest> vSelectDataList = releaseData.get(key);
            vSelectDataList.forEach(d->{
                uIdList.addAll(d.getId());
                ik.getAndIncrement();
            });
            boolean update5TableStatus = baseMapper.update5TableReleaseStatus(rStatus,tableName,uIdList);
            log.info("把表中指定数据的发布状态设置为已发布状态 {}",update5TableStatus);
        }
        //NO.4 记录版本对应的记录日志数据【异步记录日志】

        dwVersionDataService.writeVersionReleaseLog(pid,vId,vCode,vName,releaseData);
        return Result.ok("成功发布了" + ik.get() +"条知识点");
    }

    /**==========================================自定义方法区===============================**/

    /**
     * 构建目录树
     * @param list
     * @return
     */
    private List<DwCategory> buildReleaseTree(List<DwCategory> list) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionServiceImpl-->buildCusTreeCategory 构建目录树");
        }
        List<DwCategory> dwCate = new ArrayList<>();
        // 把分类组成树结构
        list.forEach(parentCate->{
            Long parentId = parentCate.getParentId();
            Long cId = parentCate.getId();
            if(parentId.longValue()==0L) {
                dwCate.add(parentCate);
            }

            list.forEach(childCate->{
                Long childFid = childCate.getId();
                Long childPid = childCate.getParentId();
                if(!cId.equals(childFid)){
                    if(cId.equals(childPid)){
                        List<DwCategory> childs = parentCate.getChilds();
                        if (childs==null) {
                            childs = new ArrayList<>();
                        }
                        childs.add(childCate);
                        parentCate.setChilds(childs);
                    }
                }
            });

        });
        List<DwCategory> dcList = new ArrayList<>();
        for(String key:cateTypeMap.keySet()){
            DwCategory keyCategory = cateTypeMap.get(key);
            List<DwCategory> childCategory = new ArrayList<>();
            dwCate.forEach(p1->{
                String p1DwType = p1.getDwType();
                if(StringUtils.equals(key,p1DwType)){
                   childCategory.add(p1);
                }
            });
            keyCategory.setChilds(childCategory);
            dcList.add(keyCategory);
        }
        return dcList;
    }

    private Map<String,DwCategory> cateTypeMap = new HashMap<>();
    {
        DwCategory dc = new DwCategory();
        dc.setId(1L);
        dc.setName("数据元");
        dc.setParentId(1L);
        dc.setLevel(1);
        dc.setIsLeaf(1);
        dc.setPath("1");
        dc.setDwType("data_element");

        cateTypeMap.put("data_element",dc);
        dc = new DwCategory();
        dc.setId(2L);
        dc.setName("数据字典");
        dc.setParentId(2L);
        dc.setLevel(2);
        dc.setIsLeaf(2);
        dc.setPath("2");
        dc.setDwType("dict");
        cateTypeMap.put("dict",dc);

        dc = new DwCategory();
        dc.setId(3L);
        dc.setName("指标");
        dc.setParentId(3L);
        dc.setLevel(3);
        dc.setIsLeaf(3);
        dc.setPath("3");
        dc.setDwType("target");
        cateTypeMap.put("target",dc);

        dc = new DwCategory();
        dc.setId(4L);
        dc.setName("模型命名规则");
        dc.setParentId(4L);
        dc.setLevel(4);
        dc.setIsLeaf(4);
        dc.setPath("4");
        dc.setDwType("mould_name");
        cateTypeMap.put("mould_name",dc);

        dc = new DwCategory();
        dc.setId(5L);
        dc.setName("数据基础库");
        dc.setParentId(5L);
        dc.setLevel(5);
        dc.setIsLeaf(5);
        dc.setPath("5");
        dc.setDwType("db_base");
        cateTypeMap.put("db_base",dc);
    }
}
