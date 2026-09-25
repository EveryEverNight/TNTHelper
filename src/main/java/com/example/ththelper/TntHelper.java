package com.example.tnthelper;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TntHelper implements ModInitializer {

	public static final String MOD_ID = "tnt-helper";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		setupFileLogger();
	}

	private static void setupFileLogger() {
		try {
			LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
			Configuration config = ctx.getConfiguration();

			PatternLayout layout = PatternLayout.newBuilder()
					.withPattern("[%d{yyyy-MM-dd HH:mm:ss}] [%t/%level] [%logger{36}]: %msg%n")
					.withConfiguration(config)
					.build();

			FileAppender fileAppender = FileAppender.newBuilder()
					.setName("TntOwnerFileAppender")
					.withFileName("logs/tnt-owner.log")
					.setLayout(layout)
					.withAppend(false)
					.setIgnoreExceptions(false)
					.setConfiguration(config)
					.build();

			fileAppender.start();
			config.addAppender(fileAppender);

			org.apache.logging.log4j.core.Logger modLogger =
					(org.apache.logging.log4j.core.Logger) LogManager.getLogger(MOD_ID);

			modLogger.addAppender(fileAppender);
			modLogger.setLevel(Level.INFO);
			modLogger.setAdditive(false);

			LOGGER.info("[TNT Helper] 已加载，日志输出到 logs/tnt-owner.log");
		} catch (Exception e) {
			LOGGER.error("[TNT Helper] 文件日志初始化失败，回退到控制台输出", e);
		}
	}
}
