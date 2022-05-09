package com.dnt.data.standard.server.model.resource.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 资产盘点已接入数据源数据预览--数据返回对象 <br>
 * @date: 2021/10/19 上午11:02 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DataPreviewResponse implements Serializable {
    private Long dataSourceTypeId;
    private String dataSourceTypeName;
    /**昨日增加发布的数量**/
    private Long yesterdayCount;
    /**数据源的数量**/
    private Integer dataSourceCount;
    /**数据库的数量**/
    private Integer dbCount;
    /**表的数量**/
    private Integer tableCount;
    /**存储大小**/
    private Double storageSize;
}
