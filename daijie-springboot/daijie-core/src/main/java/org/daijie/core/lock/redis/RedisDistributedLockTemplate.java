package org.daijie.core.lock.redis;

import java.util.concurrent.TimeUnit;

import org.daijie.core.lock.Callback;
import org.daijie.core.lock.DistributedLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * redis分布式锁执行实现类
 * 启用redis分布式锁时，调用锁工具类执行
 * @author daijie_jay
 * @since 2017年11月24日
 */
public class RedisDistributedLockTemplate implements DistributedLockTemplate {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisDistributedLockTemplate.class);

    private Object jedisLock;

    public RedisDistributedLockTemplate(Object jedisLock) {
        this.jedisLock = jedisLock;
    }

    public Object execute(String lockId, int timeout, Callback callback) {
        RedisReentrantLock distributedReentrantLock = null;
        boolean getLock=false;
        try {
            distributedReentrantLock = new RedisReentrantLock(jedisLock,lockId);
            if(distributedReentrantLock.tryLock(new Long(timeout), TimeUnit.MILLISECONDS)){
                getLock=true;
                return callback.onGetLock();
            }else{
                return callback.onTimeout();
            }
        }catch (Exception e) {
        	if(e instanceof InterruptedException){
        		Thread.currentThread().interrupt();
        	}
        	logger.debug(e.getMessage(), e);
            return callback.onError(e);
        }finally {
            if(getLock) {
                distributedReentrantLock.unlock();
            }
        }
    }
}
