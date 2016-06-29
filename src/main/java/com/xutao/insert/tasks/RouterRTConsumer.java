package com.xutao.insert.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.GlobalSettings;
import com.xutao.insert.fields.Record;
import com.xutao.insert.fields.RouterRTSingleton;
import com.xutao.insert.utils.FileProcesser;

public class RouterRTConsumer implements Runnable{

	private Logger logger = Logger.getLogger(RouterRTConsumer.class);
	private Map<String, List<Record>> map = null;
	
	public RouterRTConsumer(){
		this.map = new HashMap<String, List<Record>>();
	}
	
	@SuppressWarnings("static-access")
	public void run() {
		while(true) {
			try {
				if (!RouterRTSingleton.getInstance().getRouterRTQueue().isEmpty()) { // 如果文件队列不为空
					String filePath = RouterRTSingleton.getInstance().getRouterRTQueue().poll();
					logger.info("processing file = " + filePath);
					
					List<Record> records = FileProcesser.loadRecords(filePath);// 将文件解析成Record列表
					if(records.size() == 0) {
						logger.error(filePath + " has no records...");
						new File(filePath).delete();
						continue;
					}
					this.map = FileProcesser.clusterRecords(records);//根据list读入map
					
					//FileProcesser.printMapEntrys(this.map); //打印map元素,用于调试
					
					FileProcesser.dumpRecordClusters(this.map);// 读取record簇，调用入库接口
					
					FileProcesser.moveFileToBak(filePath, GlobalSettings.BAK_ROUTER_SRC);//RT不需要后续处理，直接备份
					
				} else { // 如果文件队列为空则等待5s
					Thread.sleep(GlobalSettings.SLEEP_TIME_RT);
				}
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}
	
	
}
