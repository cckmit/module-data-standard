package com.dnt.data.standard.server.model.mould.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 模型命名规则--入参对象 <br>
 * @date: 2021/8/5 下午2:09 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模型命名规则请求对象")
public class DwMouldNameRequest extends PageEntity {

    private static final long serialVersionUID = 4137055861137270434L;
    /**=========================第一页==================================**/
    @ApiModelProperty("模型命名规则ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("模型命名规则分类目录ID")
    private Long categoryId;
    /**名称**/
    @ApiModelProperty("模型命名规则名称")
    private String name;
    /**描述**/
    @ApiModelProperty("描述")
    private String description;
    /**=========================第二页==================================**/
    /**数据模型命名规范**/
    @ApiModelProperty("数据模型命名规范ID")
    private Long designTypeId;
    @ApiModelProperty("数据模型命名规范名称")
    private String designTypeName;
    @ApiModelProperty("选择目录ID")
    private Long catalogueId;
    @ApiModelProperty("选择目录名称")
    private String catalogueName;
    /**=========================第三页==================================**/
    @ApiModelProperty("模型命名下拉列表选择项")
    private List<Object> selectItem;
    @ApiModelProperty("模型命名下拉列表选择项的名称集合")
    private List<Object> selectItemName;
    /**生成预览(模型命名)**/
    private String mouldName;

}
