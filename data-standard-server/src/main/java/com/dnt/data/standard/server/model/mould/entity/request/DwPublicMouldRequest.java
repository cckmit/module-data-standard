package com.dnt.data.standard.server.model.mould.entity.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.PageEntity;
import com.dnt.data.standard.server.model.mould.entity.DwPublicMouldField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 公共字段模型--入参对象 <br>
 * @date: 2021/7/29 下午4:10 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@TableName("dw_public_mould")
@ApiModel("公共字段模型请求对象")
public class DwPublicMouldRequest extends PageEntity {
    @ApiModelProperty("公共字段模型ID")
    private Long id;
    /**分类目录**/
    @ApiModelProperty("分类目录ID")
    private Long categoryId;
    /**名称**/
    @ApiModelProperty(value = "名称",required = true)
    private String name;
    /**描述信息**/
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("类型ID")
    private Long typeId;
    /**公共字段关联的字段集合**/
    List<DwPublicMouldField> fields;

}
