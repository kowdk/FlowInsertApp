package com.xutao.insert.tasks;

import java.io.File;

import com.xutao.insert.fields.CustomerMinSingleton;
import com.xutao.insert.fields.CustomerRTSingleton;
import com.xutao.insert.fields.FileType;
import com.xutao.insert.fields.GroupSingleton;
import com.xutao.insert.fields.RouterMinSingleton;
import com.xutao.insert.fields.RouterRTSingleton;

/**
 * 载入监控的文件夹中已有文件,只执行一次,文件队列的生产者，根据dirType不同分发到不同队列
 * 已有文件可能的情况有：
 * 1.程序crash掉后遗留下的没有.ok的txt，需要被处理
 * 2.前端新推送过来的有.ok的txt，再次启动程序时WatchDir无法监测
 * 上述两种情况都需要被处理
 * @author xutao
 *
 */
public class LoadDirTask implements Runnable{
	
	private String dirPath = "";
	private int dirType = 0;

	public LoadDirTask(String dirPath, int dirType) {
		this.dirPath = dirPath;
		this.dirType = dirType;
	}

	public void run() {
		this.monitorDir();
	}

	@SuppressWarnings("static-access")
	private void monitorDir() {
		File rootDir = new File(dirPath);
		File[] files = rootDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			String filePath = files[i].getAbsolutePath(); // 监测到的文件名
			if (filePath.endsWith(".txt") || filePath.contains("ROUTER")) { // 监测到.txt文件
				String okPath = filePath+".ok";
				File okFile = new File(okPath);
				if(okFile.exists()) { // 存在对应的.ok，文件入队列并删除.ok
					if (dirType == FileType.ROUTER_RT.getFileType()) {
						RouterRTSingleton.getInstance().getRouterRTQueue().offer(filePath); // 文件入routerRT队列
					} else if (dirType == FileType.ROUTER_MIN.getFileType()) {
						RouterMinSingleton.getInstance().getRouterMinQueue().offer(filePath); // 文件入routerMin队列
					} else if (dirType == FileType.CUSTOMER_RT.getFileType()) {
						CustomerRTSingleton.getInstance().getCustomerRTQueue().offer(filePath); // 文件入customerRT队列
					} else if (dirType == FileType.CUSTOMER_MIN.getFileType()) {
						CustomerMinSingleton.getInstance().getCustomerMinQueue().offer(filePath); // 文件入customerMin队列
					} else if (dirType == FileType.GROUP.getFileType()) {
						GroupSingleton.getInstance().getGroupQueue().offer(filePath); // 文件入group队列
					} else {
						// 可配置其他文件夹
					}
					okFile.delete(); // 删除.ok
				} else { // 不存在.ok,直接入队列
					if (dirType == FileType.ROUTER_RT.getFileType()) {
						RouterRTSingleton.getInstance().getRouterRTQueue().offer(filePath); // 文件入routerRT队列
					} else if (dirType == FileType.ROUTER_MIN.getFileType()) {
						RouterMinSingleton.getInstance().getRouterMinQueue().offer(filePath); // 文件入routerMin队列
					} else if (dirType == FileType.CUSTOMER_RT.getFileType()) {
						CustomerRTSingleton.getInstance().getCustomerRTQueue().offer(filePath); // 文件入customerRT队列
					} else if (dirType == FileType.CUSTOMER_MIN.getFileType()) {
						CustomerMinSingleton.getInstance().getCustomerMinQueue().offer(filePath); // 文件入customerMin队列
					} else if (dirType == FileType.GROUP.getFileType()) {
						GroupSingleton.getInstance().getGroupQueue().offer(filePath); // 文件入group队列
					} else {
						// 可配置其他文件夹
					}
				}
			}
		}
	}
}
