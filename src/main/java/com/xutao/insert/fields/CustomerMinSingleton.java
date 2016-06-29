package com.xutao.insert.fields;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Customer 每五分钟的单例类，其中单例队列保存文件路径，线程安全
 * @author xutao
 *
 */
public class CustomerMinSingleton {
	private static CustomerMinSingleton instance = null; // 单例类对象
	private static Queue<String> customerMinQueue = new ConcurrentLinkedQueue<String>(); // 存放文件的队列
	
	private CustomerMinSingleton(){
	}
	
	public static CustomerMinSingleton getInstance(){
		if (instance == null) {
			synchronized (CustomerMinSingleton.class) {
				if(instance == null) {
					instance = new CustomerMinSingleton();
					customerMinQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		return instance;
	}
	
	public static Queue<String> getCustomerMinQueue(){
		return customerMinQueue;
	}
}
