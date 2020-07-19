package com.aikosolar.bigdata.ct;

/**
 * Created by jian.wang on 2019/8/12.
 */
public class JCache {

  /*  private static final Logger logger = LoggerFactory.getLogger(JCache.class);

    private static final Integer KEY_TTL = 24*60*60;

    public static synchronized JedisResourcePool buildJedisPool(String redisUrl){
        javax.cache.spi.CachingProvider provider = javax.cache.Caching.getCachingProvider();
        javax.cache.CacheManager cacheManager = provider.getCacheManager(URI.create(redisUrl), provider.getDefaultClassLoader());

        // 过期策略定义
        javax.cache.expiry.ExpiryPolicy policy = new javax.cache.expiry.ExpiryPolicy() {

            @Override
            public Duration getExpiryForCreation() {
                return Duration.ONE_DAY;
            }

            @Override
            public Duration getExpiryForAccess() {
                return null;
            }

            @Override
            public Duration getExpiryForUpdate() {
                return null;
            }
        };

        // Key应该是String或者byte[]，如果是其他的类型，则调用hashCode作为Key（即不可反序列化），迭代接口不再支持。
        // Value应尽量是String或者byte[]，如果是其他的类型，则使用Java的Object序列化与反序列化。
        javax.cache.configuration.MutableConfiguration<String, byte[]> configuration = new javax.cache.configuration.MutableConfiguration<>();
        configuration.setTypes(String.class, byte[].class)
                .setManagementEnabled(true)
                .setStatisticsEnabled(true)
                // Factory的create方法在运行过程中可能会被调用多次，因此可以借助FactoryBuilder.SingletonFactory对instance进行包装成单例工厂。
                .setExpiryPolicyFactory((javax.cache.configuration.Factory<javax.cache.expiry.ExpiryPolicy>) () -> policy);

        // 见JCache-API( https://static.javadoc.io/javax.cache/cache-api/1.1.0/javax/cache/Cache.html )
        javax.cache.Cache<String, byte[]> cache = cacheManager.getCache("nevermore2", String.class, byte[].class);
        if (null == cache) {
            javax.cache.Cache<String, byte[]> cache1 = cacheManager.createCache("nevermore2", configuration);
            //获取底层Jedis Pool，自行管理Jedis生命周期，自行选择处理key的前缀。    注：JCache会管理JedisResourcePool的生命周期。
            JedisResourcePool pool = cache1.unwrap(JedisResourcePool.class);
            return pool;
        }
        //获取底层Jedis Pool，自行管理Jedis生命周期，自行选择处理key的前缀。    注：JCache会管理JedisResourcePool的生命周期。
        JedisResourcePool pool = cache.unwrap(JedisResourcePool.class);
        return pool;
    }

    public static synchronized void closeJedisPool(JedisResourcePool pool){
        try {
            if (null != pool && !pool.isClosed()){
                pool.close();
            }
        } catch (IOException e) {
            logger.error("close redis poll exception", e);
        }
    }

    *//**
     * 有重试的sadd 重试2次
     * @param pool
     * @param key
     * @param value
     * @param retry
     * @return
     *//*
    public static Long sadd(JedisResourcePool pool, String key, String value, int retry){
        Jedis jedis = null;
        if (!pool.isClosed()) {
            try {
                jedis = pool.getResource();
                retry++;
                Long result = jedis.sadd(key, value);
                jedis.expire(key, KEY_TTL);
                return result;
            } catch (Exception e) {
                logger.error("sadd exception", e);
                try {
                    Thread.sleep(1000l);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                //自旋，最多重试2次
                if (retry < 3){
                    return sadd(pool, key, value, retry);
                }
            } finally {
                if (null != jedis && jedis.isConnected()){
                    jedis.close();
                }
            }
        }
        return 0l;
    }

    public static void del(JedisResourcePool pool, String[] days){

        if (!pool.isClosed()) {
            Jedis jedis = null;

            try {
                jedis = pool.getResource();
                for(int i=0; i<days.length; i++){
                    // 游标初始值为0
                    String cursor = ScanParams.SCAN_POINTER_START;
                    String key = "*_" + days[i];
                    ScanParams scanParams = new ScanParams();
                    scanParams.match(key);// 匹配以 *_时间戳 为后缀的 key
                    scanParams.count(1000);
                    while (true){
                        //使用scan命令获取1000条数据，使用cursor游标记录位置，下次循环使用
                        ScanResult<String> scanResult = jedis.scan(cursor, scanParams);
                        cursor = scanResult.getStringCursor();// 返回0 说明遍历完成
                        if ("0".equals(cursor)){
                            break;
                        }
                        List<String> list = scanResult.getResult();
                        long t1 = System.currentTimeMillis();
                        for(int m = 0;m < list.size();m++){
                            String mapentry = list.get(m);
                            jedis.del(key, mapentry);
                        }
                        long t2 = System.currentTimeMillis();
                        logger.info("删除android" + list.size()
                                + "条数据，耗时: " + (t2-t1) + "毫秒,cursor:" + cursor);
                    }
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }

    }*/
}
