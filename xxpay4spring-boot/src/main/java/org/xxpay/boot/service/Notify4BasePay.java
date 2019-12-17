package org.xxpay.boot.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.MyLog;
import org.xxpay.common.util.PayDigestUtil;
import org.xxpay.common.util.XXPayUtil;
import org.xxpay.dal.dao.model.MchInfo;
import org.xxpay.dal.dao.model.PayOrder;
import org.xxpay.boot.service.mq.Mq4PayNotify;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付通知处理基类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
@Component
public class Notify4BasePay extends BaseService {

    private static final MyLog _log = MyLog.getLog(Notify4BasePay.class);

    @Autowired
    private Mq4PayNotify mq4PayNotify;

    /**
     * 创建响应URL
     *
     * @param payOrder
     * @param backType 1：前台页面；2：后台接口
     * @return
     */
    private String createNotifyUrl(PayOrder payOrder, String backType) {
        String mchId = payOrder.getMchId();
        MchInfo mchInfo = super.baseSelectMchInfo(mchId);
        String resKey = mchInfo.getResKey();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("payOrderId",    null == payOrder.getPayOrderId()     ? "" : payOrder.getPayOrderId());     // 支付订单号
        paramMap.put("mchId",         null == payOrder.getMchId()          ? "" : payOrder.getMchId());          // 商户ID
        paramMap.put("mchOrderNo",    null == payOrder.getMchOrderNo()     ? "" : payOrder.getMchOrderNo());     // 商户订单号
        paramMap.put("channelId",     null == payOrder.getChannelId()      ? "" : payOrder.getChannelId());      // 渠道ID
        paramMap.put("amount",        null == payOrder.getAmount()         ? "" : payOrder.getAmount());         // 支付金额
        paramMap.put("currency",      null == payOrder.getCurrency()       ? "" : payOrder.getCurrency());       // 货币类型
        paramMap.put("status",        null == payOrder.getStatus()         ? "" : payOrder.getStatus());         // 支付状态
        paramMap.put("clientIp",      null == payOrder.getClientIp()       ? "" : payOrder.getClientIp());       // 客户端IP
        paramMap.put("device",        null == payOrder.getDevice()         ? "" : payOrder.getDevice());         // 设备
        paramMap.put("subject",       null == payOrder.getSubject()        ? "" : payOrder.getSubject());        // 商品标题
        paramMap.put("channelOrderNo",null == payOrder.getChannelOrderNo() ? "" : payOrder.getChannelOrderNo()); // 渠道订单号
        paramMap.put("param1",        null == payOrder.getParam1()         ? "" : payOrder.getParam1());         // 扩展参数1
        paramMap.put("param2",        null == payOrder.getParam2()         ? "" : payOrder.getParam2());         // 扩展参数2
        paramMap.put("paySuccTime",   null == payOrder.getPaySuccTime()    ? "" : payOrder.getPaySuccTime());    // 支付成功时间
        paramMap.put("backType", backType == null ? "" : backType);
        // 先对原文签名
        String reqSign = PayDigestUtil.getSign(paramMap, resKey);
        paramMap.put("sign", reqSign);   // 签名
        // 签名后再对有中文参数编码
        try {
            paramMap.put("device",  URLEncoder.encode(null == payOrder.getDevice()  ? "" : payOrder.getDevice(),  PayConstant.RESP_UTF8));
            paramMap.put("subject", URLEncoder.encode(null == payOrder.getSubject() ? "" : payOrder.getSubject(), PayConstant.RESP_UTF8));
            paramMap.put("param1",  URLEncoder.encode(null == payOrder.getParam1()  ? "" : payOrder.getParam1(),  PayConstant.RESP_UTF8));
            paramMap.put("param2",  URLEncoder.encode(null == payOrder.getParam2()  ? "" : payOrder.getParam2(),  PayConstant.RESP_UTF8));
        } catch (UnsupportedEncodingException e) {
            _log.error("URL Encode exception.", e);
            return null;
        }
        String param = XXPayUtil.genUrlParams(paramMap);
        StringBuffer sb = new StringBuffer();
        sb.append(payOrder.getNotifyUrl()).append("?").append(param);
        return sb.toString();
    }

    /**
     * 处理支付结果前台页面跳转
     */
    public boolean doPage(PayOrder payOrder) {
        String redirectUrl = createNotifyUrl(payOrder, "1");
        _log.info("redirect to respUrl:" + redirectUrl);
        // 前台跳转业务系统
		/*try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			_log.error("XxPay sendRedirect exception. respUrl="+redirectUrl, e);
			return false;
		}*/
        return true;
    }

    /**
     * 处理支付结果后台服务器通知
     */
    protected void doNotify(PayOrder payOrder) {
        _log.info(">>>>>> PAY开始回调通知业务系统 <<<<<<");
        // 发起后台通知业务系统
        JSONObject object = createNotifyInfo(payOrder);
        try {
            mq4PayNotify.send(object.toJSONString());
        } catch (Exception e) {
            _log.error("payOrderId={},sendMessage error.", payOrder != null ? payOrder.getPayOrderId() : "", e);
        }
        _log.info(">>>>>> PAY回调通知业务系统完成 <<<<<<");
    }

    private JSONObject createNotifyInfo(PayOrder payOrder) {
        JSONObject object = new JSONObject();
        object.put("method", "GET");
        object.put("url", createNotifyUrl(payOrder, "2"));
        object.put("orderId", payOrder.getPayOrderId());
        object.put("count", payOrder.getNotifyCount());
        object.put("createTime", System.currentTimeMillis());
        return object;
    }
}