package com.dnt.data.standard.server.model.resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @description: T1 <br>
 * @date: 2021/11/12 下午3:04 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public class ResourceContents {
    /**节点**/
    public static JSON node(String id, String label, String comboId,int levelId, int nodeKey, int comboKey,
                            int comboNodeTotal, int levelComboTotal, int levelRowTotal){
        JSONObject nj = new JSONObject();
        nj.put("id",id);
        nj.put("label",label);
        nj.put("comboId",comboId);
        nj.put("level_id",levelId);
        nj.put("node_key",nodeKey);
        nj.put("combo_key",comboKey);
        nj.put("combo_node_total",comboNodeTotal);
        nj.put("level_combo_total",levelComboTotal);
        nj.put("level_row_total",levelRowTotal);
        return nj;
    }

    /**线**/
    public static JSON edge(String target,String source){
        JSONObject edj = new JSONObject();
        edj.put("target",target);
        edj.put("source",source);
        return edj;
    }

    /**框**/
    public static JSON combo (String id ,String label){
        JSONObject com = new JSONObject();
        com.put("id",id);
        com.put("label",label);
        com.put("is_level_combo",true);
        return com;
    }
    public static JSON comboParent (String id ,String label,String p){
        JSONObject com = new JSONObject();
        com.put("id",id);
        com.put("label",label);
        com.put("parentId",p);
        return com;
    }
}
