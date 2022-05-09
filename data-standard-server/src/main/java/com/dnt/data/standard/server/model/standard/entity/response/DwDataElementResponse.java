package com.dnt.data.standard.server.model.standard.entity.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @description: 数据元--返回数据对象 <br>
 * @date: 2021/7/27 下午3:25 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDataElementResponse {

    /**====================基础信息=========================**/
    private Long id;
    /**分类目录**/
    private Long categoryId;
    private String categoryName;
    /**数据元名称**/
    private String name;
    /**标识编码**/
    private String code;
    /**数据元别名**/
    private String alias;
    /**描述**/
    private String description="";
    /**======================域值设置============================**/
    /**数据类型**/
    private Integer typeId=0;
    private String typeName="";
    /**长度**/
    private Integer length=0;
    /**业务规则**/
    private String businessRules="";
    /**引用数据字典**/
    private Long dictId=0L;
    private String dictName="";
    /**自定义质量规则**/
    private String customerRules="";

    /**数据元关联的模型信息**/
    List<Map<String,Object>> mouldList;

}
