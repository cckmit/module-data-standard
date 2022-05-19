package com.dnt.data.standard.server.model.controller;

import com.dnt.data.standard.server.model.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * @description: 基础的业务代码 <br>
 * @date: 2021/7/30 下午12:03 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class BaseController {

    /**
     * 根据传入的列名、值检验数据是否存在
     * @param property
     * @param oldValue
     * @param value
     * @param optInfo
     * @param baseService
     * @return
     */
    public boolean remoteCheck(String property, String oldValue, String value, Long categoryId,String optInfo, BaseService baseService) {
        boolean result = false;
        //参数转码

        try {
            value = URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(log.isInfoEnabled()){
            log.info("BaseController-->remoteCheck 根据列名验证名称是否存在,openInfo:{}, property:{},value:{}",optInfo,property,value);
        }
        try {
            if (StringUtils.isNotEmpty(oldValue) && StringUtils.equalsIgnoreCase(oldValue, value)) {
                return true;
            }
            result = !baseService.isExist(property, value,categoryId);
        } catch (Exception e) {
            log.error(optInfo + " 验证时，发生异常Exception", e);
        }

        return result;
    }
    /**
     * 根据传入的列名、值检验数据是否存在
     * @param property
     * @param oldValue
     * @param value
     * @param optInfo
     * @param baseService
     * @return
     */
    public boolean remoteCheckInProject(String property, String oldValue, String value, Long projectId,String optInfo, BaseService baseService) {
        boolean result = false;
        //参数转码

        try {
            value = URLDecoder.decode(value, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(log.isInfoEnabled()){
            log.info("BaseController-->remoteCheckInProject 根据列名验证名称是否存在,openInfo:{}, property:{},value:{}",optInfo,property,value);
        }
        try {
            if (StringUtils.isNotEmpty(oldValue) && StringUtils.equalsIgnoreCase(oldValue, value)) {
                return true;
            }
            result = !baseService.isExistInProject(property, value,projectId);
            log.info("根据列名验证名称是否存在:"+result);
        } catch (Exception e) {
            log.error(optInfo + " 验证时，发生异常Exception", e);
        }

        return result;
    }
    /**
     * 创建下载文件目录及文件
     * @param fil
     */
    protected void createFileMkdirs(File fil) {
        try {
            //获取父目录
            File fileParent = fil.getParentFile();
            if (!fil.getParentFile().exists()) {
                //创建目录
                fileParent.mkdirs();
            }
            //创建文件
            boolean flag = fil.createNewFile();
            log.debug("创建文件操作："+flag);
        } catch (IOException e) {
            log.error("BaseServiceImpl-->createFileMkdirs异常" + e.toString());;
        }
    }
}
