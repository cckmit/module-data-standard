package com.dnt.data.standard.server;

import cn.hutool.extra.pinyin.PinyinUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.mortbay.util.ajax.JSON;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:  <br>
 * @date: 2021/9/1 下午2:33 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public class StrTest {

    @Test
    public void sTest(){
        String str ="jdbc:hive2://172.24.15.4:10000/test_0101";
        if(StringUtils.contains(str,":10000/")){
            String[] ss = str.split(":10000/");

            System.out.println(ss[1]);
        }
    }

    @Test
    public void pyTest(){
        String code = "中华人民共和国";

        System.out.println(PinyinUtil.getFirstLetter(code,""));

        String ss = "1,2,3,4";
        System.out.println(Arrays.asList(ss.split(",")).get(0)+","+Arrays.asList(ss.split(",")).get(1));
        System.out.println(Arrays.asList(ss.split(",")).get(0)+","+Arrays.asList(ss.split(",")).get(1)+","+Arrays.asList(ss.split(",")).get(2));



    }
    @Test
    public void numberFormat(){

        DecimalFormat df = new DecimalFormat("#.##%");//格式化小数
        System.out.println(df.format(2.0/3));
    }
    @Test
    public void httpClient(){


        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpPost post=new HttpPost("http://172.24.15.10:8798/managementAuth/sys/oauth/login");
            String json="{\"validateId\":\"0000a17d-974f-4dd4-b0de-e8d6b7013021\",\"validateCode\":\"1qaz\",\"password\":\"931bd0e1cc9baae10e9ff6ca7002e834\",\"username\":\"nazhen\"}";

            /*StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            post.setEntity(entity);
            */

            Map<Object,Object> m = new HashMap<>();
            m.put("validateId","0000a17d-974f-4dd4-b0de-e8d6b7013021");
            m.put("validateCode","1qaz");
            m.put("password","931bd0e1cc9baae10e9ff6ca7002e834");
            m.put("username","nazhen");

            StringEntity e = new StringEntity(JSON.toString(m));
            post.setEntity(e);

            CloseableHttpResponse response = httpclient.execute(post);

            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println(statusCode);
            //@2.得到实体内容
            HttpEntity en = response.getEntity();
            String content = EntityUtils.toString(en, "utf-8");
            System.out.println(content);
            //5.关闭连接
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
