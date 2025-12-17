package org.lyc122.dev.mcless.sessionserver.util;

public enum PlayerState {
    ONLINE(0),
    OFFLINE(1),
    BANNED(2);
    //状态码属性
    private final int code;

    //私有构造函数
    PlayerState(int code){
        this.code = code;
    }
    //获取响应码
    public int getCode(){
        return code;
    }
}
