package com.songxh.chain;

import lombok.Getter;
import lombok.Setter;

/**
 * 链处理器基类,子类可根据情况实现以下两个方法
 * 1、execute方法：链核心处理逻辑
 * 2、executeFail方法：链失败处理方法
 *
 * @author songxh
 * @date 2019-06-26
 */
public abstract class BaseChainExecutor<T extends BaseChainData> {

    /**
     * 是否执行的标记:2的幂次，用移位运算标识步骤，与运算标识步骤是否完成
     */
    @Getter
    @Setter
    private int step;

    /**
     * 下一链
     */
    @Getter
    private BaseChainExecutor<T> next;

    /**
     * 链执行方法，抽象方法，需要子类实现
     *
     * @param chainData 链数据
     * @return 执行结果, 返回失败时，会中断当前链，上层自定义重试机制去重试
     */
    public abstract boolean execute(T chainData);

    /**
     * 执行失败时默认中断链执行，需要单独处理当前链的错误情况需要重写这个方法
     *
     * @param chainData 链数据
     * @return 返回true时继续执行接下来的链，返回false时中断链执行
     */
    public boolean executeFail(T chainData) {
        return false;
    }

    /**
     * 判断当前链是否完成
     *
     * @param status 链执行状态
     * @return 完成返回true，未完成返回false
     */
    public boolean judgeFinish(int status) {
        return (step & status) > 0;
    }

    /**
     * 标识当前链处理完成
     *
     * @param chainData 链数据
     */
    public void finish(T chainData) {
        chainData.setStatus(chainData.getStatus() | step);
    }

    public BaseChainExecutor<T> setNext(BaseChainExecutor<T> next) {
        this.next = next;
        return next;
    }
}
