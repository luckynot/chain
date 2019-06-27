package com.songxh.chain;

import lombok.Getter;
import lombok.Setter;

/**
 * 软件安装链数据
 *
 * @author hexiaosong
 * @date 2019-06-26
 */
@Getter
@Setter
public class SoftwareChainData extends BaseChainData {

    /**
     * 下载地址
     */
    private String downloadURL;

    /**
     * 安装路径
     */
    private String location;

    /**
     * 配置信息
     */
    private String config;

    /**
     * 启动命令
     */
    private String instruction;

    /**
     * 测试用状态:标明每个链节点是否能够执行成功
     */
    private int successStatus;
}
