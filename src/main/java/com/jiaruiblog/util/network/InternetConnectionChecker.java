package com.jiaruiblog.util.network;

import java.net.InetAddress;

public class InternetConnectionChecker {
    public static boolean isInternetAvailable() {
        try {
            // 尝试解析 Google 的域名
            InetAddress address = InetAddress.getByName("github.com");
            // 如果能返回地址，说明可以连接到互联网
            return address.isReachable(2000); // 超时设置为 2 秒
        } catch (Exception e) {
            return false; // 出现异常说明无法连接互联网
        }
    }

    public static void main(String[] args) {
        if (isInternetAvailable()) {
            System.out.println("Can connect to the Internet.");
        } else {
            System.out.println("Cannot connect to the Internet.");
        }
    }
}
