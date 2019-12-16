package org.xxpay.boot.ctrl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.boot.service.INotifyPayService;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.MyLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 接收处理支付宝通知
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
@RestController
public class Notify4AliPayController {

    private static final MyLog _log = MyLog.getLog(Notify4AliPayController.class);

    @Autowired
    private INotifyPayService notifyPayService;

    /**
     * 支付宝移动支付后台通知响应
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/notify/pay/aliPayNotifyRes.htm")
    @ResponseBody
    public String aliPayNotifyRes(HttpServletRequest request) throws ServletException, IOException {
        _log.info("====== 开始接收支付宝支付回调通知 ======");
        String notifyRes = doAliPayRes(request);
        _log.info("响应给支付宝:{}", notifyRes);
        _log.info("====== 完成接收支付宝支付回调通知 ======");
        return notifyRes;
    }

    private String doAliPayRes(HttpServletRequest request) throws ServletException, IOException {
        String logPrefix = "【支付宝支付回调通知】";
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        Iterator<String> iter = requestParams.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            String[] values = requestParams.get(name);
            StringBuffer valueStr = new StringBuffer();
            int len = values.length;
            if (len > 0) {
                for (int i = 0; i < len; i++) {
                    valueStr.append(values[i]);
                    if (!(i == len - 1)) {
                        valueStr.append(",");
                    }
                }
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr.toString());
        }
        _log.info("{}通知请求数据:reqStr={}", logPrefix, params);
        if (params.isEmpty()) {
            _log.error("{}请求参数为空", logPrefix);
            return PayConstant.RETURN_ALIPAY_VALUE_FAIL;
        }
        return notifyPayService.handleAliPayNotify(params);
    }
}