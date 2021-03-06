package org.xxpay.common.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description: 生成全局唯一序列号工具类
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class MySeq {

    private final static AtomicLong pay_seq = new AtomicLong(0L);
    private final static String pay_seq_prefix = "P";
    private final static AtomicLong trans_seq = new AtomicLong(0L);
    private final static String trans_seq_prefix = "T";
    private final static AtomicLong refund_seq = new AtomicLong(0L);
    private final static String refund_seq_prefix = "R";
    private final static String node = "00";

    public static String getPay() {
        return getSeq(pay_seq_prefix, pay_seq);
    }

    public static String getTrans() {
        return getSeq(trans_seq_prefix, trans_seq);
    }

    public static String getRefund() {
        return getSeq(refund_seq_prefix, refund_seq);
    }

    private static String getSeq(String prefix, AtomicLong seq) {
        prefix += node;
        return String.format("%s%s%06d", prefix, DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
    }
}