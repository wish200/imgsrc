package com.huiting.listener;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.huiting.cache.SystemCache;



/**
 * Initialize the web application context.
 * All of initialization should be write here.
 * 
 * @author HanYan
 * @date 2014-11-10
 */
public class SystemInitListener implements ServletContextListener {
	
	private final static Logger log = Logger.getLogger(SystemInitListener.class);
	/** Parameter specifying the profile's folder */
	private static final String CONFIG_PATH_PARAM = "WebConfigPath";

	/**
	 * Initialize the web application context.
	 */
	public void contextInitialized(ServletContextEvent event) {
		ServletContext context = event.getServletContext();
		
		String webRootPath = context.getRealPath(File.separator);
		String webConfigPath = (String)context.getInitParameter(CONFIG_PATH_PARAM);
		String configFullPath = webRootPath + webConfigPath;// the full path of the profile's folder.
		
		File file = new File(configFullPath);
		try {
			if (!file.exists()) {
				throw new Exception("Can not found the folder [" + configFullPath + "], " +
						"please check the context-param in your web.xml which named " + CONFIG_PATH_PARAM);
			}
			file = null;
			
			log.info("SystemCache Initialization begins ...");
		
			//Initialize the web application level configuration with the file named system.properties.
			SystemCache.newInstance().init(configFullPath);
			//Class.forName("com.sinosoft.message.cache.CacheManager");
			// add other init(String) methods ...
		} catch (Exception e) {
			try {
				throw e;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}  finally {
			log.info("Initialization has finished ...");
		}
	}

	/**
	 * Close the root web application context.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		// do nothing...
	}
}

