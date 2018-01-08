package com.cttic.cell.phone.signal.pojo;

public class TaskInfo {
	private String oriPath;
	private String oriFileMatcher;
	private short orderFieldIndex;
	private String recordType;

	public TaskInfo(String oriPath, String oriFileMatcher, short orderFieldIndex, String recordType) {
		this.oriPath = oriPath;
		this.oriFileMatcher = oriFileMatcher;
		this.orderFieldIndex = orderFieldIndex;
		this.recordType = recordType;
	}

	public String getOriPath() {
		return oriPath;
	}

	public void setOriPath(String oriPath) {
		this.oriPath = oriPath;
	}

	public String getOriFileMatcher() {
		return oriFileMatcher;
	}

	public void setOriFileMatcher(String oriFileMatcher) {
		this.oriFileMatcher = oriFileMatcher;
	}

	public short getOrderFieldIndex() {
		return orderFieldIndex;
	}

	public void setOrderFieldIndex(short orderFieldIndex) {
		this.orderFieldIndex = orderFieldIndex;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
}
