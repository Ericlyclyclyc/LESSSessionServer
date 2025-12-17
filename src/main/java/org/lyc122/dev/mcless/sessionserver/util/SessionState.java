package org.lyc122.dev.mcless.sessionserver.util;

public enum SessionState {
    // 枚举常量定义
    IN_PROGRESS(1),  // 进行中
    TERMINATED(2);   // 已结束

    // 状态码属性
    private final int code;

    // 私有构造函数
    private SessionState(int code) {
        this.code = code;
    }

    // 获取状态码
    public int getCode() {
        return code;
    }
}
