package com.dnt.data.standard.server.model.standard.entity.excel;

import cn.hutool.core.annotation.Alias;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @description: 数据元--导出数据对象 <br>
 * @date: 2021/9/17 上午10:51 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDataElementExcel {
    @Alias("分类名称")
    private String categoryName;
    /**数据元名称**/
    @Alias("数据元名称")
    private String name;
    /**数据元标识**/
    @Alias("数据元标识")
    private String code;
    /**别名**/
    @Alias("别名")
    private String alias;
    /**描述**/
    @Alias("描述")
    private String description;
    /**创建人**/
    @Alias("创建人")
    private String createUser;
    /**创建时间**/
    @JSONField(format ="yyyy-MM-dd HH:mm:ss")
    @Alias("创建时间")
    private Date createTime;
}
