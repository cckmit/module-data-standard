package com.dnt.data.standard.server.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @description: Bean 字符串类型的属性去掉前后空格 <br>
 * @date: 2022/1/18 下午2:11 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
public class BeanValueTrimUtil {

    /**
     * 利用反射通过get方法获取bean中字段fieldName的值
     * @param bean
     * @param fieldName
     * @return
     * @throws Exception
     */
    private static Object getFieldValue(Object bean, String fieldName)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("get")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();

        Object rObject = null;
        Method method = null;

        @SuppressWarnings("rawtypes")
        Class[] classArr = new Class[0];
        method = bean.getClass().getMethod(methodName, classArr);
        rObject = method.invoke(bean, new Object[0]);

        return rObject;
    }

    /**
     * 利用反射调用bean.set方法将value设置到字段
     * @param bean
     * @param fieldName
     * @param value
     * @throws Exception
     */
    private static void setFieldValue(Object bean, String fieldName, Object value)
            throws Exception {
        StringBuffer result = new StringBuffer();
        String methodName = result.append("set")
                .append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();

        /**
         * 利用发射调用bean.set方法将value设置到字段
         */
        Class[] classArr = new Class[1];
        classArr[0]="java.lang.String".getClass();
        Method method=bean.getClass().getMethod(methodName,classArr);
        method.invoke(bean,value);
    }

    /**
     * 去掉bean中所有属性为字符串的前后空格
     * @param bean
     */
    public static void beanValueTrim(Object bean){
        if(bean!=null){

            try {
                //获取所有的字段包括public,private,protected,private
                Field[] fields = bean.getClass().getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field f = fields[i];
                    if (f.getType().getName().equals("java.lang.String")) {
                        //获取字段名
                        String key = f.getName();
                        Object value = getFieldValue(bean, key);

                        if (value == null)
                            continue;

                        setFieldValue(bean, key, value.toString().trim());
                    }
                }
            } catch (Exception e) {
                System.out.println("============BeanValueTrimUtil-->beanValueTrim 去掉空格后给属性设置异常===========");
                e.printStackTrace();
            }
        }
    }

}
