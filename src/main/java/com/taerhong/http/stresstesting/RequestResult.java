package com.taerhong.http.stresstesting;

/**
 * 一次请求的结果
 *
 * @author billhu
 */
public class RequestResult implements Comparable<RequestResult> {

    /**
     * 是否执行成功
     */
    private boolean success;
    /**
     * 执行时长,毫秒
     */
    private long duration;

    public RequestResult(boolean success, long duration) {
        this.success = success;
        this.duration = duration;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int compareTo(RequestResult o) {
        if (this.getDuration() > o.getDuration()) {
            return 1;
        } else if (this.getDuration() < o.getDuration()) {
            return -1;
        }
        return 0;
    }
}
