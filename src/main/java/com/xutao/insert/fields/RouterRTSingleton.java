package com.xutao.insert.fields;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * router 每一分钟的单例类
 * @author xutao
 *
 */
public class RouterRTSingleton {
	private static RouterRTSingleton instance = null;
	private static Queue<String> routerRTQueue = new ConcurrentLinkedQueue<String>();
	
	private RouterRTSingleton() {
		
	}
	
	public static RouterRTSingleton getInstance(){
		if (instance == null) {
			synchronized (RouterRTSingleton.class) {
				if(instance == null) {
					instance = new RouterRTSingleton();
					routerRTQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		return instance;
	}
	
	public static Queue<String> getRouterRTQueue(){
		return routerRTQueue;
	}
}
