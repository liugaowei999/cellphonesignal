package com.cttic.cell.phone.signal.configure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;

import com.cttic.cell.phone.signal.pojo.TaskInfo;
import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.IniReader;

public class LoadConfigure {
	//backlog
	private int backlog;
	//�����ļ���
	private String fileName;
	//�����ļ�ȫ·��
	private String configFileFullPath;
	//�����б�
	private List<TaskInfo> taskList = new ArrayList<TaskInfo>();

	// ��������������������ʽ
	private static final String TASK_SECTION_NAME_REG = "^TASK_.*";

	//����
	private static LoadConfigure instance;

	private LoadConfigure(String fileName) {
		this.fileName = fileName;
		this.configFileFullPath = getConfigPathFile(fileName);
	}

	private void init() throws IOException {
		IniReader reader = new IniReader(configFileFullPath);
		//		backlog = Integer.parseInt(reader.getValue("HTTPSERVER", "backlog").trim());
		String loggerPath = reader.getValue("LOGGER", "logPath").trim();
		System.out.println("loggerPath=" + loggerPath);

		List<String> sections = reader.getSectionList(TASK_SECTION_NAME_REG);
		for (String section : sections) {
			String oriPath = reader.getValue(section, "oripath").trim();
			String oriFileMatcher = reader.getValue(section, "fileMatcher").trim();
			short orderFieldIndex = CastUtil.castShort(reader.getValue(section, "orderFieldIndex").trim());
			TaskInfo taskInfo = new TaskInfo(oriPath, oriFileMatcher, orderFieldIndex);
			taskList.add(taskInfo);
		}
	}

	public static LoadConfigure getInstance(String fileName) throws IOException {
		if (instance == null) {
			instance = new LoadConfigure(fileName);
			instance.init();
		}
		return instance;
	}

	public static LoadConfigure getInstance() {
		return instance;
	}

	public int getBacklog() {
		return backlog;
	}

	public String getFileName() {
		return fileName;
	}

	public String getConfigFileFullPath() {
		return configFileFullPath;
	}

	public static String getConfigPathFile(String fileName) {
		String configPath = System.getProperty("cell.config.path");
		boolean initLog4j = false;
		if (configPath == null) {
			configPath = LoadConfigure.class.getClassLoader().getResource("").getPath();
		} else {
			initLog4j = true;
		}
		if (!configPath.endsWith(File.separator)) {
			configPath += File.separator;
		}
		//���³�ʼ��log4j�������ļ�
		if (initLog4j) {
			String logConfigFile = configPath + "log4j.properties";
			PropertyConfigurator.configure(logConfigFile);
		}
		return configPath + fileName;
	}

	/**
	 * ��ȡ�����б�
	 * @return
	 */
	public List<TaskInfo> getTaskList() {
		return taskList;
	}
}
