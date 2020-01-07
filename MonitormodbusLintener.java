package com.hotent.core.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hotent.core.sms.impl.StartThread;

/**
 * 监控逸云天气体采集
 * <pre>
 * 1.服务器启动时，调用初始化系统模版事件。
 * 2.服务器关闭是，停止短信猫事件。
 * </pre>
 * @author ray
 *
 */
public class MonitormodbusLintener implements ServletContextListener {

	private Log logger = LogFactory.getLog(MonitormodbusLintener.class);
	
	public void contextInitialized(ServletContextEvent arg0) {
		logger.debug("[contextDestroyed]监控逸云天气体采集。");
		
        StartThread s=new StartThread();
        s.setDaemon(true);// 设置线程为后台线程，tomcat不会被hold,启动后依然一直监听。
        s.start();
    }

	public void contextDestroyed(ServletContextEvent arg0) {	

	}
}
