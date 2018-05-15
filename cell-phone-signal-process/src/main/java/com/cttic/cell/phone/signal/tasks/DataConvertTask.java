package com.cttic.cell.phone.signal.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cttic.cell.phone.signal.condition.ICondition;
import com.cttic.cell.phone.signal.configure.LoadBaseStationInfo;
import com.cttic.cell.phone.signal.configure.LoadConfigure;
import com.cttic.cell.phone.signal.pojo.BaseStationInfo;
import com.cttic.cell.phone.signal.pojo.CellPhoneSignal;
import com.cttic.cell.phone.signal.pojo.CellPhoneSignalList;
import com.cttic.cell.phone.signal.pojo.TaskInfo;
import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.CollectionUtil;
import com.cttic.cell.phone.signal.utils.GzipException;
import com.cttic.cell.phone.signal.utils.StringUtil;
import com.cttic.cell.phone.signal.utils.zip.FileUtil;
import com.cttic.cell.phone.signal.utils.zip.GZipUtils;

public class DataConvertTask implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataConvertTask.class);

	private static final String RECORD = "RECORD";
	private static final String ORDERVALUES = "ORDERVALUES";

	// ������Ϣ
	LoadConfigure configure;

	private boolean isStop;

	// �����ļ�Ŀ¼
	private List<TaskInfo> taskInfos;

	// ��¼���е�partition��Ϣ
	private HashMap<Long, CellPhoneSignalList> recordMap;

	public DataConvertTask(LoadConfigure configure) {
		this.configure = configure;
		List<TaskInfo> taskInfos = this.configure.getTaskList();
		this.taskInfos = taskInfos;
	}

	/**
	 * ��ȡһ���ļ����Լ������ļ����µ����е��ļ�
	 */
	private Map<TaskInfo, List<File>> getAllFiles() {
		Map<TaskInfo, List<File>> allFiles = new HashMap<>();
		int fileCount = 0, taskfileCount = 0;
		try {
			for (TaskInfo task : taskInfos) {
				taskfileCount = 0;
				List<File> files = FileUtil.getFiles(task.getOriPath());
				// ���ļ��б��������
				Collections.sort(files, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						String fileName1 = o1.getName();
						String fileName2 = o2.getName();
						String datetime1 = fileName1.substring(fileName1.lastIndexOf("_") + 1,
								fileName1.lastIndexOf("."));
						String datetime2 = fileName2.substring(fileName2.lastIndexOf("_") + 1,
								fileName2.lastIndexOf("."));

						// ����ļ�������û������
						if (StringUtils.isEmpty(datetime1) || StringUtils.isEmpty(datetime2)) {
							return o1.lastModified() > o2.lastModified() ? 1 : -1;
						} else {
							return datetime1.compareTo(datetime2) > 0 ? 1 : -1;
						}
					}
				});

				for (File f : files) {
					if (f.getName().matches(task.getOriFileMatcher())) {
						fileCount++;
						taskfileCount++;
						if (allFiles.containsKey(task)) {
							allFiles.get(task).add(f);
						} else {
							List<File> currentTaskFiles = new ArrayList<>();
							currentTaskFiles.add(f);
							allFiles.put(task, currentTaskFiles);
						}
						if (taskfileCount >= task.getMaxFileCount()) {
							break;
						}
					}
				}
			}
		} catch (PatternSyntaxException e) {
			LOGGER.error("Configure error : [fileMatcher]", e);
			throw new RuntimeException(e);
		}
		LOGGER.debug("Fetched file count:" + fileCount);
		return allFiles;
	}

	@Override
	public void run() {
		isStop = false;
		//System.out.println("Thread will start .... ");
		while (!isStop && !Thread.currentThread().isInterrupted()) {
			//System.out.println("Thread start , processing ........ ");

			//��һ������ȡһ���ļ����µ����е��ļ�
			Map<TaskInfo, List<File>> allFiles = getAllFiles();
			if (CollectionUtil.isNotEmpty(allFiles)) {

				recordMap = new HashMap<>();

				// ���������ļ��б�
				for (Map.Entry<TaskInfo, List<File>> entry : allFiles.entrySet()) {
					processTaskFiles(entry.getKey(), entry.getValue());
				}

				// д����ǰ���ε����м�¼ �� ����
				try {
					writeRecordsToFile(recordMap);
					backupAllFiles(allFiles);
				} catch (IOException e) {
					LOGGER.error("write the records to the file failure!", e);
					isStop = true;
				} catch (GzipException e) {
					LOGGER.error("Gzip the bakcup file failure!", e);
				}
			}

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				isStop = true;
				Thread.currentThread().interrupt();
				//break;
			}
		}
	}

	private void backupAllFiles(Map<TaskInfo, List<File>> allFiles) throws GzipException {
		// ���������ļ��б�
		for (Map.Entry<TaskInfo, List<File>> entry : allFiles.entrySet()) {
			List<File> value = entry.getValue();
			for (File file : value) {
				String bakfilepath = configure.getBakPath() + file.getName();
				if (FileUtil.rename(file, new File(bakfilepath))) {
					LOGGER.debug("File:[" + file.getAbsolutePath() + "] backup to [" + bakfilepath + "] success.");
				} else {
					LOGGER.error("File:[" + file.getAbsolutePath() + "] backup to [" + bakfilepath + "] failure.");
					throw new RuntimeException("Rename file to bak dirctory failure!");
				}
				if (configure.isCompress()) {
					//System.out.println("compress ........");
					// ѹ����ɾ��ԭʼ�ļ�
					GZipUtils.compress(new File(bakfilepath), true);
				}
			}
		}
	}

	private void writeRecordsToFile(HashMap<Long, CellPhoneSignalList> recordMap) throws IOException {
		// HashMap ����
		List<Map.Entry<Long, CellPhoneSignalList>> infoIds = new ArrayList<Map.Entry<Long, CellPhoneSignalList>>(
				recordMap.entrySet());
		//		System.out.println("============================= ����ǰ��");
		//		for (Entry<Long, CellPhoneSignalList> entry : infoIds) {
		//			System.out.println(entry.getKey());
		//		}

		// ����
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, CellPhoneSignalList>>() {
			@Override
			public int compare(Map.Entry<Long, CellPhoneSignalList> o1, Map.Entry<Long, CellPhoneSignalList> o2) {
				if (o2.getKey() == o1.getKey()) {
					return 0;
				}
				return o1.getKey() > o2.getKey() ? 1 : -1;
			}
		});

		//System.out.println("============================= �����");
		FileWriter fw = null;
		String fileNameBody = Thread.currentThread().getId() + "_" + System.currentTimeMillis();
		String tmpFilePath = configure.getTmpPath() + configure.getTmpFilePre() + fileNameBody
				+ configure.getTmpFileSub();
		String outputFilePath = configure.getOutPutPath() + configure.getOutputFilePre() + fileNameBody
				+ configure.getOutputFileSub();
		if (CollectionUtil.isNotEmpty(infoIds)) {
			fw = new FileWriter(tmpFilePath);
		}
		for (Entry<Long, CellPhoneSignalList> entry : infoIds) {
			//System.out.println(entry.getKey());
			CellPhoneSignalList recordList = entry.getValue();
			recordList.writeToFile(fw);
		}
		fw.flush();
		fw.close();

		// �Ƶ���ʽĿ¼��
		// System.out.println("rename to out path");
		File file = new File(tmpFilePath);
		if (FileUtil.rename(file, new File(outputFilePath))) {
			LOGGER.debug("File:[" + tmpFilePath + "] rename to [" + outputFilePath + "] success.");
		} else {
			LOGGER.error("File:[" + tmpFilePath + "] rename to [" + outputFilePath + "] failure.");
		}
	}

	/**
	 * ����ǰ����������ļ�
	 * 
	 * @param taskInfo
	 * @param files
	 */
	private void processTaskFiles(TaskInfo taskInfo, List<File> files) {
		for (File f : files) {
			processFile(taskInfo, f);
		}
	}

	/**
	 * �������ļ�
	 * 
	 * @param taskInfo
	 * @param f
	 */
	private void processFile(TaskInfo taskInfo, File f) {
		try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(f)); // , "GB2312"
				BufferedReader bufferedReader = new BufferedReader(fileReader);) {

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				Map<String, String[]> outputRecMap = getOutputRecord(taskInfo, line);
				if (CollectionUtil.isNotEmpty(outputRecMap)) {
					if (StringUtil.isNotEmpty(outputRecMap.get(RECORD)[0])) {
						intsertToContainer(outputRecMap.get(ORDERVALUES), outputRecMap.get(RECORD)[0]);
					}
				}

			}
		} catch (Exception e) {
			LOGGER.error("Process file=[" + f.getAbsolutePath() + "] error!", e);
			throw new RuntimeException(e);
		}
	}

	private Map<String, String[]> getOutputRecord(TaskInfo taskInfo, String line) {
		Map<String, String[]> returnMap = new HashMap<String, String[]>();
		String oriTime1 = null, oriTime2 = null, orderTime = null;
		// ��ǰ��������Ҫ�����������¼��ʽ����
		Map<ICondition, String[]> outPutFiledsConditionMap = taskInfo.getOutPutFiledsConditionMap();
		// ԭʼ��¼�ֶ��б�����
		Map<String, Integer> filedIndexMap = taskInfo.getFiledIndexMap();
		// ����ԭʼ��¼�ֶ����ƺ��ֶ�ֵ��key-value�б�
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		String[] oriRec = line.split(",");

		for (Map.Entry<String, Integer> entry : filedIndexMap.entrySet()) {
			String fieldName = entry.getKey();
			int filedIndex = entry.getValue();
			String fieldValue = oriRec[filedIndex - 1];
			if (fieldName.equalsIgnoreCase("time1")) {
				oriTime1 = fieldValue;
			}
			if (fieldName.equalsIgnoreCase("time2")) {
				oriTime2 = fieldValue;
			}

			if (fieldName.equalsIgnoreCase("time1") || fieldName.equalsIgnoreCase("time2")) {
				fieldValue = formatTime(taskInfo, oriRec[filedIndex - 1]);
				if (StringUtil.isEmpty(fieldValue)) {
					LOGGER.debug("Time format is error! record=[" + line + "]");
					return returnMap;
				}
			}

			fieldValueMap.put("{" + fieldName + "}", fieldValue);
		}

		// ��ԭʼ�ֶ��л�ȡcid, lac, cType, callType��Ϣ��������fieldValueMap��
		String cid, lac, cType, callType;
		cid = fieldValueMap.get("{cid}");
		lac = fieldValueMap.get("{lac}");
		cType = fieldValueMap.get("{ctype}");
		callType = fieldValueMap.get("{calltype}");
		if (cType.equalsIgnoreCase("111")) {
			cType = "2"; // 2G
		} else {
			cType = "4"; // 4G
		}

		// ��ȡ��γ��λ����Ϣ�����浽fieldValueMap��
		BaseStationInfo stationInfo = LoadBaseStationInfo.find(cid, lac, cType);
		if (stationInfo == null) {
			LOGGER.debug("Get Lon,Lat failed! record=[" + line + "], cid=" + cid + ", lac=" + lac + ",cType=" + cType);
			return returnMap;
		}
		fieldValueMap.put("{LON}", stationInfo.getLongitude());
		fieldValueMap.put("{LAT}", stationInfo.getLatitude());

		StringBuilder stringBuilder = new StringBuilder();

		String[] outPutFiledsIndexArray = null;
		// ���������жϣ� ȷ����������ļ�¼��ʽ
		for (Map.Entry<ICondition, String[]> entry : outPutFiledsConditionMap.entrySet()) {
			ICondition condition = entry.getKey();
			if (condition.isReady()) {
				if (condition.getResult()) {
					outPutFiledsIndexArray = entry.getValue();
					break;
				}
			} else {
				String filedName = condition.getLeftKey();
				if (fieldValueMap.containsKey(filedName)) {
					condition.setFirstValue(fieldValueMap.get(filedName));
				}

				filedName = condition.getRightKey();
				if (fieldValueMap.containsKey(filedName)) {
					condition.setSecondValue(fieldValueMap.get(filedName));
				}
				if (condition.getResult()) {
					outPutFiledsIndexArray = entry.getValue();
					break;
				}
			}
		}

		if (outPutFiledsIndexArray == null) {
			LOGGER.debug("No matched output format rule! record=[" + line + "], cid=" + cid + ", lac=" + lac + ",cType="
					+ cType + ",callType=" + callType);
			return returnMap;
		}

		boolean isFirst = true;
		// ����Ҫ����ļ�¼���ݣ����浽stringBuilder�����ڽ������
		for (String fieldInfo : outPutFiledsIndexArray) {
			// �����һ��ʱ��Ϊ�գ� ��ȡ�ڶ���ʱ��
			if (fieldInfo.trim().startsWith("{time1}")) {
				orderTime = oriTime1;
				if (StringUtil.isEmpty(fieldValueMap.get(fieldInfo))
						&& StringUtil.isNotEmpty(fieldValueMap.get("{time2}"))) {
					stringBuilder.append(fieldValueMap.get("{time2}") + taskInfo.getOutSplitChar());
					orderTime = oriTime2;
					continue;
				} else if (StringUtil.isEmpty(fieldValueMap.get("{time2}"))) {
					LOGGER.debug("Time is empty! record=[" + line + "]");
					return returnMap;
				}
			}
			// ʱ��2Ϊ��
			if (fieldInfo.trim().startsWith("{time2}")) {
				orderTime = oriTime2;
				if (StringUtil.isEmpty(fieldValueMap.get(fieldInfo))
						&& StringUtil.isNotEmpty(fieldValueMap.get("{time1}"))) {
					stringBuilder.append(fieldValueMap.get("{time1}") + taskInfo.getOutSplitChar());
					orderTime = oriTime1;
					continue;
				} else if (StringUtil.isEmpty(fieldValueMap.get("{time1}"))) {
					LOGGER.debug("Time is empty! record=[" + line + "]");
					return returnMap;
				}
			}

			if (fieldValueMap.containsKey(fieldInfo)) {
				if (isFirst) {
					// ���˵�imsiΪ0�ļ�¼
					if (fieldInfo.trim().toLowerCase().contains("imsi")) {
						if (fieldValueMap.get(fieldInfo).startsWith("0000000000")) {
							LOGGER.debug("imsi is invalid! record=[" + line + "]");
							return returnMap;
						}
					}
					stringBuilder.append(fieldValueMap.get(fieldInfo));
				} else {
					stringBuilder.append(taskInfo.getOutSplitChar() + fieldValueMap.get(fieldInfo));
				}

			} else {
				if (!isFirst) {
					stringBuilder.append(taskInfo.getOutSplitChar());
				} else {
					LOGGER.debug("imsi is empty! record=[" + line + "]");
					return returnMap;
				}
			}

			isFirst = false;

		}
		//System.out.println("orderTime=" + orderTime + ", line=" + line);
		returnMap.put(RECORD, new String[] { stringBuilder.toString() });
		returnMap.put(ORDERVALUES, formatTimeStr(orderTime));
		return returnMap;
	}

	private String formatTime(TaskInfo taskInfo, String oritimestr) {
		// ��ǰ�ֶ�Ϊ����������ֶΣ����涨�����������ʽ����ת��
		String dateFieldInfo = taskInfo.getDateFieldInfo();
		SimpleDateFormat oriDateFormat = new SimpleDateFormat(dateFieldInfo.split(",")[0]);
		SimpleDateFormat OutDateFormat = new SimpleDateFormat(dateFieldInfo.split(",")[1]);
		Date date = null;
		try {

			date = oriDateFormat.parse(oritimestr);
			return OutDateFormat.format(date);
		} catch (ParseException e) {
			LOGGER.error("Trans date format error!", e);
			return null;
		}
	}

	private void intsertToContainer(String[] sortValues, String line) {
		long partition = CastUtil.castLong(sortValues[0]);
		int value = CastUtil.castInt(sortValues[1]);

		if (recordMap.containsKey(partition)) {
			// ��ǰpartition�Ѿ�����
			CellPhoneSignalList cellPhoneSignalList = recordMap.get(partition);
			CellPhoneSignal cellphoneSignal = new CellPhoneSignal();
			cellphoneSignal.setId(value);
			cellphoneSignal.setRecord(line);
			cellPhoneSignalList.insertRecord(cellphoneSignal);
		} else {
			// ��ǰpartition������
			CellPhoneSignalList cellPhoneSignalList = new CellPhoneSignalList();
			cellPhoneSignalList.setPartitionKey(partition);
			CellPhoneSignal cellphoneSignal = new CellPhoneSignal();
			cellphoneSignal.setId(value);
			cellphoneSignal.setRecord(line);
			cellPhoneSignalList.insertRecord(cellphoneSignal);

			recordMap.put(partition, cellPhoneSignalList);
		}
	}

	// 20170719 06:02:23.658
	private String[] formatTimeStr(String timeStr) {
		// YYMMDD + HH + MI
		String partition, value;
		partition = timeStr.substring(2, 8) + timeStr.substring(9, 11) + timeStr.substring(12, 14);
		value = timeStr.substring(15, 17);
		if (timeStr.length() >= 21) {
			value = value + timeStr.substring(18, 21);
		} else {
			value = value + "000";
		}
		return new String[] { partition, value };
	}

}
