package com.dnt.data.standard.server.model.version.entity.response;

import cn.hutool.core.annotation.Alias;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description: 版本发布选择数据信息--返回数据对象 <br>
 * @date: 2022/4/20 下午3:41 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VersionDataResponse {
    /**
     * 数据ID
     */
    private Long dataId;
    /**
     * 数据分类ID
     */
    private Long dataCategoryId;
    /**
     * 数据名称
     */
    private String dataName;
    /**
     * 数据编号
     */
    private String dataCode;
    /**
     * 数据别名
     */
    private String dataAlias;
    /**
     * 数据描述说明
     */
    private String dataDescription;
    /**
     * 数据发布状态 0 未发布1已发布2已更新
     */
    private Integer dataReleaseStatus;

    /**
     * 数据创建人
     */
    @Alias("创建人")
    private String dataCreateUser;
    /**
     * 数据修改人
     */
    @Alias("更新人")
    private String dataUpdateUser;
    /**
     * 数据创建时间
     */
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    @Alias("创建时间")
    private Date dataCreateTime;
    /**
     * 数据修改时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @Alias("更新时间")
    private Date dataUpdateTime;
    /**
     * 当前ID的json数据
     */
    private String dataJson;
    /**
     * 关联数据1
     */
    private String dataField1;
    /**
     * 关联数据2
     */
    private String dataField2;
    /**
     * 关联数据3
     */
    private String dataField3;

}
