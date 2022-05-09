package com.dnt.data.standard.server.model.mould.entity.response;

import lombok.Data;

import java.util.List;

/**
 * @description: 数据元分类目录--返回数据对象  <br>
 * @date: 2021/9/22 上午10:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDataElementTreeResponse {
    private Long id;
    /**分类名称**/
    private String name;
    private Long parentId;
    /**分类层级的全路径**/
    private String path;
    /**是否为叶子节点  1 为是  0 为否**/
    private Integer isLeaf;
    /**层级 节点所在的层级**/
    private Integer level;
    private List<DwDataElementTreeResponse> childs;
}
