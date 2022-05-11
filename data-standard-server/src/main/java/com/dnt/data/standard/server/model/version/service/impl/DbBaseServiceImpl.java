package com.dnt.data.standard.server.model.version.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.mould.dao.DwDbBaseMapper;
import com.dnt.data.standard.server.model.mould.entity.DwDbBase;
import com.dnt.data.standard.server.model.mould.entity.DwDbBaseField;
import com.dnt.data.standard.server.model.mould.entity.response.DwDbBaseFieldResponse;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.version.entity.request.CategoryPageListRequest;
import com.dnt.data.standard.server.model.version.entity.response.VersionDataResponse;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:  数据基础库列表查询--服务接口 <br>
 * @date: 2022/4/18 下午3:52 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@Service("db_base")
public class DbBaseServiceImpl extends BaseServiceImpl<DwDbBaseMapper,DwDbBase> implements VersionCategoryDataList {
    @Resource
    private DwDbBaseMapper dwDbBaseMapper;

    /**
     * 数据基础库列表查询列表
     * @param page
     * @param wq
     * @return
     */
    @Override
    public IPage<Map<String, Object>> selectCategoryDataPageList(Page<CategoryPageListRequest> page, QueryWrapper<CategoryPageListRequest> wq) {
        if(log.isInfoEnabled()) {
            log.info("DbBaseServiceImpl-->selectCategoryDataPageList 数据基础库列表查询列表");
        }

        IPage<Map<String,Object>> pList = this.dwDbBaseMapper.selectCategoryDataPageList(page,wq);

        return pList;
    }
    /**
     * 项目下的数据 满足发布数数据返回的字段名
     * @param projectId
     * @param selectDataIdList
     * @return
     */
    @Override
    public List<VersionDataResponse> selectDataByProjectId(Long projectId,List<Long> selectDataIdList) {
        if(log.isInfoEnabled()) {
            log.info("DbBaseServiceImpl.selectDataByProjectId 项目下的数据 满足发布数数据返回的字段名");
        }
        List<VersionDataResponse> vdrList = new ArrayList<>();

        if(CollectionUtils.isEmpty(selectDataIdList)){
            return vdrList;
        }

        //NO.1 项目下的数据 满足发布数数据返回的字段名
        List<DwDbBase> mList = this.dwDbBaseMapper.selectDataByProjectId(projectId);
        if(CollectionUtils.isEmpty(mList)){
            return vdrList;
        }
        //NO.2 根据选择的数据ID 构建返回的数据对象
        for (Long selectId : selectDataIdList) {
            //NO.3 查询选择数据元关联字段
            List<DwDbBaseField> lst = this.dwDbBaseMapper.selectDwDbBaseFieldByDbId(selectId);

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


            for(DwDbBase de :mList){

                Long id = de.getId();
                if(selectId.longValue() == id.longValue()){
                    Map<String,Object> rs = new HashMap<>();
                    String headStr = de.getContentHeader();
                    if(StringUtils.isNotEmpty(headStr)){
                        rs.put("header",JSON.parseArray(headStr));
                    }
                    rs.put("data",listRes);

                    List<Map<String,Object>> jaList = new ArrayList<>();
                    jaList.add(rs);
                    Long cid = de.getCategoryId();
                    String cName =getCategoryNameById(cid);

                    de.setCategoryName(cName);
                    VersionDataResponse vdr = VersionDataResponse.builder()
                            .dataId(id)
                            .dataCategoryId(de.getCategoryId())
                            .dataName(de.getName())
                            .dataCode(de.getCode())
                            .dataDescription(de.getDescription())
                            .dataReleaseStatus(de.getReleaseStatus())
                            .dataCreateUser(de.getCreateUser())
                            .dataCreateTime(de.getCreateTime())
                            .dataUpdateUser(de.getUpdateUser())
                            .dataUpdateTime(de.getUpdateTime())
                            .dataJson(JSON.toJSONString(de))
                            .dataField1(JSON.toJSONString(jaList))
                            .build();
                    vdrList.add(vdr);
                }
            }
        }
        //返回选择数据的信息 向操作日志表中插入数据
        return vdrList;
    }
}
