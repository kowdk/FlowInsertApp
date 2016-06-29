package com.xutao.insert.fields;

/**
 * 日志文件类型标识，用于判断启动哪个APP，处理相应类型的文件
 * @author xutao
 *
 */
public enum FileType {
	ROUTER_RT(1),
	ROUTER_MIN(2),
	CUSTOMER_RT(3),
	CUSTOMER_MIN(4),
	GROUP(5);
	
	private int typeId;

	private FileType(int typeId) {
		this.typeId = typeId;
	}

	public int getFileType() {
		return this.typeId;
	}
	
	public void setFileType(int typeId){
		this.typeId = typeId;
	}
}
