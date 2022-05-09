package com.dnt.data.standard.server.model.resource.entity.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 分类层级链路--数据返回对象 <br>
 * @date: 2021/11/10 下午1:50 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class CategoryLinkResponse implements Serializable {

    private Long id;
    private String name;
    private Long parentId;
    /**分类层级的全路径**/
    private String path;
    /**是否为叶子节点  1 为是  0 为否**/
    private Integer isLeaf;
    /**层级 节点所在的层级**/
    private Integer level;
    List<CategoryLinkResponse> childs;

}
