package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @program: mmall
 * @description: redis连接池
 * @author: BoWei
 * @create: 2018-08-01 11:06
 **/
public class RedisPool {
    /* jedis连接池 */
    private static JedisPool pool;

    /* 最大连接数 */
    private static Integer maxTotal =Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    /* 在jedispool中最大的idle状态(空闲)的jedis实例个数 */
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
    /* 在jedispool中最小的idle状态(空闲)的jedis实例个数 */
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));

    /* 在 borrow 一个jedis实例的时候，是否进行验证操作，如果赋值True。则得到的jedis实例肯定是可以用的。 */
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    /* 在 borrow 一个jedis实例的时候，是否进行验证操作，如果赋值True。则得到的jedis实例肯定是可以用的。 */
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));

    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));


    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        /* 连接耗尽的时候是否阻塞，false会抛出异常，true阻塞直到超时。默认是true */
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }
    static{
        initPool();
    }
    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

}
