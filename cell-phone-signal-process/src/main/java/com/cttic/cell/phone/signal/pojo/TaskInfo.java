package com.cttic.cell.phone.signal.pojo;

import java.util.HashMap;
import java.util.Map;

import com.cttic.cell.phone.signal.condition.ICondition;
import com.cttic.cell.phone.signal.condition.SimpleCondition;
import com.cttic.cell.phone.signal.utils.CastUtil;

public class TaskInfo {
	private String oriPath;
	private String oriFileMatcher;
	private String dateFieldInfo;
	private String recordType;
	private int maxFileCount;
	private Map<ICondition, String[]> outPutFiledsConditionMap = new HashMap<>();
	private String outSplitChar;
	private Map<String, Integer> filedIndexMap = new HashMap<String, Integer>();

	public TaskInfo(String oriPath, String oriFileMatcher, String recordType) {
		this.oriPath = oriPath;
		this.oriFileMatcher = oriFileMatcher;
		this.recordType = recordType;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("recordType=" + recordType).append(", oriPath=" + oriPath)
				.append(", dateFieldInfo=" + dateFieldInfo).append(", outSplitChar=" + outSplitChar);
		return stringBuilder.toString();
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

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public void setOutPutFiledsConditionMap(String outPutFiledsIndex) {
		// outPutFieldsIndex = {calltype}=6:{imsi1},{msisdn1},{cid},{lac},{LON},{LAT},{time1},{calltype};{calltype}=7:{imsi1},{msisdn},{cid},{lac},{LON},{LAT},{motime},{calltype}
		String[] ConditionFormats = null;
		if (outPutFiledsIndex.contains(";")) {
			ConditionFormats = outPutFiledsIndex.split(";");
		} else {
			ConditionFormats = new String[] { outPutFiledsIndex };
		}

		for (String conditionFormatStr : ConditionFormats) {
			// {calltype}=6:{imsi1},{msisdn1},{cid},{lac},{LON},{LAT},{time1},{calltype}

			String conditionStr = "true"; // 如果没有配置条件，默认为true
			String outputFields = null;
			if (conditionFormatStr.contains(":")) {
				conditionStr = conditionFormatStr.split(":")[0];
				outputFields = conditionFormatStr.split(":")[1];
			} else {
				outputFields = conditionFormatStr;
			}
			ICondition condition = new SimpleCondition();
			condition.installCondExpression(conditionStr);
			outPutFiledsConditionMap.put(condition, outputFields.split(","));
		}

	}

	public Map<ICondition, String[]> getOutPutFiledsConditionMap() {
		return outPutFiledsConditionMap;
	}

	public String getDateFieldInfo() {
		return dateFieldInfo;
	}

	public void setDateFieldInfo(String dateFieldInfo) {
		this.dateFieldInfo = dateFieldInfo;
	}

	public String getOutSplitChar() {
		return outSplitChar;
	}

	public void setOutSplitChar(String outSplitChar) {
		this.outSplitChar = outSplitChar;
	}

	public Map<String, Integer> getFiledIndexMap() {
		return filedIndexMap;
	}

	/**
	 * 加载记录格式信息配置
	 * 
	 * @param fileIndexStr
	 * @return true : 成功 false：失败
	 */
	public boolean setFiledIndexMap(String fileIndexStr) {
		//fileIndexStr: fieldIndexMap=cid:7,lac:6,ctype:8,calltype:3,imsi1:9,msisdn:4,time1:1
		String[] fileds = fileIndexStr.split(",");
		for (String filed : fileds) {
			String filedName = filed.split(":")[0];
			int filedIndex = CastUtil.castInt(filed.split(":")[1], -1);
			if (filedIndex == -1) {
				System.out.println("castInt error");
				return false;
			}
			filedIndexMap.put(filedName, filedIndex);
		}
		if (!filedIndexMap.containsKey("time1") && !filedIndexMap.containsKey("time2")) {
			System.out.println("no time");
			return false;
		}
		return true;
	}

	public int getMaxFileCount() {
		return maxFileCount;
	}

	public void setMaxFileCount(int maxFileCount) {
		this.maxFileCount = maxFileCount;
	}

}
