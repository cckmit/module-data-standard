package com.dnt.data.standard.server.model.mould.entity.response;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.io.Serializable;

/**
 * @description:  <br>
 * @date: 2021/10/9 下午2:38 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwDbBaseFieldResponse implements Serializable {

    private Long id;
    /**基础库的ID**/
    private Long dbBaseId;
    private String tableName;
    /**存储的动态数据信息**/
    private JSONArray contentData;
}
