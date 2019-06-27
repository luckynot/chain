package com.songxh.chain;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 链处理服务测试用例
 *
 * @author hexiaosong
 * @date 2019-06-26
 */
public class ChainTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChainTest.class);

    public class SoftwareChainService extends AbstractChainService<SoftwareChainData> {

        @Override
        public BaseChainExecutor<SoftwareChainData> initChainExecutor() {
            // 下载链节点
            BaseChainExecutor<SoftwareChainData> downloadExecutor = new BaseChainExecutor<SoftwareChainData>() {
                @Override
                public boolean execute(SoftwareChainData chainData) {
                    LOGGER.info("从{}下载", chainData.getDownloadURL());
                    // 下载成功
                    if ((chainData.getSuccessStatus() & 0b1000) > 0) {
                        LOGGER.info("下载成功");
                        return true;
                    }
                    LOGGER.info("下载失败");
                    return false;
                }
            };
            // 安装链节点
            BaseChainExecutor<SoftwareChainData> installExecutor = new BaseChainExecutor<SoftwareChainData>() {
                @Override
                public boolean execute(SoftwareChainData chainData) {
                    LOGGER.info("安装在{}", chainData.getLocation());
                    // 安装成功
                    if ((chainData.getSuccessStatus() & 0b0100) > 0) {
                        LOGGER.info("安装成功");
                        return true;
                    }
                    LOGGER.info("安装失败");
                    return false;
                }

                @Override
                public boolean executeFail(SoftwareChainData chainData) {
                    LOGGER.warn("安装失败，google一下吧");
                    return false;
                }
            };
            // 配置链节点
            BaseChainExecutor<SoftwareChainData> configExecutor = new BaseChainExecutor<SoftwareChainData>() {
                @Override
                public boolean execute(SoftwareChainData chainData) {
                    LOGGER.info("使用配置{}", chainData.getConfig());
                    // 配置成功
                    if ((chainData.getSuccessStatus() & 0b0010) > 0) {
                        LOGGER.info("配置成功");
                        return true;
                    }
                    LOGGER.info("配置失败");
                    return false;
                }

                @Override
                public boolean executeFail(SoftwareChainData chainData) {
                    LOGGER.warn("配置失败，放弃配置，用默认配置吧");
                    finish(chainData);
                    return true;
                }
            };
            // 启动链节点
            BaseChainExecutor<SoftwareChainData> launchExecutor = new BaseChainExecutor<SoftwareChainData>() {
                @Override
                public boolean execute(SoftwareChainData chainData) {
                    LOGGER.info("启动命令{}启动", chainData.getInstruction());
                    // 启动成功
                    if ((chainData.getSuccessStatus() & 0b0001) > 0) {
                        LOGGER.info("启动成功");
                        return true;
                    }
                    LOGGER.info("启动失败");
                    return false;
                }
            };
            // 拼接链节点并返回头节点
            downloadExecutor.setNext(installExecutor).setNext(configExecutor).setNext(launchExecutor);
            return downloadExecutor;
        }
    }

    @Test
    public void chainTest() {
        SoftwareChainService softwareChainService = new SoftwareChainService();
        SoftwareChainData softwareChainData = new SoftwareChainData();
        softwareChainData.setDownloadURL("www.download.com");
        softwareChainData.setLocation("/data/server");
        softwareChainData.setConfig("my.config");
        softwareChainData.setInstruction("service start");
        softwareChainData.setSuccessStatus(0b0000);
        for (int i = 0; i < 3; i++) {
            LOGGER.info(">>>>>>>>>>>>>>>>第{}次尝试", i + 1);
            if (softwareChainService.process(softwareChainData)) {
                break;
            }
        }
    }
}
