package com.dnt.data.standard.server.model.resource.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.resource.ResourceContents;
import com.dnt.data.standard.server.model.resource.dao.DwResourcePanoramaMapper;
import com.dnt.data.standard.server.model.resource.entity.DwResourcePanorama;
import com.dnt.data.standard.server.model.resource.entity.request.CategoryLinkRequest;
import com.dnt.data.standard.server.model.resource.entity.request.DwResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.entity.request.ResourcePanoramaRequest;
import com.dnt.data.standard.server.model.resource.entity.response.CategoryLinkResponse;
import com.dnt.data.standard.server.model.resource.service.DwResourcePanoramaService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 资产全景--服务接口实现层 <br>
 * @date: 2021/11/8 下午2:01 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Service
@Slf4j
public class DwResourcePanoramaServiceImpl extends BaseServiceImpl<DwResourcePanoramaMapper, DwResourcePanorama> implements DwResourcePanoramaService {

    private String splitChar = ",";

    /**
     * 资产全景列表
     * @param request
     * @return
     */
    @Override
    public List<DwResourcePanorama> selectPanoramaList(DwResourcePanoramaRequest request) {
        QueryWrapper<DwResourcePanorama> q = Wrappers.query();
        q.eq("delete_model", 1)
                .eq(Optional.ofNullable(request.getProjectId()).isPresent(),"project_id",request.getProjectId())
                .like(StringUtils.isNotEmpty(request.getName()), "name", request.getName())
                .orderByDesc("id");

        return this.baseMapper.selectList(q);
    }

    /**
     * 更新资产全景状态
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R updatePanoramaStatus(String userCode, DwResourcePanoramaRequest request) {

        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->updatePanoramaStatus 更新资产全景状态");
        }
        Long id = request.getId();
        Integer status = request.getStatus();
        //NO.1 获取表中当前资产全景的信息
        DwResourcePanorama pdb = this.baseMapper.selectById(id);
        if (!Optional.ofNullable(pdb).isPresent()) {
            return Result.fail("根据ID查询全景信息为空");
        }
        //状态的判断
        if (status.equals(pdb.getStatus())) {
            return Result.fail(status == 0 ? "待上线的资产全景不能下线操作" : "已上线的资产全景不能上线操作");
        }
        //NO.2 更新操作
        DwResourcePanorama panorama = new DwResourcePanorama();
        panorama.setId(id);
        panorama.setStatus(status);
        int ii = this.baseMapper.updateById(panorama);
        log.warn("成功操作了{}条数据", ii);

        return Result.ok("操作成功");
    }

    /**
     * 获取资产全景下拉列表
     * @return
     */
    @Override
    public List<Map<String, Object>> getResourcePanoramaItem() {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->getResourcePanoramaItem 获取资产全景下拉列表");
        }
        //查询所有已上线的  资产全景
        return this.baseMapper.getResourcePanoramaItem();
    }

    /**
     * 获取模型链路配置列表
     * @return
     */
    @Override
    public List<Map<String, Object>> selectMouldCategory() {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl.selectMouldCategory获取模型链路配置列表");
        }
        //获取模型链路配置列表
        return this.baseMapper.selectMouldCategory();
    }

    /**
     * 选择模型链路中展示模型层级
     * @param request
     * @return
     */
    @Override
    public List<CategoryLinkResponse> selectCategoryLink(CategoryLinkRequest request) {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->selectCategoryLink 选择模型链路中展示模型层级");
        }

        //NO.1 构建查询条件
        QueryWrapper<CategoryLinkResponse> q = Wrappers.query();
        q.eq("delete_model", 1).gt("parent_id", 0);

        List<CategoryLinkResponse> list = this.baseMapper.selectCategoryLink(q);


        return buildCategory(list, 2, request.getCategoryIds());
    }

    /**
     * 查看资产全景详情
     * @param id
     * @return
     */
    @Override
    public ResourcePanoramaRequest detailResourcePanorama(Long id) {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->detailResourcePanorama 查看资产全景详情");
        }
        DwResourcePanorama rp = this.baseMapper.selectById(id);
        ResourcePanoramaRequest panorama = new ResourcePanoramaRequest();
        BeanValueTrimUtil.beanValueTrim(rp);
        BeanUtils.copyProperties(rp,panorama);
        String topCategoryIds = rp.getMouldTopCategoryIds();
        panorama.setMouldTopCategoryIds(id2Long(topCategoryIds));
        String topCategoryNames = rp.getMouldTopCategoryNames();
        panorama.setMouldTopCategoryNames(Arrays.asList(topCategoryNames.split(splitChar)));
        String sourceIds = rp.getSourceIds();
        panorama.setSourceIds(id2Long(sourceIds));
        String sourceNames = rp.getSourceNames();
        panorama.setSourceNames(Arrays.asList(sourceNames.split(splitChar)));

        String applicationIds = rp.getApplicationIds();
        panorama.setApplicationIds(id2Long(applicationIds));
        String applicationNames = rp.getApplicationNames();
        panorama.setApplicationNames(Arrays.asList(applicationNames.split(splitChar)));

        return panorama;
    }

    /**
     * 添加资产全景
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R saveResourcePanorama(ResourcePanoramaRequest request, String userCode) {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->saveResourcePanorama 添加资产全景");
        }

        //NO.1 判断集合数据是否一致
        List<Long> mouldTopCategoryIds = request.getMouldTopCategoryIds();
        List<String> mouldTopCategoryNames = request.getMouldTopCategoryNames();
        if (CollectionUtils.isEmpty(mouldTopCategoryIds) || CollectionUtils.isEmpty(mouldTopCategoryNames)) {
            return Result.fail("模型顶级分类目录信息不能為空");
        }
        if (mouldTopCategoryIds.size() != mouldTopCategoryNames.size()) {
            return Result.fail("模型顶级分类目录信息ID与名称不一致");
        }

        List<Long> sourceIds = request.getSourceIds();
        List<String> sourceNames = request.getSourceNames();
        if (CollectionUtils.isEmpty(sourceIds) || CollectionUtils.isEmpty(sourceNames)) {
            return Result.fail("业务数据来源信息不能为空");
        }
        if (sourceIds.size() != sourceNames.size()) {
            return Result.fail("业务数据来源信息ID与名称不一致");
        }

        List<Long> applicationIds = request.getApplicationIds();
        List<String> applicationNames = request.getApplicationNames();
        if (CollectionUtils.isEmpty(applicationIds) || CollectionUtils.isEmpty(applicationNames)) {
            return Result.fail("应用信息不能为空");
        }
        if (applicationIds.size() != applicationNames.size()) {
            return Result.fail("应用信息ID与名称不一致");
        }
        //构建数据
        DwResourcePanorama rp = new DwResourcePanorama();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,rp);
        rp.setMouldTopCategoryIds(mouldTopCategoryIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setMouldTopCategoryNames(mouldTopCategoryNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setSourceIds(sourceIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setSourceNames(sourceNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setApplicationIds(applicationIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setApplicationNames(applicationNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setCreateUser(userCode);
        //NO.2 持久化数据
        int ii = this.baseMapper.insert(rp);
        log.warn("成功添加了{}条资产全景的信息",ii);
        return Result.ok("添加资产全景操作成功");
    }

    /**
     * 修改资产全景
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R updateResourcePanorama(ResourcePanoramaRequest request, String userCode) {
        if (log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->updateResourcePanorama 修改资产全景");
        }
        //NO.1 判断集合数据是否一致
        List<Long> mouldTopCategoryIds = request.getMouldTopCategoryIds();
        List<String> mouldTopCategoryNames = request.getMouldTopCategoryNames();
        if (CollectionUtils.isEmpty(mouldTopCategoryIds) || CollectionUtils.isEmpty(mouldTopCategoryNames)) {
            return Result.fail("模型顶级分类目录信息不能為空");
        }
        if (mouldTopCategoryIds.size() != mouldTopCategoryNames.size()) {
            return Result.fail("模型顶级分类目录信息ID与名称不一致");
        }

        List<Long> sourceIds = request.getSourceIds();
        List<String> sourceNames = request.getSourceNames();
        if (CollectionUtils.isEmpty(sourceIds) || CollectionUtils.isEmpty(sourceNames)) {
            return Result.fail("业务数据来源信息不能为空");
        }
        if (sourceIds.size() != sourceNames.size()) {
            return Result.fail("业务数据来源信息ID与名称不一致");
        }

        List<Long> applicationIds = request.getApplicationIds();
        List<String> applicationNames = request.getApplicationNames();
        if (CollectionUtils.isEmpty(applicationIds) || CollectionUtils.isEmpty(applicationNames)) {
            return Result.fail("应用信息不能为空");
        }
        if (applicationIds.size() != applicationNames.size()) {
            return Result.fail("应用信息ID与名称不一致");
        }
        //构建数据
        DwResourcePanorama rp = new DwResourcePanorama();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,rp);
        rp.setMouldTopCategoryIds(mouldTopCategoryIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setMouldTopCategoryNames(mouldTopCategoryNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setSourceIds(sourceIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setSourceNames(sourceNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setApplicationIds(applicationIds.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setApplicationNames(applicationNames.stream().map(s -> s + "").collect(Collectors.joining(splitChar)));
        rp.setUpdateUser(userCode);
        rp.setUpdateTime(new Date());
        //NO.2 持久化数据
        int iu = baseMapper.updateById(rp);
        log.warn("成功更新了{}条资产全景的信息",iu);
        return Result.ok("修改资产全景操作成功");
    }

    /**
     * 渲染资产全景的层级关系
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R doRenderResourcePanorama(String userCode, DwResourcePanoramaRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->doRenderResourcePanorama 渲染资产全景的层级关系");
        }

        QueryWrapper<DwResourcePanorama> q = Wrappers.query();
        q.eq("delete_model",1)
                .eq("id",request.getId())
                .eq(Optional.ofNullable(request.getProjectId()).isPresent(),"project_id",request.getProjectId());

        //NO.1 根据ID查询资产全景的节点信息
        DwResourcePanorama rp = this.baseMapper.selectOne(q);
        if(ObjectUtils.isEmpty(rp)){
            return Result.fail("暂无配置资产全景的节点信息");
        }
        //业务数据 来源
        String sourceIds = rp.getSourceIds();
        List<Long> sourceList = id2Long(sourceIds);
        String sourceNames = rp.getSourceNames();
        List<String> sourceNameList = Arrays.asList(sourceNames.split(splitChar));
        //数据分析
        Long linkId = rp.getLinkCategoryId();
        String linkName = rp.getLinkCategoryName();
        //业务应用
        String applicationIds = rp.getApplicationIds();
        List<Long> applicationList = id2Long(applicationIds);
        String applicationNames = rp.getApplicationNames();
        List<String> applicationNameList = Arrays.asList(applicationNames.split(splitChar));


        JSONObject renderJson =new JSONObject();
        renderJson.put("nodes",buildNodes(applicationList,applicationNameList,linkId,linkName,sourceList,sourceNameList));
        renderJson.put("edges",buildEdges(applicationList,applicationNameList,linkId,linkName,sourceList,sourceNameList));
        renderJson.put("combos",buildCombos(applicationList,applicationNameList,linkId,linkName,sourceList,sourceNameList));

        return Result.ok(renderJson);
    }

    /**
     * 下一级资产全景的数据与层级关系
     * @param userCode
     * @param request
     * @return
     */
    @Override
    public R doRenderChildPanorama(String userCode, DwResourcePanoramaRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwResourcePanoramaServiceImpl-->doRenderChildPanorama 下一级资产全景的数据与层级关系");
        }

        JSONObject renderJson =new JSONObject();
        renderJson.put("nodes",new JSONArray());
        renderJson.put("edges",new JSONArray());
        renderJson.put("combos",new JSONArray());
        return Result.ok(renderJson);
    }

    /**
     * 构建 node节点
     * @param applicationList
     * @param applicationNameList
     * @param linkId
     * @param linkName
     * @param sourceList
     * @param sourceNameList
     * @return
     */
    private JSONArray buildNodes(List<Long> applicationList, List<String> applicationNameList, Long linkId, String linkName, List<Long> sourceList, List<String> sourceNameList) {
        JSONArray node = new JSONArray();
        //第一层
        for(int i=0;i<applicationList.size();i++){
            Long aid = applicationList.get(i);
            String aname = applicationNameList.get(i);

            String id =aid+"";
            String label ="";
            String comboId ="combo_" + aname;
            int levelId = 1;
            int nodeKey =1;
            int comboKey = i+1;
            int comboNodeTotal=1;
            int levelComboTotal = applicationList.size();
            int levelRowTotal = applicationList.size();

            node.add(ResourceContents.node(id,label,comboId,
                        levelId,nodeKey,comboKey,
                        comboNodeTotal,levelComboTotal,levelRowTotal));
        }
        //第二层

        String id2 =linkId+"";
        String label2 ="";
        String comboId2 ="combo_" + linkName;
        int levelId2 = 2;
        int nodeKey2 =1;
        int comboKey2 = 1;
        int comboNodeTotal2=1;
        int levelComboTotal2 = 1;
        int levelRowTotal2 = 1;
        node.add(ResourceContents.node(id2,label2,comboId2,
                levelId2,nodeKey2,comboKey2,
                comboNodeTotal2,levelComboTotal2,levelRowTotal2));

        //第三层
        for(int i=0;i<sourceList.size();i++){
            Long sourceId = sourceList.get(i);
            String sname = sourceNameList.get(i);

            String sid = sourceId+"";
            String slabel =sname;
            String scomboId ="combo_" + sname;
            int slevelId = 3;
            int snodeKey =1;
            int scomboKey = i+1;
            int scomboNodeTotal=1;
            int slevelComboTotal = sourceList.size();
            int slevelRowTotal = sourceList.size();

            node.add(ResourceContents.node(sid,slabel,scomboId,
                    slevelId,snodeKey,scomboKey,
                    scomboNodeTotal,slevelComboTotal,slevelRowTotal));
        }

        return node;
    }

    /**
     * 构建节点之间的连线
     * @param applicationList
     * @param applicationNameList
     * @param linkId
     * @param linkName
     * @param sourceList
     * @param sourceNameList
     * @return
     */
    private JSONArray buildEdges(List<Long> applicationList, List<String> applicationNameList, Long linkId, String linkName, List<Long> sourceList, List<String> sourceNameList) {
        JSONArray node = new JSONArray();
        for(int i=0;i<sourceList.size();i++) {
            String target = sourceList.get(i)+"";
            String source = linkId+"";
            node.add(ResourceContents.edge(target,source));
        }

        return node;
    }

    /**
     * 构建数据组合框
     * @param applicationList
     * @param applicationNameList
     * @param linkId
     * @param linkName
     * @param sourceList
     * @param sourceNameList
     * @return
     */
    private JSONArray buildCombos(List<Long> applicationList, List<String> applicationNameList, Long linkId, String linkName, List<Long> sourceList, List<String> sourceNameList) {
        JSONArray node = new JSONArray();
        //新建管理数据的层
        //业务应用层
        node.add(ResourceContents.combo("combo_layer1","业务应用层"));
        //数据分析层
        node.add(ResourceContents.combo("combo_layer2","数据分析层"));
        //业务数据层
        node.add(ResourceContents.combo("combo_layer3","业务数据层"));
        //新建节点之第一层
        for(int i=0;i<applicationNameList.size();i++){
            String aname = applicationNameList.get(i);
            node.add(ResourceContents.comboParent("combo_"+aname,aname,"combo_layer1"));
        }
        //新建节点之第二层
        node.add(ResourceContents.comboParent("combo_"+linkName,linkName,"combo_layer2"));
        //新建节点之第三层
        for(int i=0;i<sourceNameList.size();i++){
            String sname = sourceNameList.get(i);
            node.add(ResourceContents.comboParent("combo_"+sname,sname,"combo_layer3"));
        }
        return node;
    }

    /**
     * 构建树型数据
     * @param list
     * @param leaveDefault
     * @param ids
     * @return
     */
    private List<CategoryLinkResponse> buildCategory(List<CategoryLinkResponse> list, Integer leaveDefault, List<Long> ids) {
        List<CategoryLinkResponse> dwCate = new ArrayList<>();
        // 把分类组成树结构
        list.forEach(parentCate -> {
            Integer leaveValue = parentCate.getLevel();
            Long cId = parentCate.getId();
            Long pId = parentCate.getParentId();
            ids.forEach(d -> {
                if (leaveValue.intValue() == leaveDefault.intValue() && d.doubleValue() == pId.doubleValue()) {
                    dwCate.add(parentCate);
                }
                list.forEach(childCate -> {
                    Long childFid = childCate.getId();
                    Long childPid = childCate.getParentId();
                    if (!cId.equals(childFid)) {
                        if (cId.equals(childPid)) {
                            List<CategoryLinkResponse> childs = parentCate.getChilds();
                            if (childs == null) {
                                childs = new ArrayList<>();
                            }
                            childs.add(childCate);
                            parentCate.setChilds(childs);
                        }
                    }
                });
            });


        });
        return dwCate;
    }

    /**
     * 把ID字符串转成 Long集合
     * @param ids
     * @return
     */
    private List<Long> id2Long(String ids) {
        List<Long> it = new ArrayList<>();
        String[] strIds = ids.split(splitChar);
        for (String strId : strIds) {
            it.add(Long.parseLong(strId.trim()));
        }
        return it;
    }

}
