package com.dnt.data.standard.server.model.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dnt.data.standard.server.model.entity.BaseEntity;

/**
 * @description: 基础业务层代码 <br>
 * @date: 2021/7/30 下午1:39 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface BaseService<T extends BaseEntity> extends IService<T> {
    /**
     * 根据属性名与属性值检查数据是否存在
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    boolean isExist(String property, String value,Long categoryId);
}
