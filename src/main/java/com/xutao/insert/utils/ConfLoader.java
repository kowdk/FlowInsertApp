package com.xutao.insert.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.GlobalSettings;

/**
 * 读取Router_id和IP地址的对应关系，需要动态加载，以.ok为读取标志
 * @author xutao
 *
 */
public class ConfLoader {

	private Logger logger = Logger.getLogger(ConfLoader.class);
	
	private final String confDir = GlobalSettings.ID_CONF_PATH;// ip-router_id配置路径
	private Map<String, String> map = new HashMap<String, String>();//ip是key,router_id是value
	
	public ConfLoader(){
		loadIdAndIPConf();
		//loadID();
	}
	
	/**
	 * 判断当前配置文件夹下有没有.ok文件
	 * @return
	 */
	public boolean hasNewConf(){
		File rootDir = new File(confDir);
		File[] files = rootDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].getAbsolutePath();
			if (filePath.endsWith(".ok")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 载入IP和ID对应关系
	 */
	private void loadIdAndIPConf() {
		BufferedReader br = null;
		File rootDir = new File(confDir);
		File[] files = rootDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].getAbsolutePath();
			if (filePath.endsWith(".ok")) { // 监测到.ok文件
				String confPath = filePath.substring(0, filePath.length() - 3);
				try {
					br = new BufferedReader(new FileReader(new File(confPath)));
					String line = "";
					while ((line = br.readLine()) != null) {
						String[] fields = line.split("\t");
						map.put(fields[1], fields[0]); // 0：id; 1: ip
					}
				} catch (IOException e) {
					logger.error(e);
				} finally {
					try {
						br.close();
						new File(filePath).delete(); // 删除.ok文件
						//new File(confPath).delete(); // 删除配置文件
					} catch (IOException e) {
						logger.error(e);
					}
				}
			}
		}
	}
	
	/**
	 * 根据网络字节序IP地址查找对应router_id
	 * @param ipLong
	 * @return
	 */
	public String searchRouterID(String ipLong) {
		String ip = IPLongToStr(Long.parseLong(ipLong));
		return map.containsKey(ip) ? map.get(ip) : null;
	}
	
	/**
	 * 将IP从字符串转为long
	 * @param ip
	 * @return
	 */
	public long IPStrToLong(String ip) {
		long res = 0L;
		ip = ip.trim();
		String[] ips = ip.split("\\.");
		for (int i = 0; i < 4; ++i) {
			res = res << 8 | Integer.parseInt(ips[i]);
		}
		return res;
	}

	/**
	 * 将ip从long转为字符串
	 * @param ip
	 * @return
	 */
	public String IPLongToStr(long ip) {
		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((ip & 0x000000FF))).append(".");
		sb.append(String.valueOf((ip & 0x0000FFFF) >>> 8)).append(".");
		sb.append(String.valueOf((ip & 0x00FFFFFF) >>> 16)).append(".");
		sb.append(String.valueOf((ip >>> 24)));
		return sb.toString();
	}

	/**
	 * 测试函数
	 * @param args
	 */
	public static void main(String[] args) {
		/*String longIp = "17017024";
		ConfLoader loader = new ConfLoader();
		System.out.println(loader.searchRouterID(longIp));*/
		
		File rootDir = new File("G:\\Experiment\\flow\\src\\R5");
		File[] files = rootDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].getAbsolutePath();
			System.out.println(filePath);
		}
		
	}
	
}
