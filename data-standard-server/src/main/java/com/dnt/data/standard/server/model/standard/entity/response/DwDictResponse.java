package com.dnt.data.standard.server.model.standard.entity.response;

import com.dnt.data.standard.server.model.standard.entity.DwDictField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据字典--返回对象 <br>
 * @date: 2021/7/22 下午5:40 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDictResponse implements Serializable {
    private Long id;
    /**分类目录**/
    private Long categoryId;
    private String categoryName;
    /**集群名称**/
    private String name;
    /**编号**/
    private String code;
    /**别名**/
    private String alias;
    /**描述**/
    private String description="";

    /**关联字段**/
    private List<DwDictField> fields;
    /**关联的元数据**/
    private List<Map<String,Object>> dataElementList;
}
