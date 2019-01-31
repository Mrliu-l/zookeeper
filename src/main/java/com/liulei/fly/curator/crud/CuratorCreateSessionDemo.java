package com.liulei.fly.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 9:32
 * @Description: 描述:创建回话的两种方式
 */
public class CuratorCreateSessionDemo {

    private static final String CONNECTSERVER = "192.168.1.121:2181,192.168.1.122:2181,192.168.1.123:2181,192.168.1.124:2181";

    public static void main(String[] args) {
        //1、通过CuratorFrameworkFactory新建一个连接
        CuratorFramework curatorFramework = CuratorFrameworkFactory.newClient(CONNECTSERVER, 4000, 4000,
                new ExponentialBackoffRetry(1000, 3));
        curatorFramework.start();
        //通过fluent风格创建
        CuratorFramework fluentCurator = CuratorFrameworkFactory.builder().connectString(CONNECTSERVER).sessionTimeoutMs(4000).connectionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
        System.out.println("连接成功");
    }
}
