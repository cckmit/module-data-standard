package com.dnt.data.standard.server.model.standard.entity.request;

import com.dnt.data.standard.server.model.entity.PageEntity;
import com.dnt.data.standard.server.model.standard.entity.DwDictField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 数据字典--入参数据实体 <br>
 * @date: 2021/7/12 下午1:34 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("数据字典请求对象")
public class DwDictRequest extends PageEntity {

    @ApiModelProperty("数据字典ID")
    private Long id;
    /**分类目录ID**/
    @ApiModelProperty(value = "分类目录ID",required = true)
    private Long categoryId;
    /**名称**/
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    /**编号**/
    @ApiModelProperty(value = "数据字典编码",required = true)
    private String code;
    /**别名**/
    @ApiModelProperty("别名")
    private String alias;
    /**描述**/
    @ApiModelProperty("描述信息")
    private String description;

    @ApiModelProperty("数据字典关联对象字段")
    private List<DwDictField> dFieldList;
}
