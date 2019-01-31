package com.liulei.fly.curator.crud;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 9:44
 * @Description: 描述:
 */
public class CuratorOperatorDemo {


    public static void main(String[] args) throws Exception {
        CuratorFramework curatorFramework = CuratorClientUtils.getInstance();
        System.out.println("连接成功" + curatorFramework.getState());
        //创建永久节点
        String path = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                .forPath("/liulei/liulei1/liulei11", "init".getBytes());
        System.out.println("创建永久节点：" + path);
        //获取永久节点数据
        byte[] bytes = curatorFramework.getData().forPath("/liulei/liulei1/liulei11");
        String data = new String(bytes);
        System.out.println("获取永久节点数据：" + data);
        //修改永久节点数据
        curatorFramework.setData().forPath("/liulei/liulei1/liulei11","666".getBytes());
        bytes = curatorFramework.getData().forPath("/liulei/liulei1/liulei11");
        data = new String(bytes);
        System.out.println("修改后的数据为：" + data);
        //删除永久节点
        curatorFramework.delete().deletingChildrenIfNeeded().forPath("/liulei");

        //创建临时节点并验证_异步执行
        //创建临时节点时，如果存在父节点，那父节点必然是持久节点，临时节点不能存在子节点
        ExecutorService service = Executors.newFixedThreadPool(1);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
            @Override
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println(Thread.currentThread().getName() + ";resultCode:" + curatorEvent.getResultCode() + ";TYPE:" +curatorEvent.getType());
                countDownLatch.countDown();
            }
        },service).forPath("/test/test1/test1-1", "1231".getBytes());
        countDownLatch.await();
        service.shutdown();
        curatorFramework.close();
        TimeUnit.SECONDS.sleep(5);
        curatorFramework = CuratorClientUtils.getInstance();
        Stat stat = curatorFramework.checkExists().forPath("/test/test1/test1-1");
        System.out.println("已经不存在临时节点：" + (stat == null));
        System.out.println("success");
    }
}
