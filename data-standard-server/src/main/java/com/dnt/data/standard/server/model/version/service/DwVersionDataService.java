package com.dnt.data.standard.server.model.version.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.model.version.entity.request.DwVersionHistoryLogRequest;
import com.dnt.data.standard.server.model.version.entity.request.VersionReleaseSelectDataRequest;
import com.dnt.data.standard.server.model.version.entity.response.DwVersionDataResponse;
import com.dnt.data.standard.server.model.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @description: 发布版本数据记录--服务接口层 <br>
 * @date: 2022/4/20 下午2:26 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface DwVersionDataService extends BaseService<DwVersionData> {
    /**
     * 记录版本对应的记录日志数据【异步记录日志】
     * @param projectId
     * @param vId
     * @param vCode
     * @param vName
     * @param releaseData
     */
    void writeVersionReleaseLog(Long projectId,Long vId,String vCode,String vName,Map<String, List<VersionReleaseSelectDataRequest>> releaseData);

    /**
     * 获取发布历史版本日志分页列表
     * @param request
     * @return
     */
    IPage<DwVersionData> selectVersionHistoryLogPage(DwVersionHistoryLogRequest request);

    /**
     * 删除发布历史版本日志
     * @param id
     * @return
     */
    R deleteVersionHistoryLog(Long id);

    /**
     * 查看历史版本日志详情
     * @param id
     * @return
     */
    DwVersionDataResponse detailVersionHistoryLog(Long id);
}
