package com.xutao.insert.fields;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Customer 每分钟的单例类
 * @author xutao
 *
 */
public class CustomerRTSingleton {
	private static CustomerRTSingleton instance = null;
	private static Queue<String> customerRTQueue = new ConcurrentLinkedQueue<String>();
	
	private CustomerRTSingleton(){
	}
	
	public static CustomerRTSingleton getInstance(){
		if (instance == null) {
			synchronized (CustomerRTSingleton.class) {
				if(instance == null) {
					instance = new CustomerRTSingleton();
					customerRTQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		return instance;
	}
	
	public static Queue<String> getCustomerRTQueue(){
		return customerRTQueue;
	}
}
