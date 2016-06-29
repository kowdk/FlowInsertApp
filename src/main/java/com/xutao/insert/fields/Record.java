package com.xutao.insert.fields;

import java.util.ArrayList;

/**
 * 代表文件中每一行记录的类，tablename用于区分sql语句，fields是入库数据
 * 
 * @author xutao
 *
 */
public class Record {
	private String tableName; // 表名
	private ArrayList<Long> fields; // 除了表名和时间戳以外的列

	public Record(){}
	
	public Record(String[] strs) { //构造函数，传入一列split后的数组
		this.tableName = strs[0];
		this.fields = new ArrayList<Long>();
		for (int i = 1; i < strs.length; i++) {
			this.fields.add(Long.parseLong(strs[i]));
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<Long> getFields() {
		return fields;
	}

	public void setFields(ArrayList<Long> fields) {
		this.fields = new ArrayList<Long>(fields);
	}

	@Override
	public String toString() {
		return "Record [tableName=" + tableName + ", fields=" + fields.toString() + "]";
	}
}
