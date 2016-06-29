package com.xutao.insert.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * SQL语句加载类
 * @author xutao
 *
 */
public class SqlLoader {
	private static Logger logger = Logger.getLogger(SqlLoader.class);
	private Map<String, String> sqlMap = new HashMap<String, String>();
	private final String confName = "sqlConfig.properties";

	public SqlLoader() {
		loadSql();
	}

	/**
	 * 加载SQL信息
	 * 
	 * @param sqlPath
	 */
	private void loadSql() {
		Properties prop = new Properties();
		try {
			prop.load(SqlLoader.class.getClassLoader().getResourceAsStream(confName));

			for (String key : prop.stringPropertyNames()) {
				sqlMap.put(key, prop.getProperty(key));
			}

		} catch (IOException e) {
			logger.error(e);
		} 
	}

	/**
	 * 根据tableName查询SQL语句
	 * 
	 * @param tableName
	 * @return
	 */
	public String searchSql(String tableName) {
		return sqlMap.containsKey(tableName) ? sqlMap.get(tableName) : null;
	}

	public static void main(String[] args) {
		SqlLoader loader = new SqlLoader();
		String tableName = "ROUTER_STAT_M";
		String suffix = tableName.substring(tableName.lastIndexOf("_")+1, tableName.length());
		String prefix = tableName.substring(0, tableName.lastIndexOf("_"));
		String insertSql = loader.searchSql(prefix).replace("XXX", suffix);
		System.out.println(insertSql);
	}
}
