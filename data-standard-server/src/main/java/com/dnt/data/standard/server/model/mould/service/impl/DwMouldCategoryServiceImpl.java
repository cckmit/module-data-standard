package com.dnt.data.standard.server.model.mould.service.impl;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwMouldCategoryMapper;
import com.dnt.data.standard.server.model.mould.dao.DwMouldMapper;
import com.dnt.data.standard.server.model.mould.entity.DwMould;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.mould.entity.request.DwMouldCategoryRequest;
import com.dnt.data.standard.server.model.mould.service.DwMouldCategoryService;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.dnt.data.standard.server.web.Result;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @description:  模型层级--服务接口实现层 <br>
 * @date: 2021/8/2 下午6:07 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Service
@Slf4j
public class DwMouldCategoryServiceImpl extends BaseServiceImpl<DwMouldCategoryMapper, DwMouldCategory> implements DwMouldCategoryService {

    @Autowired
    private DwMouldCategoryMapper dwMouldCategoryMapper;
    @Autowired
    private DwMouldMapper dwMouldMapper;

    /**
     * 获模型层级分类树型列表
     * @param projectId
     * @return
     */
    @Override
    public List<DwMouldCategory> selectTreeList(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->selectTreeList 获模型层级分类树型列表");
        }
        //NO.1 构建查询条件
        QueryWrapper<DwMouldCategory> q = Wrappers.query();
        q.select("id","name","parent_id","is_leaf","path","level")
                .eq("project_id",projectId)
                .eq("delete_model",1);
        List<DwMouldCategory> list = this.dwMouldCategoryMapper.selectList(q);
        //NO.2 把查询的数据构建成树型结构
        List<DwMouldCategory> lists = buildTreeCategory(list,1);
        return lists;
    }

    /**
     * 分类目录树没有子目录则不展示一级目录
     * @param projectId
     * @return
     */
    @Override
    public List<DwMouldCategory> selectNewTreeList(Long projectId) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->selectNewTreeList 分类目录树没有子目录则不展示一级目录");
        }
        //NO.1 构建查询条件
        QueryWrapper<DwMouldCategory> q = Wrappers.query();
        q.select("id","name","parent_id","is_leaf","path","level")
                .eq("delete_model",1)
                .eq("project_id",projectId)
                .gt("parent_id",0);
        List<DwMouldCategory> list = this.dwMouldCategoryMapper.selectList(q);
        //NO.2 把查询的数据构建成树型结构
        List<DwMouldCategory> lists = buildTreeCategory(list,2);
        return lists;
    }

    /**
     * 获取模型层级分类分页列表
     * @param request
     * @return
     */
    @Override
    public IPage<DwMouldCategory> selectDwMouldCategoryPage(DwMouldCategoryRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->selectDwMouldCategoryPage 获取模型层级分类分页列表");
        }
        //NO.1 构建查询条件
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwMouldCategory> page = new Page<>(pn,ps);
        QueryWrapper<DwMouldCategory> q = Wrappers.query();
        q.eq("delete_model",1)
                .eq("project_id",request.getProjectId())
                .eq("parent_id",0)
                .like(Optional.fromNullable(request.getName()).isPresent(),"name",request.getName())
                .orderByDesc("id");
        //NO.2 执行查询
        return this.dwMouldCategoryMapper.selectPage(page,q);
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @Override
    public DwMouldCategory detailMouldCategory(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->detailMouldCategory 查看详情");
        }
        return dwMouldCategoryMapper.selectById(id);
    }

    /**
     * 添加分类
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R saveMouldCategory(DwMouldCategoryRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl.saveMouldCategory 添加分类");
        }

        //名称
        String name =request.getName();
        if(StringUtils.isEmpty(name)){
            return Result.fail("添加模型层级分类时分类名不能为空");
        }

        //如果编号为空则名称的首字母
        if(StringUtils.isEmpty(request.getCode())){
            String cc = PinyinUtil.getFirstLetter(request.getName(),"");
            request.setCode(cc);
        }
        Long pid = Optional.fromNullable(request.getParentId()).isPresent()?request.getParentId():0L;

        //NO.1 查询输入的名称是否存在
        QueryWrapper<DwMouldCategory> query = new QueryWrapper<>();
        query.select("id,code,name")
                .eq("delete_model",1)
                .eq("parent_id",pid)
                .eq("name",name);
        List<DwMouldCategory> entitys = this.dwMouldCategoryMapper.selectList(query);
        if(CollectionUtils.isNotEmpty(entitys)){
            return Result.fail("添加的分类名称已存在");
        }
        //NO.2 查询上级目录的数据信息

       // Long pid = request.getParentId();
        DwMouldCategory mc = new DwMouldCategory();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,mc);
        if (pid.longValue()==0L){
            //为空
            mc.setParentId(0L);
            mc.setLevel(1);
        }
        //设置一些属性值
        mc.setCreateUser(userCode);
        mc.setCreateTime(new Date());
        //NO.3 插入数据信息
        this.dwMouldCategoryMapper.insert(mc);
        //获取保存后的ID   执行更新  路径  层级 中否为叶子节点信息
        Long id = mc.getId();
        if (pid.longValue()!=0L){
            //不为空时根据ID查询父级的目录数据
            DwMouldCategory tempAc = this.dwMouldCategoryMapper.selectById(pid);
            String pftPath = tempAc.getPath();
            //根据id更新 路径 是否为叶子节点 层级
            DwMouldCategory mcNow = new DwMouldCategory();
            mcNow.setId(id);
            mcNow.setPath(pftPath+","+id);
            mcNow.setLevel(tempAc.getLevel()+1);
            mcNow.setIsLeaf(1);
            mcNow.setUpdateTime(new Date());
            this.dwMouldCategoryMapper.updateById(mcNow);
            DwMouldCategory mcP = new DwMouldCategory();
            mcP.setId(pid);
            //把上一层级设置为非叶子节点
            mcP.setIsLeaf(0);
            mcP.setUpdateTime(new Date());
            this.dwMouldCategoryMapper.updateById(mcP);
        }else{
            DwMouldCategory acNow = new DwMouldCategory();
            acNow.setId(id);
            acNow.setPath(id+"");
            acNow.setLevel(1);
            acNow.setUpdateTime(new Date());
            this.dwMouldCategoryMapper.updateById(acNow);
        }

        return Result.ok("添加分类操作成功");
    }


    /**
     * 修改分类
     * @param request
     * @param userCode
     * @return
     */
    @Override
    public R updateMouldCategory(DwMouldCategoryRequest request, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->updateMouldCategory 修改分类");
        }
        String name = request.getName();
        Long id = request.getId();
        if(StringUtils.isEmpty(request.getName())){
            return Result.ok("修改信息时，名称不能为空");
        }
        Long pid = Optional.fromNullable(request.getParentId()).isPresent()?request.getParentId():0L;


        QueryWrapper<DwMouldCategory> query = new QueryWrapper<>();
        query.select("id,code,name")
                .eq("delete_model",1)
                .eq("parent_id",pid)
                .eq("name",name);
        List<DwMouldCategory> lists = this.dwMouldCategoryMapper.selectList(query);

        Long nowNAID = request.getId();
        AtomicBoolean haveNa = new AtomicBoolean(false);
        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(lists)){
            //判断名称是否存在
            lists.forEach(na->{
                if(na.getId().longValue()!=nowNAID.longValue()){
                    haveNa.set(true);
                }

            });
        }

        if(haveNa.get()){
            return Result.ok("修改模型分类时，名称已存在");
        }

        //如果不输入则名称的首字母
        if(StringUtils.isEmpty(request.getCode())){
            String cc = PinyinUtil.getFirstLetter(request.getName(),"");
            request.setCode(cc);
        }

        //NO.1 构建数据
        DwMouldCategory ac = new DwMouldCategory();
        BeanValueTrimUtil.beanValueTrim(request);
        BeanUtils.copyProperties(request,ac);
        ac.setUpdateTime(new Date());
        ac.setUpdateUser(userCode);

        //NO.2 执行更新
        this.dwMouldCategoryMapper.updateById(ac);
        //NO.3 更新路径 是否叶子节点  层级
        Long fid = request.getId();

        DwMouldCategory fc = new DwMouldCategory();
        fc.setId(fid);
        //查询是否有子目录
        QueryWrapper<DwMouldCategory> ww = Wrappers.query();
        ww.like("path",fid);
        List<DwMouldCategory> childCategory = this.dwMouldCategoryMapper.selectList(ww);


        if (pid.longValue()!=0L) {
            DwMouldCategory pft = this.dwMouldCategoryMapper.selectById(pid);
            String patch = pft.getPath()+","+fid;
            fc.setPath(patch);
            fc.setIsLeaf(CollectionUtils.isEmpty(childCategory)?1:0);
            fc.setLevel(StringUtils.split(patch,",").length);
        }else{
            fc.setPath(fid+"");
            fc.setIsLeaf(CollectionUtils.isEmpty(childCategory)?1:0);
            fc.setLevel(1);
            fc.setParentId(0L);
        }
        //NO.3 更新路径信息
        this.dwMouldCategoryMapper.updateById(fc);
        return Result.ok("更新操作成功");
    }

    /**
     * 删除分类
     * @param id
     * @param userCode
     * @return
     */
    @Override
    public R deleteMouldCategory(Long id, String userCode) {
        if(log.isInfoEnabled()) {
            log.info("DwMouldCategoryServiceImpl-->deleteMouldCategory 删除分类");
        }

        //NO.1 根据删除目录分类的ID 查询数据
        DwMouldCategory c = this.dwMouldCategoryMapper.selectById(id);
        if(c.getIsLeaf()==0){
            return Result.ok("当前目录下有子目录不允许删除").setMsg("FAIL");
        }

        //NO.2 查询分类是否有数据信息
        int recordsCount = selectRecordsCount(id);
        if(recordsCount!=0){
            return Result.ok("当前目录下有数据不允许删除").setMsg("FAIL");
        }

        //NO.3 构建数据
        DwMouldCategory mc = new DwMouldCategory();
        mc.setId(id);
        mc.setDeleteModel(0);
        mc.setUpdateUser(userCode);
        mc.setUpdateTime(new Date());
        //NO.4 执行删除操作
        int ik = this.dwMouldCategoryMapper.updateById(mc);

        //NO.5 删除叶子节点后父节点下没有节点后 把父节点 状态修改
        Long pid = c.getParentId();
        QueryWrapper<DwMouldCategory> q = Wrappers.query();
        q.eq("parent_id",pid).eq("delete_model",1);
        List<DwMouldCategory> categoryList = this.baseMapper.selectList(q);
        if(CollectionUtils.isEmpty(categoryList)){

            DwMouldCategory pdc = new DwMouldCategory();
            pdc.setId(pid);
            pdc.setIsLeaf(1);
            pdc.setUpdateTime(new Date());
            pdc.setUpdateUser(userCode);
            this.baseMapper.updateById(pdc);
        }
        log.info("删除了{}条分类信息",ik);
        return Result.ok("删除成功");
    }

    /**
     * 判断分类下是否有模型数据
     * @param id
     * @return
     */
    private int selectRecordsCount(Long id) {
        QueryWrapper<DwMould> mq = Wrappers.query();
        mq.select("id,name").eq("delete_model",1).eq("category_id",id);
        List<DwMould> mouldList = this.dwMouldMapper.selectList(mq);
        return CollectionUtils.isEmpty(mouldList)?0:mouldList.size();
    }


}
