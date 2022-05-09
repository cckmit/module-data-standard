package com.dnt.data.standard.server.model.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dnt.data.standard.server.model.standard.dao.DwCategoryMapper;
import com.dnt.data.standard.server.model.standard.entity.DwCategory;
import com.dnt.data.standard.server.model.dao.BaseDao;
import com.dnt.data.standard.server.model.entity.BaseEntity;
import com.dnt.data.standard.server.model.mould.entity.DwMouldCategory;
import com.dnt.data.standard.server.model.service.BaseService;
import com.google.common.base.Optional;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 基础业务层实现代码 <br>
 * @date: 2021/7/30 下午1:43 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public class BaseServiceImpl<M extends BaseDao<T>, T extends BaseEntity> extends ServiceImpl<M,T> implements BaseService<T> {
    @Autowired
    protected ApplicationContext applicationContext;
    @Autowired
    private DwCategoryMapper dwCategoryMapper;

    @Override
    public boolean isExist(String property, String value,Long categoryId) {
        return baseMapper.isExist(property,value,categoryId);
    }

    /**构建树型数据**/
    protected List<DwMouldCategory> buildTreeCategory(List<DwMouldCategory> list,Integer leaveDefault) {
        List<DwMouldCategory> dwCate = new ArrayList<>();
        // 把分类组成树结构
        list.forEach(parentCate->{
            Integer leaveValue = parentCate.getLevel();
            Long cId = parentCate.getId();
            if(leaveValue.intValue()==leaveDefault.intValue()) {
                dwCate.add(parentCate);
            }
            list.forEach(childCate->{
                Long childFid = childCate.getId();
                Long childPid = childCate.getParentId();
                if(!cId.equals(childFid)){
                    if(cId.equals(childPid)){
                        List<DwMouldCategory> childs = parentCate.getChilds();
                        if (childs==null) {
                            childs = new ArrayList<>();
                        }
                        childs.add(childCate);
                        parentCate.setChilds(childs);
                    }
                }
            });

        });
        return dwCate;
    }

    /**
     * hive的数据源信息
     * @param jdbcUrl
     * @param userName
     * @param password
     * @return
     */
    protected DataSource getDataSource(String jdbcUrl, String userName, String password) {

        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(jdbcUrl);
        datasource.setUsername(userName);
        datasource.setPassword(password);
        datasource.setDriverClassName("org.apache.hive.jdbc.HiveDriver");

        //配置统一属性
        datasource.setInitialSize(1);
        datasource.setMinIdle(3);
        datasource.setMaxActive(20);
        datasource.setMaxWait(60000);
        datasource.setTimeBetweenEvictionRunsMillis(60000);
        datasource.setMinEvictableIdleTimeMillis(30000);
        datasource.setValidationQuery("select 1");
        datasource.setTestWhileIdle(true);
        datasource.setTestOnBorrow(false);
        datasource.setTestOnReturn(false);
        datasource.setPoolPreparedStatements(true);
        datasource.setMaxPoolPreparedStatementPerConnectionSize(20);
        return datasource;
    }

    /**
     * 根据请求对象获取IP地址
     * @param request
     * @return
     */
    protected String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 如果是多级代理，那么取第一个ip为客户端ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }

        return ip;
    }
    /**根据分类ID 获取分类名称**/
    protected String getCategoryNameById(Long categoryId){
        log.debug("==============获取分类中文名称===============");
        String cname = "";
        //NO.1 判断分类ID是否为空
        if(!Optional.fromNullable(categoryId).isPresent()){
            return cname;
        }
        //NO.2 根据ID查询分类
        QueryWrapper<DwCategory> cwq = Wrappers.query();
        cwq.select("id","name").eq("delete_model",1).eq("id",categoryId);
        List<Map<String,Object>> mms = this.dwCategoryMapper.selectMaps(cwq);

        if(CollectionUtils.isEmpty(mms)){
            return cname;
        }
        //NO.3 获取名称
        Map<String,Object> mm = mms.get(0);
        //构建名称
        if(MapUtils.isNotEmpty(mm) &&  mm.containsKey("name")){
           cname = mm.get("name").toString();
        }
        return cname;
    }


}
