package com.dnt.data.standard.server.model.version.entity.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 版本管理--返回数据对象 <br>
 * @date: 2022/4/14 上午10:31 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
public class DwVersionResponse {
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 版本编号
     */
    private String versionCode;
    /**
     * 参考标准
     */
    private String referenceStandard;
    /**
     * 描述说明
     */
    private String description;
    /**
     * 发布的数据
     */
    private Map<String, List<Map<String,Object>>> releaseData;

}
