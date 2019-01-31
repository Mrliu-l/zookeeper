package com.liulei.fly.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 9:45
 * @Description: 描述:
 */
public class CuratorClientUtils {

    private static CuratorFramework curatorFramework;

    private static final String CONNECTSERVER = "192.168.1.121:2181,192.168.1.122:2181,192.168.1.123:2181,192.168.1.124:2181";

    public static CuratorFramework getInstance(){
        curatorFramework = CuratorFrameworkFactory.builder().connectString(CONNECTSERVER).sessionTimeoutMs(5000).connectionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        curatorFramework.start();
        return curatorFramework;
    }
}
