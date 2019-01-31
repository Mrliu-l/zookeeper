package com.liulei.fly.javaApi.exclusiveLock;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 21:17
 * @Description: 描述: java Api客户端
 */
public class ZookeeperClient {

    //服务器地址
    private static final String CONNECTSERVER = "192.168.1.121:2181,192.168.1.122:2181,192.168.1.123:2181,192.168.1.124:2181";

    private static ZooKeeper client;

    //session超时时间
    public static int sessionTimeOut = 5000;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getClient() throws IOException, InterruptedException {
        client = new ZooKeeper(CONNECTSERVER, sessionTimeOut, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if(watchedEvent.getState() == Event.KeeperState.SyncConnected){
                    countDownLatch.countDown();
                }
            }
        });
        countDownLatch.await();
        return client;
    }

}
