package org.xxpay.boot.ctrl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xxpay.boot.service.IMchInfoService;
import org.xxpay.boot.service.IPayChannelService;
import org.xxpay.boot.service.IPayOrderService;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.MyLog;
import org.xxpay.common.util.MySeq;
import org.xxpay.common.util.XXPayUtil;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 支付订单, 包括:统一下单,订单查询,补单等接口
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
@RestController
public class PayOrderController {

    private final MyLog _log = MyLog.getLog(PayOrderController.class);

    @Autowired
    private IPayOrderService payOrderService;

    @Autowired
    private IPayChannelService payChannelService;

    @Autowired
    private IMchInfoService mchInfoService;

    /**
     * 统一下单接口:
     * 1)先验证接口参数以及签名信息
     * 2)验证通过创建支付订单
     * 3)根据商户选择渠道,调用支付服务进行下单
     * 4)返回下单数据
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/api/pay/create_order")
    public String payOrder(@RequestParam String params) {
        JSONObject po = JSONObject.parseObject(params);
        return payOrder(po);
    }

    @RequestMapping(value = "/api/pay/create_order", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public String payOrder(@RequestBody JSONObject params) {
        _log.info("###### 开始接收商户统一下单请求 ######");
        String logPrefix = "【商户统一下单】";
        try {
            JSONObject payContext = new JSONObject();
            JSONObject payOrder = null;
            // 验证参数有效性
            Object object = validateParams(params, payContext);
            if (object instanceof String) {
                _log.info("{}参数校验不通过:{}", logPrefix, object);
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, object.toString(), null, null));
            }
            if (object instanceof JSONObject)
                payOrder = (JSONObject) object;
            if (payOrder == null)
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心下单失败", null, null));
            int result = payOrderService.createPayOrder(payOrder);
            _log.info("{}创建支付订单,结果:{}", logPrefix, result);
            if (result != 1) {
                return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "创建支付订单失败", null, null));
            }
            String channelId = payOrder.getString("channelId");
            String resKey = payContext.getString("resKey");
            switch (channelId) {
                case PayConstant.PAY_CHANNEL_WX_APP:    // 微信APP支付
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_APP, payOrder, resKey);
                case PayConstant.PAY_CHANNEL_WX_JSAPI:  // 微信公众号支付
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_JSPAI, payOrder, resKey);
                case PayConstant.PAY_CHANNEL_WX_NATIVE: // 微信原生扫码支付
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_NATIVE, payOrder, resKey);
                case PayConstant.PAY_CHANNEL_WX_MWEB:   // 微信H5支付
                    return payOrderService.doWxPayReq(PayConstant.WxConstant.TRADE_TYPE_MWEB, payOrder, resKey);
                case PayConstant.PAY_CHANNEL_ALIPAY_MOBILE:
                case PayConstant.PAY_CHANNEL_ALIPAY_PC:
                case PayConstant.PAY_CHANNEL_ALIPAY_WAP:
                case PayConstant.PAY_CHANNEL_ALIPAY_QR:
                    return payOrderService.doAliPayReq(channelId, payOrder, resKey);
                default:
                    return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "不支持的支付渠道类型[channelId=" + channelId + "]", null, null));
            }
        } catch (Exception e) {
            _log.error(e, "");
            return XXPayUtil.makeRetFail(XXPayUtil.makeRetMap(PayConstant.RETURN_VALUE_FAIL, "支付中心系统异常", null, null));
        }
    }

    /**
     * 验证创建订单请求参数,参数通过返回JSONObject对象,否则返回错误文本信息
     *
     * @param params
     * @return
     */
    private Object validateParams(JSONObject params, JSONObject payContext) {
        // 验证请求参数,参数有问题返回错误提示
        // 支付参数
        String mchId = params.getString("mchId");      // 商户ID
        String mchOrderNo = params.getString("mchOrderNo"); // 商户订单号
        String channelId = params.getString("channelId");  // 渠道ID
        String amount = params.getString("amount");     // 支付金额（单位分）
        String currency = params.getString("currency");   // 币种
        String notifyUrl = params.getString("notifyUrl");  // 支付结果回调URL
        String subject = params.getString("subject");    // 商品主题
        String body = params.getString("body");       // 商品描述信息
        String sign = params.getString("sign");       // 签名

        String clientIp = params.getString("clientIp");   // 客户端IP
        String device = params.getString("device");     // 设备
        String extra = params.getString("extra");      // 特定渠道发起时额外参数
        String param1 = params.getString("param1");     // 扩展参数1
        String param2 = params.getString("param2");     // 扩展参数2
        // 验证请求参数有效性（必选项）
        if (StringUtils.isBlank(mchId)) return "request params[mchId] error.";
        if (StringUtils.isBlank(mchOrderNo)) return "request params[mchOrderNo] error.";
        if (StringUtils.isBlank(channelId)) return "request params[channelId] error.";
        if (!NumberUtils.isNumber(amount)) return "request params[amount] error.";
        if (StringUtils.isBlank(currency)) return "request params[currency] error.";
        if (StringUtils.isBlank(notifyUrl)) return "request params[notifyUrl] error.";
        if (StringUtils.isBlank(subject)) return "request params[subject] error.";
        if (StringUtils.isBlank(body)) return "request params[body] error.";
        if (StringUtils.isEmpty(sign)) return "request params[sign] error.";

        // 根据不同渠道,判断extra参数
        if (channelId.equalsIgnoreCase(PayConstant.PAY_CHANNEL_WX_JSAPI)) {         // 微信公众号支付
            if (StringUtils.isEmpty(extra)) return "request params[extra] error.";

            JSONObject extraObject = JSON.parseObject(extra);
            String openId = extraObject.getString("openId");
            if (StringUtils.isBlank(openId)) return "request params[extra.openId] error.";

        } else if (channelId.equalsIgnoreCase(PayConstant.PAY_CHANNEL_WX_NATIVE)) {// 微信原生扫码支付
            if (StringUtils.isEmpty(extra)) return "request params[extra] error.";

            JSONObject extraObject = JSON.parseObject(extra);
            String prodId = extraObject.getString("productId");
            if (StringUtils.isBlank(prodId)) return "request params[extra.productId] error.";

        } else if (channelId.equalsIgnoreCase(PayConstant.PAY_CHANNEL_WX_MWEB)) {   // 微信H5支付
            if (StringUtils.isEmpty(extra)) return "request params[extra] error.";

            JSONObject extraObject = JSON.parseObject(extra);
            String prodId = extraObject.getString("sceneInfo");
            if (StringUtils.isBlank(prodId)) return "request params[extra.sceneInfo] error.";
            if (StringUtils.isBlank(clientIp)) return "request params[clientIp] error.";
        }

        // 查询商户信息
        JSONObject mchInfo = mchInfoService.getByMchId(mchId);
        if (mchInfo == null) return "Can't found mchInfo[mchId=" + mchId + "] record in db.";

        if (mchInfo.getByte("state") != 1) return "mchInfo not available [mchId=" + mchId + "] record in db.";

        String reqKey = mchInfo.getString("reqKey");
        if (StringUtils.isBlank(reqKey)) return "reqKey is null[mchId=" + mchId + "] record in db.";
        payContext.put("resKey", mchInfo.getString("resKey"));

        // 查询商户对应的支付渠道
        JSONObject payChannel = payChannelService.getByMchIdAndChannelId(mchId, channelId);
        if (payChannel == null)
            return "Can't found payChannel[channelId=" + channelId + ",mchId=" + mchId + "] record in db.";

        if (payChannel.getByte("state") != 1)
            return "channel not available [channelId=" + channelId + ",mchId=" + mchId + "]";

        // 验证签名数据
        boolean verifyFlag = XXPayUtil.verifyPaySign(params, reqKey);
        if (!verifyFlag) return "Verify XX pay sign failed.";

        // 验证参数通过,返回JSONObject对象
        JSONObject payOrder = new JSONObject();
        payOrder.put("payOrderId", MySeq.getPay());
        payOrder.put("mchId", mchId);
        payOrder.put("mchOrderNo", mchOrderNo);
        payOrder.put("channelId", channelId);
        payOrder.put("amount", Long.parseLong(amount));
        payOrder.put("currency", currency);
        payOrder.put("clientIp", clientIp);
        payOrder.put("device", device);
        payOrder.put("subject", subject);
        payOrder.put("body", body);
        payOrder.put("extra", extra);
        payOrder.put("channelMchId", payChannel.getString("channelMchId"));
        payOrder.put("param1", param1);
        payOrder.put("param2", param2);
        payOrder.put("notifyUrl", notifyUrl);
        return payOrder;
    }
}