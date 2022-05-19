package com.dnt.data.standard.server.model.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dnt.data.standard.server.model.entity.BaseEntity;

/**
 * @description: 基础Dao层业务封装 <br>
 * @date: 2021/7/30 下午1:53 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public interface BaseDao<T extends BaseEntity> extends BaseMapper<T> {
    /**
     * 判断字段是否存在
     * @param property
     * @param value
     * @param categoryId
     * @return
     */
    boolean isExist(String property, String value,Long categoryId);


    boolean isExistInProject(String property, String value, Long projectId);
}
