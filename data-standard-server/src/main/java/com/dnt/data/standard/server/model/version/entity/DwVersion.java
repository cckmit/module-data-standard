package com.dnt.data.standard.server.model.version.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @description: 版本管理--实体对象 <br>
 * @date: 2022/4/14 上午9:37 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dw_version")
public class DwVersion extends BaseEntity implements Serializable {
    /**
     * 版本ID
     */
    private Long id;
    /**
     * 版本名称
     */
    private String versionName;
    /**
     * 版本编号
     */
    private String versionCode;
    /**
     * 参考标准
     */
    private String referenceStandard;
    /**
     * 描述说明
     */
    private String description;

}
