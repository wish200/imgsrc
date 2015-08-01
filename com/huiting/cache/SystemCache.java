package com.huiting.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.log4j.Logger;


/**
 * 系统配置信息缓存。
 * 此类为Singleton。
 * getCache方法为线程同步，在高并发核心代码处谨慎使用。
 * 
 * @author HanYan
 * @date 2014-09-02
 */
public class SystemCache {
	
	private static final SystemCache instance = new SystemCache();// 单一实例
	private static final String SYSTEM_CONFIG_NAME = "system.properties";// 配置文件的文件夹名称
	private final static Logger log = Logger.getLogger(SystemCache.class);
	
	private Map<String, String> cache = new Hashtable<String, String>();// 各个配置的缓存
	private String webConfigPath;
	
	/**
	 * 构造函数私有化。
	 */
	private SystemCache() {
	}
	
	/**
	 * 获取唯一实例。
	 * @return
	 */
	public static SystemCache newInstance() {
		return instance;
	}
	
	/**
	 * 初始化，将配置文件的参数加载到缓存中。
	 * 
	 * @param context
	 * @throws ServiceException
	 */
	public void init(String path) throws Exception {
		
		log.info("SystemCache初始化开始...");
		Properties prop = new Properties();
		InputStream is = null;
		
		if (path != null) {
			webConfigPath = path + File.separator + SYSTEM_CONFIG_NAME;
		}
		
		File file = new File(webConfigPath);
		if (!file.exists()) {
			throw new Exception("获取配置文件路径失败，未找到的路径:" + webConfigPath);
		}
		
		try {
			is = new FileInputStream(file);
			prop.load(is);
		} catch (IOException e) {
			throw new Exception("初始化系统配置文件失败，I/O异常：", e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		
		Iterator<Entry<Object, Object>> iterator = prop.entrySet().iterator();
		while (iterator.hasNext()) {// 将配置文件中的配置信息加载到缓存中。
			Entry<Object, Object> e = iterator.next();
			cache.put((String)e.getKey(), (String)e.getValue());
		}
		System.out.println(cache.get("webinfo.path"));
		// 将WEB-INF的路径加入到公共缓存中。
		if (cache.get("webinfo.path") == null || "auto".equalsIgnoreCase(cache.get("webinfo.path"))) {
			cache.put("webinfo.path", path);
		}
        System.out.println(SystemCache.getCache("webinfo.path"));
        System.out.println(path);
		log.info("SystemCache初始化完成...");
	}
	
	/**
	 * 应业务需求，对外开放的重新加载缓存配置文件的方法，谨慎使用。
	 * @return
	 */
	public synchronized boolean reload() {
		Map<String, String> cacheTemp = instance.cache;// 备份
		
		try {
			instance.cache.clear();
			instance.init(null);
			
			return true;
		} catch (Exception e) {
			instance.cache = cacheTemp;
			log.error("SystemCache reload failed!", e);
			return false;
		} finally {
			cacheTemp = null;
		}
	}

	/**
	 * 根据key值获取配置。
	 * @param key
	 * @return
	 */
	public static synchronized String getCache(String key) {
		if (key == null) {
			return null;
		}
		return instance.cache.get(key);
	}

	public String getWebConfigPath() {
		return webConfigPath;
	}
	
	
}
