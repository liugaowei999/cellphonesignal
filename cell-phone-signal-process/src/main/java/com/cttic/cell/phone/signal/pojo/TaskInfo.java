package com.cttic.cell.phone.signal.pojo;

public class TaskInfo {
	private String oriPath;
	private String oriFileMatcher;
	private short orderFieldIndex;

	public TaskInfo(String oriPath, String oriFileMatcher, short orderFieldIndex) {
		this.oriPath = oriPath;
		this.oriFileMatcher = oriFileMatcher;
		this.orderFieldIndex = orderFieldIndex;
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

}
