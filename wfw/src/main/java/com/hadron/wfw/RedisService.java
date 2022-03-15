package com.hadron.wfw;

import java.util.List;


/**
 * 
 * @author xuychao
 * @date 2022年3月15日
 * @classname RedisService.java
 * @email xuychao@163.com  git@github.com:geek208/wfw.git
 */
public interface RedisService {

     boolean set(String key, String value) throws Exception;

     String get(String key) throws Exception;

     boolean expire(String key, long expire) throws Exception;

     <T> boolean setList(String key, List<T> list) throws Exception;

     <T> List<T> getList(String key, Class<T> clz) throws Exception;

     long lpush(String key, Object obj) throws Exception;

     long rpush(String key, Object obj) throws Exception;

     void hmset(String key, Object obj) throws Exception;

     <T> T hget(String key, Class<T> clz) throws Exception;


     void del(String key) throws Exception;

     <T> List<T>  hmGetAll(String key, Class<T> clz) throws Exception;

     String lpop(String key) throws Exception;
}
