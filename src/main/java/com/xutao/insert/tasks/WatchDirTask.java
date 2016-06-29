package com.xutao.insert.tasks;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.CustomerMinSingleton;
import com.xutao.insert.fields.CustomerRTSingleton;
import com.xutao.insert.fields.FileType;
import com.xutao.insert.fields.GroupSingleton;
import com.xutao.insert.fields.RouterMinSingleton;
import com.xutao.insert.fields.RouterRTSingleton;

/**
 * 文件夹监控程序，死循环监控新产生的文件，采用事件触发WatchService，文件队列的生产者，根据dirType分发到不同队列
 * @author xutao
 *
 */
public class WatchDirTask implements Runnable{
	private Logger logger = Logger.getLogger(WatchDirTask.class);
	
	private String dirPath = "";
	private int dirType = 0;
	
	public WatchDirTask(String dirPath, int dirType){
		this.dirPath = dirPath;
		this.dirType = dirType;
	}

	@SuppressWarnings({ "static-access", "rawtypes" })
	public void run() {
		try {
			WatchService watchService = FileSystems.getDefault().newWatchService();
			Path path = Paths.get(dirPath);
		    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
			while (true) {
				try{
					WatchKey key = watchService.take();
					List<WatchEvent<?>> watchEvents = key.pollEvents();
					for (WatchEvent<?> watchEvent : watchEvents) {
						try{
							if(watchEvent.context()==null){
								continue;
							}
							String changedFileName = watchEvent.context().toString();
							WatchEvent.Kind kind = watchEvent.kind();
							if (kind == StandardWatchEventKinds.ENTRY_CREATE) {// 说明当前文件已经写好并转移到了相应的rst目录下
								if (changedFileName.endsWith(".ok")) {
									String realFilePath = dirPath + changedFileName.substring(0, changedFileName.length() - 3);
									if(dirType == FileType.ROUTER_RT.getFileType()){
										RouterRTSingleton.getInstance().getRouterRTQueue().offer(realFilePath); // 文件入routerRT队列
									} else if(dirType == FileType.ROUTER_MIN.getFileType()) {
										RouterMinSingleton.getInstance().getRouterMinQueue().offer(realFilePath); // 文件入routerMin队列
									} else if(dirType == FileType.CUSTOMER_RT.getFileType()) {
										CustomerRTSingleton.getInstance().getCustomerRTQueue().offer(realFilePath); // 文件入customerRT队列
									} else if(dirType == FileType.CUSTOMER_MIN.getFileType()) {
										CustomerMinSingleton.getInstance().getCustomerMinQueue().offer(realFilePath); // 文件入customerMin队列
									} else if(dirType == FileType.GROUP.getFileType()) {
										GroupSingleton.getInstance().getGroupQueue().offer(realFilePath); // 文件入group队列
									} else {
										//可添加类型
									}
									new File(dirPath + changedFileName).delete(); // 删除.ok，不重复监测
								}
							}
						}catch(Exception e){
							logger.error(e);
						}
					}
					boolean valid = key.reset();
					if (!valid) {
						logger.info("key.reset() ERROR !");
					}
				}catch(Exception e){
					logger.error(e);
				}
			}
		} catch (Exception e) {
			 logger.error(e);
		}

	}
}
