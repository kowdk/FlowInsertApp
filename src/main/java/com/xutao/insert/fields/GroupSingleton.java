package com.xutao.insert.fields;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Group 单例类
 * @author xutao
 *
 */
public class GroupSingleton {
	private static GroupSingleton instance = null;
	private static Queue<String> groupQueue = new ConcurrentLinkedQueue<String>();
	
	private GroupSingleton(){
		
	}
	
	public static GroupSingleton getInstance(){
		
		if(instance == null) {
			synchronized(GroupSingleton.class) {
				if(instance == null) {
					instance = new GroupSingleton();
					groupQueue = new ConcurrentLinkedQueue<String>();
				}
			}
		}
		
		return instance;
	}
	
	public static Queue<String> getGroupQueue(){
		return groupQueue;
	}
}
