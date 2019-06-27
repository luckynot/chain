package com.songxh.chain;

import lombok.Getter;
import lombok.Setter;

/**
 * 链数据
 *
 * @author songxh
 * @date 2019-06-26
 */
public class BaseChainData {

    /**
     * 链式处理器状态记录:按位记录
     */
    @Getter
    @Setter
    protected int status = 0;

    @Override
    public String toString() {
        return "status:" + status;
    }
}
