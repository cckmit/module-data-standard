package com.dnt.data.standard.server.model.resource.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 资产盘点数据地图分布--数据返回对象 <br>
 * @date: 2021/10/19 下午4:18 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */

@Data
public class DataDistributionResponse implements Serializable {
    /**负责人**/
    private String ownerName;
    /**表的数量**/
    private Float tableCount;
    /**百分比**/
    private String percentage;
}
