package com.dnt.data.standard.server.model.standard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.standard.dao.DwCategoryMapper;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.standard.entity.request.DwCategoryRequest;
import com.dnt.data.standard.server.model.standard.service.DwCategoryService;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description: 业务分类-服务接口层实现 <br>
 * @date: 2021/7/8 下午6:06 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwCategoryServiceImpl extends BaseServiceImpl<DwCategoryMapper, DwCategory> implements DwCategoryService {
    @Autowired
    private DwCategoryMapper dwCategoryMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<DwCategory> selectTreeList(Long projectId,String dwType) {
            if(log.isDebugEnabled()) {
                log.debug("DwCategoryServiceImpl-->selectTreeList 查看业务分类树型数据");
            }

            QueryWrapper<DwCategory> q = Wrappers.query();
            q.select("id","name","parent_id","is_leaf","path","level")
                    .eq("delete_model",1)
                    .eq("dw_type",dwType)
                    .eq("project_id",projectId);
            List<DwCategory> list = this.dwCategoryMapper.selectList(q);
            //NO.2 把查询的数据构建成树型结构
            List<DwCategory> lists = buildCusTreeCategory(list);

        return lists;
    }

    @Override
    public DwCategory detailCategory(Long id) {
        if(log.isDebugEnabled()) {
            log.debug("DwCategoryServiceImpl-->detailCategory 查看业务分类详情");
        }
        return this.dwCategoryMapper.selectById(id);
    }

    @Override
    public R deleteCategory(Long id, String userCode) {
        if(log.isDebugEnabled()){
            log.debug("DwCategoryServiceImpl-->deleteCategory 删除业务分类");
        }
        //NO.1 根据删除目录分类的ID 查询数据
        DwCategory c = this.dwCategoryMapper.selectById(id);
        if(c.getIsLeaf()==0){
            return Result.ok("当前目录下有子目录不允许删除").setMsg("FAIL");
        }

        //NO.2 查询分类是否有数据信息
        int recordsCount = selectRecordsCount(c.getId(),"dw_"+c.getDwType());
        if(recordsCount!=0){
            return Result.ok("当前目录下有数据不允许删除").setMsg("FAIL");
        }
        //NO.3 构建删除数据信息
        DwCategory dwc = new DwCategory();
        dwc.setId(id);
        dwc.setDeleteModel(0);
        dwc.setUpdateTime(new Date());
        dwc.setUpdateUser(userCode);
        //NO.4 执行删除操作
        int i = this.dwCategoryMapper.updateById(dwc);

        //NO.5 删除叶子节点后父节点下没有节点后 把父节点 状态修改
        Long pid = c.getParentId();
        QueryWrapper<DwCategory> q = Wrappers.query();
        q.eq("parent_id",pid).eq("delete_model",1);
        List<DwCategory> categoryList = this.dwCategoryMapper.selectList(q);
        if(CollectionUtils.isEmpty(categoryList)){

            DwCategory pdc = new DwCategory();
            pdc.setId(pid);
            pdc.setIsLeaf(1);
            pdc.setUpdateTime(new Date());
            pdc.setUpdateUser(userCode);
            this.dwCategoryMapper.updateById(pdc);
        }

        log.info("删除了{}条分类信息",i);
        return Result.ok("删除成功");
    }

    /**
     * 根据分类ID 与标识查询删除分类时的数据信息
     * @param categoryId
     * @param tableName
     * @return
     */
    private int selectRecordsCount(Long categoryId, String tableName ) {
        List<Map<String,Object>> list = new ArrayList<>();
        int ik =0;
        String sql ="select id from " + tableName +" where delete_model=1 and category_id = " + categoryId ;

        log.warn("========删除分类时查询分类下数据sql:{}========",sql);
        jdbcTemplate.query(sql,
                (RowCallbackHandler) res -> {
                    int count = res.getMetaData().getColumnCount();
                    String str ="";
                    for (int i = 1; i < count; i++) {
                        str += res.getString(i) + " ";
                    }
                    str += res.getString(count);
                    Map<String,Object> tempMap = new HashMap<>();
                    tempMap.put("field",res.getString(1));
                    list.add(tempMap);
                });
        log.warn("========删除分类时查询分类对应的数据条数为:{}========",list.size());
        return CollectionUtils.isEmpty(list)?ik:list.size();
    }

    /**
     * 编辑业务分类
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R updateCategory(DwCategoryRequest request, String userCode) {
        if(log.isDebugEnabled()){
            log.debug("DwCategoryServiceImpl-->updateCategory 编辑业务分类 ");
        }
        if(StringUtils.isEmpty(request.getName())){
            return Result.ok("修改信息时，名称不能为空");
        }

        String name = request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.ok("添加分类时名称不能为空");
        }

        Long nowCategoryId = request.getId();
        Long pid = Optional.fromNullable(request.getParentId()).isPresent()?request.getParentId():0L;

        QueryWrapper<DwCategory> query = new QueryWrapper<>();
        query.eq("delete_model",1).
                eq("name",name).
                eq("parent_id",pid).
                eq("project_id",request.getProjectId()).
                eq("dw_type",request.getDwType());
        List<DwCategory> entitys = this.dwCategoryMapper.selectList(query);

        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(entitys)){
            //判断名称是否存在
            entitys.forEach(na->{
                if(na.getId().longValue()!=nowCategoryId.longValue()){
                    haveNa.set(true);
                }

            });
        }
        if(haveNa.get()){
            return Result.ok("修改分类时，名称已存在");
        }


        //NO.1 构建数据
        DwCategory ac = new DwCategory();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ac);
        ac.setUpdateTime(new Date());
        ac.setUpdateUser(userCode);

        //NO.2 执行更新
        this.dwCategoryMapper.updateById(ac);
        //NO.3 更新路径 是否叶子节点  层级
        Long fid = request.getId();


        DwCategory fc = new DwCategory();
        fc.setId(fid);
        //查询是否有子目录
        QueryWrapper<DwCategory> ww = Wrappers.query();
        ww.like("path",fid);
        List<DwCategory> childCategory = this.dwCategoryMapper.selectList(ww);


        if (pid.longValue()==0){
            fc.setPath(fid+"");
            fc.setIsLeaf(CollectionUtils.isEmpty(childCategory)?1:0);
            fc.setLevel(1);
            fc.setParentId(0L);
        }else{
            DwCategory pft = this.dwCategoryMapper.selectById(pid);
            String patch = pft.getPath()+","+fid;
            fc.setPath(patch);
            fc.setIsLeaf(CollectionUtils.isEmpty(childCategory)?1:0);
            fc.setLevel(StringUtils.split(patch,",").length);
        }
        //NO.3 更新路径信息
        this.dwCategoryMapper.updateById(fc);

        return Result.ok("更新操作成功");
    }

    @Override
    public R saveCategory(DwCategoryRequest request, String userCode) {
        if(log.isDebugEnabled()) {
            log.debug("DwCategoryServiceImpl-->saveCategory 添加业务分类");
        }
        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("添加分类时分类名不能为空");
        }

        Long pid = Optional.fromNullable(request.getParentId()).isPresent()?request.getParentId():0L;

        //NO.1 查询输入的名称是否存在
        QueryWrapper<DwCategory> query = new QueryWrapper<>();
        query.eq("delete_model",1).
                eq("name",name).
                eq("parent_id",pid).
                eq("project_id",request.getProjectId()).
                eq("dw_type",request.getDwType());
        List<DwCategory> entitys = this.dwCategoryMapper.selectList(query);
        if(CollectionUtils.isNotEmpty(entitys)){
            return Result.fail("添加的分类名称已存在");
        }
        //NO.2 查询上级目录的数据信息
        //Long pid = request.getParentId();

        DwCategory ac = new DwCategory();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ac);
        if (pid.longValue()==0){
            //为空
            ac.setParentId(0L);
            ac.setLevel(1);
        }
        //设置一些属性值
        ac.setCreateUser(userCode);
        ac.setCreateTime(new Date());
        //NO.3 插入数据信息
        this.dwCategoryMapper.insert(ac);
        //获取保存后的ID   执行更新  路径  层级 中否为叶子节点信息
        Long id = ac.getId();
        if (pid.longValue()!=0L){
            //不为空时根据ID查询父级的目录数据
            DwCategory tempAc = this.dwCategoryMapper.selectById(pid);
            String pftPath = tempAc.getPath();
            //根据id更新 路径 是否为叶子节点 层级
            DwCategory acNow = new DwCategory();
            acNow.setId(id);
            acNow.setPath(pftPath+","+id);
            acNow.setLevel(tempAc.getLevel()+1);
            acNow.setIsLeaf(1);
            acNow.setUpdateTime(new Date());
            this.dwCategoryMapper.updateById(acNow);
            DwCategory acP = new DwCategory();
            acP.setId(pid);
            acP.setIsLeaf(0);  //把上一层级设置为非叶子节点
            acP.setUpdateTime(new Date());
            this.dwCategoryMapper.updateById(acP);
        }else{
            DwCategory acNow = new DwCategory();
            acNow.setId(id);
            acNow.setPath(id+"");
            acNow.setLevel(1);
            acNow.setUpdateTime(new Date());
            this.dwCategoryMapper.updateById(acNow);
        }

        return Result.ok("添加分类操作成功");
    }


    /**
     * 构建目录树
     * @param list
     * @return
     */
    private List<DwCategory> buildCusTreeCategory(List<DwCategory> list) {
        if(log.isInfoEnabled()) {
            log.info("DwCategoryServiceImpl-->buildCusTreeCategory 构建分类树结构");
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
        return dwCate;
    }
}
