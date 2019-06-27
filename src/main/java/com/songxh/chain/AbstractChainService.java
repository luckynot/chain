package com.songxh.chain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 链服务抽象方法
 *
 * @author songxh
 * @date 2019-06-26
 */
public abstract class AbstractChainService<T extends BaseChainData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractChainService.class);

    /**
     * 链节点个数:最大个数为31
     * int二进制表示最多只有31个1
     * 大多数情况链个数都不会太大,表示不想用long类型（一脸矫情= =|）
     */
    private int count = 0;

    /**
     * 链处理入口
     */
    protected BaseChainExecutor<T> head;

    public AbstractChainService() {
        head = initChainExecutor();
        if (null == head) {
            throw new ChainException("链处理器初始化异常,头节点不能为null");
        }
        BaseChainExecutor<T> mark = head;
        while (mark != null && count < 31) {
            mark.setStep(1 << count++);
            mark = mark.getNext();
        }
        if (mark != null) {
            throw new ChainException("链处理器初始化异常,链数量太大");
        }
    }

    /**
     * 初始化链处理器，拼接链
     *
     * @return 链的头节点，即链的处理入口
     */
    public abstract BaseChainExecutor<T> initChainExecutor();

    /**
     * 链式服务运行逻辑
     *
     * @param chainData 链消息
     * @return 成功返回true，失败时返回false，chainData的status会改变
     */
    public boolean process(T chainData) {
        BaseChainExecutor<T> mark = head;
        // 循环处理链上每一个节点
        while (mark != null) {
            // 判断当前链执行状态
            if (mark.judgeFinish(chainData.getStatus())) {
                mark = mark.getNext();
                continue;
            }
            // 执行当前链
            boolean res;
            try {
                res = mark.execute(chainData);
            } catch (Exception e) {
                LOGGER.error("[{}]chain execute error,chainData={}", mark.getStep(),
                        chainData.toString(), e);
                throw new ChainException(e);
            }
            if (res) {
                // 记录当前链执行成功
                mark.finish(chainData);
                mark = mark.getNext();
            } else if (mark.executeFail(chainData)) {
                // 当前链执行失败，调用失败处理逻辑，返回true，继续执行
                mark = mark.getNext();
            } else {
                break;
            }
        }
        return judgeFullFinish(chainData.getStatus());
    }

    /**
     * 判断整个链是否全部处理完成
     *
     * @param status 链执行状态
     * @return 完成返回true，未完成返回false
     */
    private boolean judgeFullFinish(int status) {
        return status != 0 && ((status + 1) & status) == 0 && status >> (count - 1) > 0;
    }
}
