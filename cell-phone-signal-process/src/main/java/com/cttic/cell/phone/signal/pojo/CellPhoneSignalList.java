package com.cttic.cell.phone.signal.pojo;

import java.util.List;

public class CellPhoneSignalList implements Comparable<CellPhoneSignalList> {
	private long partitionKey;
	private List<CellPhoneSignal> recordList;

	public long getPartitionKey() {
		return partitionKey;
	}

	public void setPartitionKey(long partitionKey) {
		this.partitionKey = partitionKey;
	}

	public List<CellPhoneSignal> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<CellPhoneSignal> recordList) {
		this.recordList = recordList;
	}

	public void insertRecord(CellPhoneSignal cellPhoneSignal) {

	}

	@Override
	public int compareTo(CellPhoneSignalList cellPhoneSignal) {
		if (this.getPartitionKey() == cellPhoneSignal.getPartitionKey()) {
			return 0;
		}
		return this.getPartitionKey() > cellPhoneSignal.getPartitionKey() ? 1 : -1;
	}

}
