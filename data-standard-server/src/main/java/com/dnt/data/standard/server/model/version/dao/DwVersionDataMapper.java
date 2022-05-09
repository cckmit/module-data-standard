package com.dnt.data.standard.server.model.version.dao;

import com.dnt.data.standard.server.model.version.entity.DwVersionData;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 发布版本数据记录--数据库实现层 <br>
 * @date: 2022/4/20 下午2:11 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwVersionDataMapper extends BaseDao<DwVersionData> {
}
