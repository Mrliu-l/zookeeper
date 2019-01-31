package com.liulei.fly.javaApi.exclusiveLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author liu_l
 * @email: liu_lei_programmer@163.com
 * @time 2019/1/24 21:17
 * @Description: 描述: zookeeper 实现排它锁_分布式锁
 */
public class ExclusiveLock {

    private static ZooKeeper zooKeeper;

    private static final String ROOT_LOCKS = "/LOCKS";
    //锁节点id
    private String lockID;
    //节点数据
    private static final byte[] data = {1,2};
    //定义watcher中的计数器
    private CountDownLatch countDownLatch = new CountDownLatch(1);
    //客户端连接超时市场
    private int connectOutTime;

    public ExclusiveLock(){
        try {
            zooKeeper = ZookeeperClient.getClient();
            connectOutTime = ZookeeperClient.sessionTimeOut;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean lock(){
        try {
            //1.创建临时有序节点
            lockID = zooKeeper.create(ROOT_LOCKS + "/", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            //2.获取所有子节点,并组装为tree
            List<String> childrenIDs = zooKeeper.getChildren(ROOT_LOCKS, true);
            SortedSet<String> childrenIDTree = new TreeSet<>();
            for(String childrenID : childrenIDs){
                childrenIDTree.add(ROOT_LOCKS + "/" + childrenID);
            }
            //3.判断此临时有序节点是否为最小子节点
            //4.是-得到锁，
            String firstID = childrenIDTree.first();
            if(lockID.equals(firstID)){
                System.out.println(Thread.currentThread().getName() + "->成功获得锁,lock节点为：[" + lockID + "]");
                return true;
            }
            //5.否-监听本节点的前一个子节点
            SortedSet<String> sortedSet = childrenIDTree.headSet(lockID);
            if(!sortedSet.isEmpty()){
                String preLockId = sortedSet.last();
                zooKeeper.exists(preLockId, new LockWatcher(countDownLatch));
                countDownLatch.await(connectOutTime, TimeUnit.SECONDS);
                System.out.println(Thread.currentThread().getName() + "->成功获得锁,lock节点为：[" + lockID + "]");
            }
            return true;

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean unLock() {
        System.out.println(Thread.currentThread().getName() + "->开始释放锁,lock节点为：[" + lockID + "]");
        try {
            zooKeeper.delete(lockID, -1);
            System.out.println("节点：[" + lockID + "]成功被删除");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        CountDownLatch count = new CountDownLatch(10);
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            new Thread(()->{
                ExclusiveLock exclusiveLock = new ExclusiveLock();
                try {
                    count.countDown();
                    count.await();
                    exclusiveLock.lock();
                    TimeUnit.MILLISECONDS.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    exclusiveLock.unLock();
                }
            }).start();
        }
    }
}
