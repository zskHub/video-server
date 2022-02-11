package com.winnerdt.modules.video.ffch4j.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认任务消息输出处理
 * @author eguid
 * @since jdk1.7
 * @version 2017年10月13日
 */
@Slf4j
public class DefaultOutHandlerMethod implements OutHandlerMethod{

	/**
	 * 任务是否异常中断，如果
	 */
	public boolean isBroken=false;

	@Override
	public void parse(String id,String msg) {
		//过滤消息
		if (msg.indexOf("fail") != -1) {
			log.error(id + "任务可能发生故障：" + msg);
			log.error("失败，设置中断状态");
			isBroken=true;
		}else if(msg.indexOf("miss")!= -1) {
			log.error(id + "任务可能发生丢包：" + msg);
			log.error("失败，设置中断状态");
			isBroken=true;
		}else {
			isBroken=false;
			log.debug(id + "消息：" + msg);

		}

	}

	@Override
	public boolean isbroken() {
		return isBroken;
	}

}
