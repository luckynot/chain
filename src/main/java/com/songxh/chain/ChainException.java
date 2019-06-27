package com.songxh.chain;

/**
 * 自定义链异常
 *
 * @author songxh
 * @date 2019-06-26
 */
public class ChainException extends RuntimeException {

    public ChainException(){
        super();
    }

    public ChainException(String message) {
        super(message);
    }

    public ChainException(Exception e) {
        super(e);
    }
}
