package org.xxpay.common.util;

/**
 * @author dingzhiwei jmdhappy@126.com
 * @version V1.0
 * @Description:
 * @date 2017-07-05
 * @Copyright: www.xxpay.org
 */
public class MyLogFace implements MyLogInf {

    private org.slf4j.Logger _log = null;

    public void setName(String clz) {
        _log = org.slf4j.LoggerFactory.getLogger(clz);
    }

    private boolean isDebugEnabled() {
        return _log.isDebugEnabled();
    }

    private boolean isInfoEnabled() {
        return _log.isInfoEnabled();
    }

    private boolean isWarnEnabled() {
        return _log.isWarnEnabled();
    }

    private boolean isErrorEnabled() {
        return _log.isErrorEnabled();
    }

    private boolean isTraceEnabled() {
        return _log.isTraceEnabled();
    }

    public void trace(String message, Object... args) {
        if (this.isTraceEnabled()) _log.trace(message, args);
    }

    public void debug(String message, Object... args) {
        if (this.isDebugEnabled()) _log.debug(message, args);
    }

    public void info(String message, Object... args) {
        if (this.isInfoEnabled()) _log.info(message, args);
    }

    public void warn(String message, Object... args) {
        if (this.isWarnEnabled()) _log.warn(message, args);
    }

    public void error(String message, Object... args) {
        if (this.isErrorEnabled()) _log.error(message, args);
    }

    public void error(Throwable e, String message, Object... args) {
        if (this.isErrorEnabled()) _log.error(String.format(message, args), e);
    }

    //------------------
    public void error(Throwable e, String message) {//简化版
        if (this.isErrorEnabled()) _log.error(message + e.toString(), e);
    }
}