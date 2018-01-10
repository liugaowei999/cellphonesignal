package com.cttic.cell.phone.signal.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cttic.cell.phone.signal.configure.LoadConfigure;
import com.cttic.cell.phone.signal.pojo.CellPhoneSignal;
import com.cttic.cell.phone.signal.pojo.CellPhoneSignalList;
import com.cttic.cell.phone.signal.pojo.TaskInfo;
import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.CollectionUtil;
import com.cttic.cell.phone.signal.utils.GzipException;
import com.cttic.cell.phone.signal.utils.zip.FileUtil;
import com.cttic.cell.phone.signal.utils.zip.GZipUtils;

public class DataConvertTask implements Runnable {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataConvertTask.class);

	// 配置信息
	LoadConfigure configure;

	private boolean isStop;

	// 处理文件目录
	private List<TaskInfo> taskInfos;

	// 记录所有的partition信息
	private HashMap<Long, CellPhoneSignalList> recordMap;

	public DataConvertTask(LoadConfigure configure) {
		this.configure = configure;
		List<TaskInfo> taskInfos = this.configure.getTaskList();
		this.taskInfos = taskInfos;
	}

	/**
	 * 获取一个文件夹以及其子文件夹下的所有的文件
	 */
	private Map<TaskInfo, List<File>> getAllFiles() {
		Map<TaskInfo, List<File>> allFiles = new HashMap<>();
		int fileCount = 0;
		try {
			for (TaskInfo task : taskInfos) {
				List<File> files = FileUtil.getFiles(task.getOriPath());
				for (File f : files) {
					if (f.getName().matches(task.getOriFileMatcher())) {
						fileCount++;
						if (allFiles.containsKey(task)) {
							allFiles.get(task).add(f);
						} else {
							List<File> currentTaskFiles = new ArrayList<>();
							currentTaskFiles.add(f);
							allFiles.put(task, currentTaskFiles);
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

			//第一步：获取一个文件夹下的所有的文件
			Map<TaskInfo, List<File>> allFiles = getAllFiles();
			if (CollectionUtil.isNotEmpty(allFiles)) {

				recordMap = new HashMap<>();

				// 遍历所有文件列表
				for (Map.Entry<TaskInfo, List<File>> entry : allFiles.entrySet()) {
					processTaskFiles(entry.getKey(), entry.getValue());
				}

				// 写出当前批次的所有记录 并 排序
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
		// 遍历所有文件列表
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
					// 压缩并删除原始文件
					GZipUtils.compress(new File(bakfilepath), true);
				}
			}
		}
	}

	private void writeRecordsToFile(HashMap<Long, CellPhoneSignalList> recordMap) throws IOException {
		// HashMap 排序
		List<Map.Entry<Long, CellPhoneSignalList>> infoIds = new ArrayList<Map.Entry<Long, CellPhoneSignalList>>(
				recordMap.entrySet());
		//		System.out.println("============================= 排序前：");
		//		for (Entry<Long, CellPhoneSignalList> entry : infoIds) {
		//			System.out.println(entry.getKey());
		//		}

		// 排序
		Collections.sort(infoIds, new Comparator<Map.Entry<Long, CellPhoneSignalList>>() {
			@Override
			public int compare(Map.Entry<Long, CellPhoneSignalList> o1, Map.Entry<Long, CellPhoneSignalList> o2) {
				if (o2.getKey() == o1.getKey()) {
					return 0;
				}
				return o1.getKey() > o2.getKey() ? 1 : -1;
			}
		});

		//System.out.println("============================= 排序后：");
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

		// 移到正式目录下
		// System.out.println("rename to out path");
		File file = new File(tmpFilePath);
		if (FileUtil.rename(file, new File(outputFilePath))) {
			LOGGER.debug("File:[" + tmpFilePath + "] rename to [" + outputFilePath + "] success.");
		} else {
			LOGGER.error("File:[" + tmpFilePath + "] rename to [" + outputFilePath + "] failure.");
		}
	}

	/**
	 * 处理当前任务的所有文件
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
	 * 处理单个文件
	 * 
	 * @param taskInfo
	 * @param f
	 */
	private void processFile(TaskInfo taskInfo, File f) {
		short sortFieldIndex = (short) (taskInfo.getOrderFieldIndex() - 1);
		try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(f)); // , "GB2312"
				BufferedReader bufferedReader = new BufferedReader(fileReader);) {

			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] sortValues = formatTimeStr(line.split(",")[sortFieldIndex]);
				intsertToContainer(sortValues, taskInfo.getRecordType() + "," + line);
			}
		} catch (Exception e) {
			LOGGER.error("Process file=[" + f.getAbsolutePath() + "] error!", e);
			throw new RuntimeException(e);
		}
	}

	private void intsertToContainer(String[] sortValues, String line) {
		long partition = CastUtil.castLong(sortValues[0]);
		int value = CastUtil.castInt(sortValues[1]);

		if (recordMap.containsKey(partition)) {
			// 当前partition已经存在
			CellPhoneSignalList cellPhoneSignalList = recordMap.get(partition);
			CellPhoneSignal cellphoneSignal = new CellPhoneSignal();
			cellphoneSignal.setId(value);
			cellphoneSignal.setRecord(line);
			cellPhoneSignalList.insertRecord(cellphoneSignal);
		} else {
			// 当前partition不存在
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
		String partition = timeStr.substring(2, 8) + timeStr.substring(9, 11) + timeStr.substring(12, 14);
		String value = timeStr.substring(15, 17) + timeStr.substring(18, 21);
		//System.out.println("partition=" + partition + ", value=" + value);
		return new String[] { partition, value };
	}

}
