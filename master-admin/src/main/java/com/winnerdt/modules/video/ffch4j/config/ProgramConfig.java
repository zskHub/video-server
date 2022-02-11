package com.winnerdt.modules.video.ffch4j.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 程序基础配置
 *
 * @author eguid
 *
 */
@Data
public class ProgramConfig {
	/**
	 * 默认命令行执行根路径
	 * */
	private String path;
	/**
	 * 是否开启debug模式
	 * */
	private boolean debug;
	/**
	 * 任务池大小
	 * */
	private Integer size;
	/**
	 * 回调通知地址
	 * */
	private String callback;
	/**
	 * 是否开启保活
	 * */
	private boolean keepalive;

	private String pushCommand1;

	private String pushCommand2;

	private String getCommand1;
}
