package com.dnt.data.standard.server;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.dnt.data.standard.server.model.mould.entity.DwMould;
import com.dnt.data.standard.server.model.resource.entity.DwMouldResource;
import com.dnt.data.standard.server.utils.BeanValueTrimUtil;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @description: LongTest <br>
 * @date: 2021/8/10 下午4:28 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Slf4j
public class LongTest {
    @Test
    public void longTest() {

        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        String ss = ids.stream().map(s -> s + "").collect(Collectors.joining(", "));


        List<String> t1 = Arrays.asList(ss.split(","));

        System.out.println("LongTest.longTest:" + ss);
    }

    @Test
    public void streamList(){

        List<DwMouldResource> rList = getResourceList();
        List<DwMouldResource> srL = rList.stream().filter(d->d.getStatus()==1000).collect(Collectors.toList());
        System.out.println("LongTest.streamList--> " + srL.size()) ;


        System.out.println(12/16.00);

    }
    @Test
    public void streamMapList(){

        List<Map<String,Object>> rList = getMapList();
        List<Map<String,Object>> srL = rList.stream().filter(d->Integer.parseInt(d.get("status")+"") > 0 && Long.parseLong(d.get("id")+"")==9999).collect(Collectors.toList());
        System.out.println("LongTest.streamMapList--> " + srL.size()) ;

        String os = "${java.version}";

        log.info("java version:{}",os);
    }


    @Test
    public void hutoolUrlTest(){
        Map<String,Object> m = new HashMap<>();
/**        String url="http://172.24.15.10:6011/managementAuth/sys/audit/config/list";**/
        String url="http://127.0.0.1:6001/sys/audit/config/list";

        JSONObject j = new JSONObject();
        j.put("gatewayPrefix","");
        String s = HttpRequest.post(url)
                .header("Content-Type","application/json;charset=UTF-8")
                .body(j.toJSONString())
                .timeout(20000)/**超时，毫秒**/
                .execute().body();



        System.out.println(s);

    }



    @Test
    public void auditLogTest(){
        String url="http://172.24.15.10:9798/managementAuth/sys/log/page";


        String s = HttpRequest.get(url)
                .header("token","f03e5a36-b679-4e73-bf8b-978d3f71a741")
                .form("operModel","数仓")
                .form("operUser","admin")
                .timeout(20000)/**超时，毫秒**/
                .execute().body();



        System.out.println(s);

    }


    @Test
    public void BeanTrimTest(){

        DwMould md = new DwMould();
        md.setId(1L);
        md.setTypeId("  nihao  ");
        md.setName("mouldName   ");
        md.setDescription("这是一个去掉   前后空格的工具 ");
        BeanValueTrimUtil.beanValueTrim(md);
        DwMould mm = new DwMould();
        BeanUtils.copyProperties(md,mm);
        System.out.println(mm.getName());


    }

    @Test
    public void  patternTest(){
        String re ="jdbc:hive2://172.24.15.4:10000/";
        String regex = "[0-9]/\\w+(?=\\w*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(re);
        while (matcher.find()){
            System.out.println(matcher.group());

            System.out.println(matcher.group().substring(2));
        }
    }

    @Test
    public  void mapTest(){
        List<Map<String,Object>> objList = getMapList();

        for(int ii=0;ii<objList.size();ii++){
            Map<String,Object> mt1 = objList.get(ii);
            Set<Map.Entry<String,Object>> msets = mt1.entrySet();
            for(Map.Entry entry:msets) {
                System.out.println(entry.getKey()+"--"+ entry.getValue());
            }


            System.out.println("=====================");

           /* Set<String> setKey = mt1.keySet();
            Iterator<String>it = setKey.iterator();
            while (it.hasNext()){
                String kk = it.next();
                System.out.println(kk+"--"+ mt1.get(kk));
            }*/

        }



    }


    private List<Map<String, Object>> getMapList() {

        List<Map<String, Object>> lm = new ArrayList<>();

        for(int i=0;i<100;i++){

            Map<String,Object> mt = new HashMap<>();
            mt.put("id",i);
            mt.put("status",i);
            mt.put("name","MapList" +i);

            lm.add(mt);
        }
        return lm;
    }


    private List<DwMouldResource> getResourceList() {

        List<DwMouldResource> r = new ArrayList<>();

        for(int i=0;i<100;i++) {
            DwMouldResource dr = new DwMouldResource();
            dr.setId(Long.parseLong((i+1)+""));
            dr.setName("resource" +(i+1));
            dr.setStatus(i);
            r.add(dr);
        }

        return r;
    }


    @Test
    public void tStream(){

        List<Map<String,Object>> l1 = new ArrayList<>();

        Map<String,Object> mm = new HashMap<>();
        mm.put("id",1);
        mm.put("code","DATE");
        mm.put("name","日期类型");
        l1.add(mm);
        mm = new HashMap<>();
        mm.put("id",2);
        mm.put("code","STRING");
        mm.put("name","字符类型");
        l1.add(mm);
        mm = new HashMap<>();
        mm.put("id",3);
        mm.put("code","INTEGER");
        mm.put("name","整数类型");
        l1.add(mm);

       List<Object> lt =  l1.stream().filter(f-> StringUtils.equals("字符类型",f.get("name")+"")).map(m->m.get("id")).collect(Collectors.toList());


        System.out.println(lt);
    }
    @Test
    public void isnullTest(){

        DwMould m =new DwMould();

        if(!Optional.ofNullable(m).isPresent()){
            System.out.println("空对象");
        }
        Optional.ofNullable(m).orElseThrow(() -> new RuntimeException("空一下了吧"));
        System.out.println("========执行空下面数据===========");
    }

    @Test
    public void mapUtilTest(){
        Map<String,Object> m1 = new HashMap<>();
        m1.put("A1","A1");
        m1.put("B1","B1");
        m1.put("C1","C1");
        m1.put("D1","D1");
        m1.put("E1","E1");
        m1.put("F1","F1");

        Map<String,Object> m2 = new HashMap<>();
        m2.put("A1","A1");
        m2.put("F1","F21");
        m2.put("A21","A21");
        m2.put("B21","B21");
        m2.put("C21","C21");

        u(m1,m2);
    }

    private static void u(Map<String,Object> map1, Map<String,Object> map2) {
        MapDifference<String, Object> difference = Maps.difference(map1, map2);
        /** 是否有差异，返回boolean**/
        boolean areEqual = difference.areEqual();
        System.out.println("比较两个Map是否有差异:" + areEqual);
        /** 两个map的交集**/
        Map<String, Object> entriesInCommon = difference.entriesInCommon();
        System.out.println("两个map都有的部分（交集）===：" + entriesInCommon);
        /** 键相同但是值不同值映射项。返回的Map的值类型为MapDifference.ValueDifference，以表示左右两个不同的值**/
        Map<String, MapDifference.ValueDifference<Object>> entriesDiffering = difference.entriesDiffering();
        System.out.println("键相同但是值不同值映射项===：" + entriesDiffering);
        /** 键只存在于左边Map的映射项**/
        Map<String, Object> onlyOnLeft = difference.entriesOnlyOnLeft();
        System.out.println("键只存在于左边Map的映射项:" + onlyOnLeft);
        /** 键只存在于右边Map的映射项**/
        Map<String, Object> entriesOnlyOnRight = difference.entriesOnlyOnRight();
        System.out.println("键只存在于右边Map的映射项:" + entriesOnlyOnRight);
    }
}
