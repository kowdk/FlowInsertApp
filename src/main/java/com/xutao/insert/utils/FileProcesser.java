package com.xutao.insert.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.GlobalSettings;
import com.xutao.insert.fields.Record;

/**
 * 文件处理工具类
 * 
 * @author xutao
 *
 */
public class FileProcesser {

	private static Logger logger = Logger.getLogger(FileProcesser.class);
	private static SqlLoader sqlLoader = new SqlLoader();
	private static ConfLoader confLoader = new ConfLoader();//第一次读取配置载入内存，并删除配置文件和.ok文件

	public FileProcesser(){
		
	}
	
	/**
	 * 将map中的各个list解析并入库
	 * @param map
	 */
	public static void dumpRecordClusters(Map<String, List<Record>> map){
		for(String tableName : map.keySet()) {
			List<Record> list = map.get(tableName);
			if(sqlLoader == null) {
				sqlLoader = new SqlLoader();
			}
			
			String suffix = tableName.substring(tableName.lastIndexOf("_")+1, tableName.length());
			String prefix = tableName.substring(0, tableName.lastIndexOf("_"));
			String insertSql = sqlLoader.searchSql(prefix).replace("XXX", suffix);
			if(insertSql == null) {
				logger.error("insertSql has not been found...");
			}
			
			logger.info("size = " + list.size() + "; insertSql = " + insertSql);
			if(list.size() <= GlobalSettings.BATCH_SIZE) { // record的数量小于batch，直接入库
				//logger.info("size = " + list.size() + "; insertSql = " + insertSql);
				OscarDao.batchInsert(list, insertSql);
			} else { // record的数量大于batch，分批入库，每批batch个
				int num = GlobalSettings.BATCH_SIZE, i = 0;
				for(; i < list.size() / num; i++) {
					List<Record> tmp = list.subList(i * num, (i+1) * num);
					//logger.info("size = " + tmp.size() + "; insertSql = " + insertSql);
					OscarDao.batchInsert(tmp, insertSql);
				}
				if(i * num < list.size()) {
					List<Record> tmp = list.subList(i * num, list.size() - 1);
					//logger.info("size = " + (list.size() - i * num) + "; insertSql = " + insertSql);
					OscarDao.batchInsert(tmp, insertSql);
				}
			}
		}
	}
	
	/**
	 * 将文件解析成record队列
	 * @param filePath
	 * @return
	 */
	public static List<Record> loadRecords(String filePath) {
		ArrayList<Record> fileRecords = new ArrayList<Record>();
		if(confLoader.hasNewConf()) { // 如果检测到.ok文件，就动态重新加载
			confLoader = new ConfLoader(); 
		}
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(filePath)));
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] fields = line.split("\t");
				
				/*if(fields[0].startsWith("ROUTER_STAT")){
					continue;
				}*/
				
				// fields[2]是网络字节序的IP地址，需要读取配置转换为router_id.
				// Router是fields[2], Customer是fields[3]
				if(fields[0].startsWith("ROUTER")){
					String router_id = confLoader.searchRouterID(fields[2]);
					if(router_id == null){
						//logger.error("no router_id found!!");
						router_id = "10111";
					}
					fields[2] = router_id;
				}
				if(fields[0].startsWith("CUSTOMER")) {
					String router_id = confLoader.searchRouterID(fields[3]);
					if(router_id == null){
						//logger.error("no router_id found!!");
						router_id = "10111";
					}
					fields[3] = router_id;
				}
				
				//logger.info(Arrays.toString(fields));
				Record record = new Record(fields);
				fileRecords.add(record);
			}
		} catch (IOException e) {
			logger.error(e);
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return fileRecords;
	}

	/**
	 * 将record列表根据tableName聚类
	 * 
	 * @param list
	 * @return
	 */
	public static Map<String, List<Record>> clusterRecords(List<Record> records) {
		Map<String, List<Record>> map = new HashMap<String, List<Record>>();
		for (Record r : records) { // 对于每一个记录
			String tableName = r.getTableName();
			if (map.containsKey(tableName)) { // 如果包含该表名
				map.get(tableName).add(r);
			} else { // 不包含该表名
				List<Record> list = new ArrayList<Record>();
				list.add(r);
				map.put(tableName, list);
			}
		}
		return map;
	}
	
	/**
	 * 打印map元素
	 * @param map
	 */
	public static void printMapEntrys(Map<String, List<Record>> map){
		if(!map.isEmpty()){
			for(String tableName : map.keySet()) {
				List<Record> list = map.get(tableName);
				//logger.info("list size == " + list.size());
			}
		} else {
			logger.error("map is empty...");
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param src
	 * @param dst
	 */
	public static void copyFile(String src, String dst) {
		FileChannel srcChannel = null;
		FileChannel dstChannel = null;
		try {
			srcChannel = new FileInputStream(new File(src)).getChannel();
			dstChannel = new FileOutputStream(new File(dst)).getChannel();
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
		} catch (IOException e1) {
			logger.error(e1);
		} finally {
			try {
				dstChannel.close();
				srcChannel.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
	}

	/**
	 * 将文件移动到需要合并的文件夹
	 * 
	 * @param filePath
	 * @param dstDirPath
	 */
	public static void moveFileToStat(String filePath, String dstDirPath) {
		//logger.info("statFilePath = " + filePath + "; statDstDirPath = " + dstDirPath);
		String nameOnly = filePath.substring(filePath.lastIndexOf('/') + 1); // timestamp.txt
		File txtFile = new File(filePath);
		String newPath = dstDirPath + nameOnly;
		logger.info("statfilePath = " + filePath + "; statdstDirPath = " + newPath);
		File newFile = new File(newPath);
		if(newFile.exists()) {
			newFile.delete();
			logger.error(dstDirPath + nameOnly + " has already exists...");
		}
		txtFile.renameTo(new File(newPath)); // dstDirPath/timestamp.txt

		File okFile = new File(newPath + ".ok");
		try {
			if(!okFile.exists()) {
				okFile.createNewFile();// dstDirPath/timestamp.txt.ok
			}
		} catch (IOException e) {
			logger.error(e);
		} 
	}

	/**
	 * 将文件移动到备份文件夹
	 * 
	 * @param filePath
	 * @param dstDirPath
	 */
	public static void moveFileToBak(String filePath, String dstDirPath) {
		logger.info("bakFilePath = " + filePath + "; bakDstDirPath = " + dstDirPath);
		String nameOnly = filePath.substring(filePath.lastIndexOf('/') + 1); // timestamp.txt
		File txtFile = new File(filePath);
		File dstFile = new File(dstDirPath + nameOnly);
		if(dstFile.exists()){
			dstFile.delete();
			logger.error(dstDirPath + nameOnly + " has been already exists...");
		}
		txtFile.renameTo(dstFile); // dstDirPath/timestamp.txt
	}

}
