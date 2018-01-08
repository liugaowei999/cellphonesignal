package com.cttic.cell.phone.signal.pojo;

public class CellPhoneSignal implements Comparable<CellPhoneSignal> {
	private int id;
	private String record;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRecord() {
		return record;
	}

	public void setRecord(String record) {
		this.record = record;
	}

	@Override
	public int compareTo(CellPhoneSignal cellPhoneSignal) {
		if (this.getId() == cellPhoneSignal.getId()) {
			return 0;
		}
		return this.getId() > cellPhoneSignal.getId() ? 1 : -1;
	}

}
