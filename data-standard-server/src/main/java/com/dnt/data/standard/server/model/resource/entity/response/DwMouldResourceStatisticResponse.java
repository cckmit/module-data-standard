package com.dnt.data.standard.server.model.resource.entity.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:资源分布--返回数据对象 <br>
 * @date: 2021/10/15 下午2:03 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Data
public class DwMouldResourceStatisticResponse {
    /**id**/
    private Long id;
    /**名称**/
    private String name;
    /**数量**/
    private BigDecimal value;
    /**百分比**/
    private String percentage;
}
