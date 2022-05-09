package com.dnt.data.standard.server.model.version.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dnt.data.standard.server.model.service.impl.BaseServiceImpl;
import com.dnt.data.standard.server.model.version.dao.DwVersionDataMapper;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionHistoryLogRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseSelectDataRequest;
import com.dnt.data.standard.server.model.version.entity.response.DwVersionDataResponse;
import com.dnt.data.standard.server.model.version.entity.response.VersionDataResponse;
import com.dnt.data.standard.server.model.version.service.DwVersionDataService;
import com.dnt.data.standard.server.web.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description: 发布版本数据记录--服务接口层实现 <br>
 * @date: 2022/4/20 下午2:27 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
@Service
public class DwVersionDataServiceImpl extends BaseServiceImpl<DwVersionDataMapper, DwVersionData> implements DwVersionDataService {

    /**
     * 记录版本对应的记录日志数据【异步记录日志】
     * @param projectId
     * @param vId
     * @param vCode
     * @param vName
     * @param releaseData
     */
    @Async
    @Override
    public void writeVersionReleaseLog(Long projectId,Long vId,String vCode,
                                       String vName,Map<String, List<VersionReleaseSelectDataRequest>> releaseData) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionDataServiceImpl-->writeVersionReleaseLog 记录版本对应的记录日志数据【异步记录日志】");
        }

        for(String key:releaseData.keySet()) {
            String tableName = "dw_" + key;
            List<VersionReleaseSelectDataRequest> selectDataList = releaseData.get(key);
            //选择的数据ID
            List<Long> selectDataIdList = new ArrayList<>();
            selectDataList.forEach(vs->{
                selectDataIdList.addAll(vs.getId());
            });


            VersionCategoryDataList vcDataList = applicationContext.getBean(key,VersionCategoryDataList.class);
            //项目下的数据
            List<VersionDataResponse> projectData = vcDataList.selectDataByProjectId(projectId,selectDataIdList);
            for (VersionDataResponse p : projectData) {
                DwVersionData vd = new DwVersionData();
                BeanUtils.copyProperties(p,vd);
                vd.setTableName(tableName);
                vd.setVersionId(vId);
                vd.setVersionCode(vCode);
                vd.setVersionName(vName);
                vd.setProjectId(projectId);
                vd.setOperationFlag("release");
                vd.setOperationInfo("发布数据成功");

                baseMapper.insert(vd);
                log.info("==============成功插入了一条发布的历史数据日志==============");
            }
        }

    }

    /**
     * 获取发布历史版本日志分页列表
     * @param request
     * @return
     */
    @Override
    public IPage<DwVersionData> selectVersionHistoryLogPage(DwVersionHistoryLogRequest request) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionDataServiceImpl-->selectVersionHistoryLogPage 获取发布历史版本日志分页列表");
        }

        //页数
        Integer pn = request.getPageNum();
        //每页显示的记录数
        Integer ps = request.getPageSize();
        Page<DwVersionData> page = new Page<>(pn,ps);

        QueryWrapper<DwVersionData> vdq = Wrappers.query();
        vdq.eq("table_name","dw_"+request.getMouldType())
                .eq(StringUtils.isNotBlank(request.getOperationFlag()),"operation_flag",request.getOperationFlag())
                .eq(Optional.ofNullable(request.getDataId()).isPresent(),"data_id",request.getDataId())
                .orderByDesc("id");

        return this.baseMapper.selectPage(page,vdq);
    }

    /**
     * 删除发布历史版本日志
     * @param id
     * @return
     */
    @Override
    public R deleteVersionHistoryLog(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionDataServiceImpl-->deleteVersionHistoryLog 删除发布历史版本日志");
        }
        this.baseMapper.deleteById(id);
        log.info("删除历史版本发布数据操作成功");
        return Result.ok("删除数据成功");
    }

    /**
     * 查看历史版本日志详情
     * @param id
     * @return
     */
    @Override
    public DwVersionDataResponse detailVersionHistoryLog(Long id) {
        if(log.isInfoEnabled()) {
            log.info("DwVersionDataServiceImpl-->detailVersionHistoryLog 查看历史版本日志详情");
        }
        DwVersionData vd = baseMapper.selectById(id);
        DwVersionDataResponse dr = DwVersionDataResponse.builder()
                .id(vd.getId())
                .versionId(vd.getVersionId())
                .versionCode(vd.getVersionCode())
                .versionName(vd.getVersionName())
                .tableName(vd.getTableName())
                .dataId(vd.getDataId())
                .dataCategoryId(vd.getDataCategoryId())
                .dataCode(vd.getDataCode())
                .dataAlias(vd.getDataAlias())
                .dataName(vd.getDataName())
                .dataDescription(vd.getDataDescription())
                .dataReleaseStatus(vd.getDataReleaseStatus())
                .dataCreateUser(vd.getDataCreateUser())
                .dataCreateTime(vd.getDataCreateTime())
                .dataUpdateUser(vd.getDataUpdateUser())
                .dataUpdateTime(vd.getDataUpdateTime())
                .dataJson(vd.getDataJson())
                .operationFlag(vd.getOperationFlag())
                .operationInfo(vd.getOperationInfo())
                .dataField1(StringUtils.isBlank(vd.getDataField1())?new JSONArray():JSONArray.parseArray(vd.getDataField1()))
                .dataField2(StringUtils.isBlank(vd.getDataField2())?new JSONArray():JSONArray.parseArray(vd.getDataField2()))
                .dataField3(StringUtils.isBlank(vd.getDataField3())?new JSONArray():JSONArray.parseArray(vd.getDataField3()))
                .build();

        return dr;
    }
}
