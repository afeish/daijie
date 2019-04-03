package org.daijie.core.lock.zk;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.daijie.core.lock.Callback;
import org.daijie.core.lock.DistributedLockTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * zookeeper分布式锁执行实现类
 * 启用zookeeper分布式锁时，调用锁工具类执行
 * @author daijie_jay
 * @since 2017年11月24日
 */
public class ZkDistributedLockTemplate implements DistributedLockTemplate {
	
	private static final Logger logger = LoggerFactory.getLogger(ZkDistributedLockTemplate.class);

    private CuratorFramework client;

    public ZkDistributedLockTemplate(CuratorFramework client) {
        this.client = client;
    }

    private boolean tryLock(ZkReentrantLock distributedReentrantLock,Long timeout) throws Exception {
        return distributedReentrantLock.tryLock(timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public Object execute(String lockId, int timeout, Callback callback) {
        ZkReentrantLock distributedReentrantLock = null;
        boolean getLock=false;
        try {
            distributedReentrantLock = new ZkReentrantLock(client,lockId);
            if(tryLock(distributedReentrantLock,new Long(timeout))){
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
            if(getLock){
                distributedReentrantLock.unlock();
            }
        }
    }
}
