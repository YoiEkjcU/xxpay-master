package org.xxpay.common.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/6/23.
 */
public class ObjectValidUtil {

    private static boolean isValid(Object... objs) {
        if (objs == null || objs.length < 1) {
            return false;
        }

        for (Object obj : objs) {
            if (obj instanceof Short) {
                if (isNull(obj)) {
                    return false;
                }
            } else if (obj instanceof Integer) {
                if (isInvalidInteger((Integer) obj)) {
                    return false;
                }
            } else if (obj instanceof Long) {
                if (isInvalidLong((Long) obj)) {
                    return false;
                }
            } else if (obj instanceof String) {
                if (isInvalidString(obj.toString())) {
                    return false;
                }
            } else if (obj instanceof List) {
                if (CollectionUtils.isEmpty((List) obj)) {
                    return false;
                }
            } else if (obj instanceof Map) {
                if (isNull(obj) || ((Map) obj).isEmpty()) {
                    return false;
                }
            } else {
                if (isNull(obj)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isInvalid(Object... objs) {
        return !isValid(objs);
    }

    /**
     * 判断是否为有效Short值
     *
     * @param num
     * @return
     */
    private static boolean isValidShort(Short num) {
        return num != null && num.compareTo((short) 0) > 0;
    }

    public static boolean isInvalidShort(Short num) {
        return !isValidShort(num);
    }

    /**
     * 判断是否为有效Integer值
     *
     * @param num
     * @return
     */
    private static boolean isValidInteger(Integer num) {
        return num != null && num.compareTo(0) > 0;
    }

    private static boolean isInvalidInteger(Integer num) {
        return !isValidInteger(num);
    }

    /**
     * 判断是否为有效Long值
     *
     * @param num
     * @return
     */
    private static boolean isValidLong(Long num) {
        return num != null && num.compareTo(0L) > 0;
    }

    private static boolean isInvalidLong(Long num) {
        return !isValidLong(num);
    }

    /**
     * 判断是否为有效BigDecimal值
     *
     * @param num
     * @return
     */
    private static boolean isValidBigDecimal(BigDecimal num) {
        return num != null && num.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isInvalidBigDecimal(BigDecimal num) {
        return !isValidBigDecimal(num);
    }

    /**
     * 判断是否为有效String值
     *
     * @param str
     * @return
     */
    public static boolean isValidString(String str) {
        return StringUtils.isNotBlank(str);
    }

    private static boolean isInvalidString(String str) {
        return StringUtils.isBlank(str);
    }

    private static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    private static boolean isValidCurPage(Integer curPage) {
        if (curPage == null) {
            return false;
        }
        return curPage.compareTo(1) >= 0;
    }

    private static boolean isValidCurPage(Long curPage) {
        if (curPage == null) {
            return false;
        }
        return curPage.compareTo(1L) >= 0;
    }

    public static boolean isInvalidCurPage(Integer curPage) {
        return !isValidCurPage(curPage);
    }

    public static boolean isInvalidCurPage(Long curPage) {
        return !isValidCurPage(curPage);
    }

    private static boolean isValidViewNumber(Integer viewNumber) {
        if (viewNumber == null) {
            return false;
        }
        return viewNumber.compareTo(0) > 0;
    }

    private static boolean isValidViewNumber(Long viewNumber) {
        if (viewNumber == null) {
            return false;
        }
        return viewNumber.compareTo(0L) > 0;
    }

    public static boolean isInvalidViewNumber(Integer viewNumber) {
        return !isValidViewNumber(viewNumber);
    }

    public static boolean isInvalidViewNumber(Long viewNumber) {
        return !isValidViewNumber(viewNumber);
    }

    private static boolean isValidLimit(Integer limit) {
        if (limit == null) {
            return false;
        }
        return limit.compareTo(0) > 0;
    }

    public static boolean isInvalidLimit(Integer limit) {
        return !isValidLimit(limit);
    }

    public static boolean isAllNull(Object... objs) {
        if (objs == null || objs.length < 1) {
            return true;
        }
        int nullcount = 0;
        for (Object obj : objs) {
            if (isNull(obj)) {
                nullcount++;
            }
        }
        return objs.length == nullcount;
    }
}