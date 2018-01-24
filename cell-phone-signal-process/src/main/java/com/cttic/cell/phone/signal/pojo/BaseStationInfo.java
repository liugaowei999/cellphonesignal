package com.cttic.cell.phone.signal.pojo;

import java.io.Serializable;

public class BaseStationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int CID; // ��վ���
	private int LAC; // С����ʶ
	private String cType; // 2��2G,4��4G
	private String cell_name; // ��վ����
	private String azimuth; // ��λ��
	private String longitude; // ����
	private String latitude; // γ��

	@Override
	public String toString() {
		return CID + "|" + LAC + "|" + cType + "|" + cell_name + "|" + azimuth + "|" + longitude + "|" + latitude;
	}

	public int getCID() {
		return CID;
	}

	public void setCID(int cID) {
		CID = cID;
	}

	public int getLAC() {
		return LAC;
	}

	public void setLAC(int lAC) {
		LAC = lAC;
	}

	public String getcType() {
		return cType;
	}

	public void setcType(String cType) {
		this.cType = cType;
	}

	public String getCell_name() {
		return cell_name;
	}

	public void setCell_name(String cellName) {
		this.cell_name = cellName;
	}

	public String getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(String azimuth) {
		this.azimuth = azimuth;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	// ���л��� �����л�

}
