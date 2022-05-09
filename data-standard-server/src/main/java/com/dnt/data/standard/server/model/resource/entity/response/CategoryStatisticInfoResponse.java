package com.dnt.data.standard.server.model.resource.entity.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: 资产盘点数据资源分布--数据返回对象 <br>
 * @date: 2021/10/19 下午4:48 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class CategoryStatisticInfoResponse implements Serializable {
    /**数据源的数量**/
    private Integer sourceCount;
    /**库的数量**/
    private Integer dbCount;
    /**表的数量**/
    private Integer tableCount;
    /**二级目录数量**/
    private Float secondCount;
    /**三级目录数量**/
    private Float thirdCount;
    /**存储大小**/
    private Double storageSize;
    /**百分比**/
    private String percentage;
}
