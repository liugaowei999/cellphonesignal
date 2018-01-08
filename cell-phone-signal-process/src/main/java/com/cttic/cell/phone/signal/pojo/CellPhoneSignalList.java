package com.cttic.cell.phone.signal.pojo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CellPhoneSignalList implements Comparable<CellPhoneSignalList> {
	private long partitionKey;
	private List<CellPhoneSignal> recordList = new ArrayList<>();

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
		recordList.add(cellPhoneSignal);
	}

	@Override
	public int compareTo(CellPhoneSignalList cellPhoneSignal) {
		if (this.getPartitionKey() == cellPhoneSignal.getPartitionKey()) {
			return 0;
		}
		return this.getPartitionKey() > cellPhoneSignal.getPartitionKey() ? 1 : -1;
	}

	/**
	 * ≈≈–Ú
	 */
	public void doSort() {
		Collections.sort(recordList);
	}
	
	public void writeToFile(FileWriter fw) throws IOException {
		doSort();
		for (CellPhoneSignal cellPhoneSignal : recordList) {
			fw.write(cellPhoneSignal.getRecord() + "\r\n");
		}
	}

}
