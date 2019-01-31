package com.liulei.fly.javaApi.exclusiveLock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 21:46
 * @Description: 描述:
 */
public class LockWatcher implements Watcher {

    private CountDownLatch countDownLatch;

    public LockWatcher(CountDownLatch countDownLatch){
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getType() == Event.EventType.NodeDeleted){
            countDownLatch.countDown();
        }
    }
}
