package com.xutao.insert.fields;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Router 每五分钟的单例类
 * @author xutao
 *
 */
public class RouterMinSingleton {
	
	private static RouterMinSingleton instance = null;
	private static Queue<String> routerMinQueue = new ConcurrentLinkedQueue<String>();
	
	private RouterMinSingleton(){
		
	}
	
	public static RouterMinSingleton getInstance(){
		if (instance == null) {
			synchronized (RouterMinSingleton.class) {
				if(instance == null) {
					instance = new RouterMinSingleton();
					routerMinQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		return instance;
	}
	
	public static Queue<String> getRouterMinQueue(){
		return routerMinQueue;
	}
}
