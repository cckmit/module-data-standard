package com.dnt.data.standard.server.model.mould.dao;

import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 模型层级--数据库实现层 <br>
 * @date: 2021/8/2 下午6:02 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwMouldCategoryMapper extends BaseDao<DwMouldCategory> {
}
