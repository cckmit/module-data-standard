package com.dnt.data.standard.server.model.mould.entity.response;

import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;

import java.util.Map;

/**
 * @description: 数据基础库--返回对象 <br>
 * @date: 2021/8/4 下午5:12 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDbBaseResponse extends BaseEntity {
    private Long id;
    /**分类目录**/
    private Long categoryId;
    private String categoryName;
    /**基础库名称**/
    private String name;
    /**基础库标识**/
    private String code;
    /**描述**/
    private String description;
    /**表头信息**/
    private String contentHeader;
    /**返回**/
    private Map<String,Object> lists;
}
