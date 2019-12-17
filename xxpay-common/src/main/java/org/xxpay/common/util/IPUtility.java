package org.xxpay.common.util;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: IP地址工具类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class IPUtility {

    /**
     * getLocalhostIp(获取本机ip地址)
     *
     * @throws UnknownHostException
     * @Exception 异常对象
     * @since CodingExample　Ver(编码范例查看) 1.1
     */
    private static String getLocalhostIp() {
        String ip = "";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

    private static List<String> getIpAddrs() throws Exception {
        List<String> IPs = new ArrayList<>();
        Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        InetAddress ip;
        while (allNetInterfaces.hasMoreElements()) {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                if (ip instanceof Inet4Address && ip.getHostAddress().contains(".")) {
                    IPs.add(ip.getHostAddress());
                }
            }
        }
        return IPs;
    }

    /**
     * 兼容Linux系统
     *
     * @return
     */
    private static String getLocalIP() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = e1.nextElement();
                Enumeration<InetAddress> e2 = ni.getInetAddresses();
                while (e2.hasMoreElements()) {
                    InetAddress inetAddress = e2.nextElement();
                    if (inetAddress instanceof Inet6Address)
                        continue;
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            return "";
        }
        return ip;
    }

//    public static void main(String[] args) throws Exception {
//        System.out.println(IPUtility.getLocalhostIp());
//        System.out.println(IPUtility.getIpAddrs());
//        System.out.println(IPUtility.getLocalIP());
//    }
}