package com.xutao.insert.tasks;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.GlobalSettings;
import com.xutao.insert.fields.Record;
import com.xutao.insert.fields.RouterMinSingleton;
import com.xutao.insert.utils.FileProcesser;

/**
 * Router五分钟队列的消费者
 * @author xutao
 *
 */
public class RouterMinConsumer implements Runnable{

	private Logger logger = Logger.getLogger(RouterMinConsumer.class);
	private Map<String, List<Record>> map = null;
	
	public RouterMinConsumer(){
		this.map = new HashMap<String, List<Record>>();
	}
	
	@SuppressWarnings("static-access")
	public void run() {
		while(true) {
			try {
				if (!RouterMinSingleton.getInstance().getRouterMinQueue().isEmpty()) { // 如果文件队列不为空
					String filePath = RouterMinSingleton.getInstance().getRouterMinQueue().poll();
					logger.info("processing file = " + filePath);
					
					List<Record> records = FileProcesser.loadRecords(filePath);// 将文件解析成Record列表
					if(records.size() == 0) {
						logger.error(filePath + " has no records...");
						new File(filePath).delete();
						continue;
					}
					this.map = FileProcesser.clusterRecords(records);//根据list读入map
					FileProcesser.dumpRecordClusters(this.map);// 读取record簇，调用入库接口
					FileProcesser.moveFileToStat(filePath, GlobalSettings.STAT_ROUTER_HOUR);//MIN需要备份到stat文件夹，并生成.ok
					
				} else { // 如果文件队列为空则等待30s
					Thread.sleep(GlobalSettings.SLEEP_TIME_MIN);
				}
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

}
