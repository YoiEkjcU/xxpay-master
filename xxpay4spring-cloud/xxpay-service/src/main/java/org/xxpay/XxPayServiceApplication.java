package org.xxpay;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: xxpay支付核心服务, 包括:各支付渠道接口,通知处理
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
@EnableDiscoveryClient
@SpringBootApplication
public class XxPayServiceApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(XxPayServiceApplication.class).web(true).run(args);
    }
}
