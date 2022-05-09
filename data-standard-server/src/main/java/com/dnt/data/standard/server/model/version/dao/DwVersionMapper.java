package com.dnt.data.standard.server.model.version.dao;

import com.dnt.data.standard.server.model.version.entity.DwVersion;
import com.dnt.data.standard.server.model.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @description: 版本管理-数据库实现层 <br>
 * @date: 2022/4/14 上午9:49 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Mapper
public interface DwVersionMapper extends BaseDao<DwVersion> {
    /**
     * 同时更新多个表中的发布字段 状态
     * @param rStatus
     * @param tableName
     * @param uIdList
     * @return
     */
    @Update("<script>" +
            "update ${tableName} set  release_status=#{rStatus} where delete_model=1 " +
            " <if test=\"list != null and list.size() != 0\"> " +
            "    and id in  " +
            "      <foreach collection=\"list\" item=\"item\" open=\"(\" close=\")\" index=\"i\" separator=\",\">" +
            "          #{item}" +
            "      </foreach>" +
            " </if>" +
            "</script>")
    boolean update5TableReleaseStatus(@Param("rStatus") int rStatus,
                                      @Param("tableName") String tableName,
                                      @Param("list") List<Long> uIdList);
}
