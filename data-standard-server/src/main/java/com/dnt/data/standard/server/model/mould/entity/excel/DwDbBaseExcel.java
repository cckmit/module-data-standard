package com.dnt.data.standard.server.model.mould.entity.excel;

import cn.hutool.core.annotation.Alias;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @description: 数据基础库--导出数据对象  <br>
 * @date: 2021/9/17 上午9:59 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDbBaseExcel {
    @Alias("分类名称")
    private String categoryName;
    /**基础库名称**/
    @Alias("基础库名称")
    private String name;
    /**基础库标识**/
    @Alias("基础库标识")
    private String code;
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
