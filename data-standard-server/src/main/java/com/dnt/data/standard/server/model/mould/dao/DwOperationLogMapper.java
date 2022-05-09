package com.dnt.data.standard.server.model.mould.dao;

import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.mould.entity.DwOperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description: 业务操作日志--数据库实现层 <br>
 * @date: 2021/8/12 下午1:18 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwOperationLogMapper extends BaseDao<DwOperationLog> {
}
