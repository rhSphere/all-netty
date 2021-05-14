package com.rhsphere.netty.flash.io;

import java.io.IOException;
import java.net.Socket;
import java.util.Date;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-05-14 14:50
 */
public class IOClient {

    /**
     * 1.线程资源受限：线程是操作系统中非常宝贵的资源，同一时刻有大量的线程处于阻塞状态是非常严重的资源浪费，操作系统耗不起
     * 2.线程切换效率低下：单机 CPU 核数固定，线程爆炸之后操作系统频繁进行线程切换，应用性能急剧下降。
     * 3.除了以上两个问题，IO 编程中，我们看到数据读写是以字节流为单位。
     */

    public static void main(String[] args) {
        // 2.线程切换效率低下
        new Thread(() -> {
            try {
                Socket socket = new Socket("127.0.0.1", 8000);
                // 1.线程资源受限
                while (true) {
                    try {
                        //3.数据读写是以字节流为单位
                        socket.getOutputStream().write((new Date() + ": hello world").getBytes());
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                }
            } catch (IOException e) {
            }
        }).start();
    }
}
