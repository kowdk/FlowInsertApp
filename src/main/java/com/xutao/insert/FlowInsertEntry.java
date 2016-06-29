package com.xutao.insert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.xutao.insert.fields.FileType;
import com.xutao.insert.fields.GlobalSettings;
import com.xutao.insert.tasks.CustomerMinConsumer;
import com.xutao.insert.tasks.CustomerRTConsumer;
import com.xutao.insert.tasks.GroupConsumer;
import com.xutao.insert.tasks.LoadDirTask;
import com.xutao.insert.tasks.RouterMinConsumer;
import com.xutao.insert.tasks.RouterRTConsumer;
import com.xutao.insert.tasks.WatchDirTask;

/**
 * 程序入口点，各个部分充分冗余，将业务故障可能降低，单例文件队列，一写多读
 * @author xutao
 *
 */
public class FlowInsertEntry {
	
	private Logger logger = Logger.getLogger(FlowInsertEntry.class);
	
	public FlowInsertEntry(){
		startRouterRT();
		startRouterMin();
		startCustomerRT();
		startCustomerMin();
		startGroup();
	}
	
	private void startGroup(){
		ExecutorService executor = Executors.newFixedThreadPool(GlobalSettings.THREAD_POOL_SIZE);
		executor.submit(new LoadDirTask(GlobalSettings.SRC_GROUP_PATH, FileType.GROUP.getFileType()));
		executor.submit(new WatchDirTask(GlobalSettings.SRC_GROUP_PATH, FileType.GROUP.getFileType()));
		for(int i = 0; i < GlobalSettings.CONSUMER_SIZE; i++) {
			executor.submit(new GroupConsumer());
		}
		
		executor.shutdown();
		logger.info("group started...");
	}
	
	private void startCustomerMin(){
		ExecutorService executor = Executors.newFixedThreadPool(GlobalSettings.THREAD_POOL_SIZE);
		executor.submit(new LoadDirTask(GlobalSettings.SRC_CUSTOMER_MIN_PATH, FileType.CUSTOMER_MIN.getFileType()));
		executor.submit(new WatchDirTask(GlobalSettings.SRC_CUSTOMER_MIN_PATH, FileType.CUSTOMER_MIN.getFileType()));
		for(int i = 0; i < GlobalSettings.CONSUMER_SIZE; i++) {
			executor.submit(new CustomerMinConsumer());
		}
		
		executor.shutdown();
		logger.info("customer min started...");
	}
	
	private void startCustomerRT(){
		ExecutorService executor = Executors.newFixedThreadPool(GlobalSettings.THREAD_POOL_SIZE);
		executor.submit(new LoadDirTask(GlobalSettings.SRC_CUSTOMER_RT_PATH, FileType.CUSTOMER_RT.getFileType()));
		executor.submit(new WatchDirTask(GlobalSettings.SRC_CUSTOMER_RT_PATH, FileType.CUSTOMER_RT.getFileType()));
		for(int i = 0; i < GlobalSettings.CONSUMER_SIZE; i++) {
			executor.submit(new CustomerRTConsumer());
		}
		
		executor.shutdown();
		logger.info("customer rt started...");
	}
	
	private void startRouterRT(){
		ExecutorService executor = Executors.newFixedThreadPool(GlobalSettings.THREAD_POOL_SIZE);
		executor.submit(new LoadDirTask(GlobalSettings.SRC_ROUTER_RT_PATH, FileType.ROUTER_RT.getFileType()));
		executor.submit(new WatchDirTask(GlobalSettings.SRC_ROUTER_RT_PATH, FileType.ROUTER_RT.getFileType()));
		for(int i = 0; i < GlobalSettings.CONSUMER_SIZE; i++) {
			executor.submit(new RouterRTConsumer());
		}
		
		executor.shutdown();
		logger.info("router rt started...");
	}
	
	private void startRouterMin(){
		ExecutorService executor = Executors.newFixedThreadPool(GlobalSettings.THREAD_POOL_SIZE);
		executor.submit(new LoadDirTask(GlobalSettings.SRC_ROUTER_MIN_PATH, FileType.ROUTER_MIN.getFileType()));
		executor.submit(new WatchDirTask(GlobalSettings.SRC_ROUTER_MIN_PATH, FileType.ROUTER_MIN.getFileType()));
		for(int i = 0; i < GlobalSettings.CONSUMER_SIZE; i++) {
			executor.submit(new RouterMinConsumer());
		}
		
		executor.shutdown();
		logger.info("router min started...");
	}
	
	public static void main(String[] args) {
		new FlowInsertEntry();
	}
}
