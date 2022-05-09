package com.dnt.data.standard.server;

import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Map;
import java.util.Set;

@SpringBootTest
class StandardServerApplicationTests {

    @Autowired
    private CacheService cacheService;

    @Test
    void contextLoads() {

        cacheService.lock("redis_lock","saveCategory",1,300);
        System.out.println("加锁成功");
    }


    @Test
    void zSetValue(){

        Set<String> keys = cacheService.scan("zset-history:wh-map-search","*");
        System.out.println(keys.size());


        for (String key : keys) {
            String p = key.substring(0,key.lastIndexOf(":"));
            String k = StringUtils.substring(key,key.lastIndexOf(":")+1);
            // 执行定时业务逻辑
            Set<ZSetOperations.TypedTuple<Object>> ztos = cacheService.zReverseRangeWithScores(p,k,0,-1);
            ztos.forEach(f->{

                Double sscore = f.getScore();
                Map<String,String> mvalue = (Map<String,String>)f.getValue();

                String dbName = mvalue.get("dbName");
                String dbDesc = mvalue.get("dbDesc");
                String tableName = mvalue.get("tableName");
                String tableDesc = mvalue.get("tableDesc");
                String inputContent = mvalue.get("inputContent");
                String userCode = mvalue.get("userCode");

                System.out.println("================"+sscore);
            });
        }
    }



}
