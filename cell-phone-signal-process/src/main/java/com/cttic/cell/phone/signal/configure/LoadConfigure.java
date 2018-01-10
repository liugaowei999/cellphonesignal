package com.cttic.cell.phone.signal.configure;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

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

	// ���Ŀ¼
	private String outPutPath;

	// ����ļ�ǰ׺
	private String outputFilePre;

	// ����ļ���׺
	private String outputFileSub;

	// ��ʱĿ¼
	private String tmpPath;

	// ��ʱ�ļ�ǰ׺
	private String tmpFilePre;

	// ��ʱ�ļ���׺
	private String tmpFileSub;

	// ����Ŀ¼
	private String bakPath;

	// �Ƿ�ѹ��
	private boolean isCompress;

	// ��������������������ʽ
	private static final String TASK_SECTION_NAME_REG = "^TASK_.*";

	// Kafka ��صĲ�������
	private Properties kafkaProps = new Properties();

	//����
	private static LoadConfigure instance;

	private LoadConfigure(String fileName) {
		this.fileName = fileName;
		this.configFileFullPath = getConfigPathFile(fileName);
	}

	private void init() throws IOException {
		IniReader reader = new IniReader(configFileFullPath);
		//		backlog = Integer.parseInt(reader.getValue("HTTPSERVER", "backlog").trim());
		//		String loggerPath = reader.getValue("LOGGER", "logPath").trim();
		//		System.out.println("loggerPath=" + loggerPath);

		setOutPutPath(reader.getValue("OUTPUT", "outputpath").trim());
		setOutputFilePre(reader.getValue("OUTPUT", "outputFilePre").trim());
		setOutputFileSub(reader.getValue("OUTPUT", "outputFileSub").trim());

		setTmpPath(reader.getValue("TMP", "tmppath").trim());
		setTmpFilePre(reader.getValue("TMP", "tmpFilePre").trim());
		setTmpFileSub(reader.getValue("TMP", "tmpFileSub").trim());

		setBakPath(reader.getValue("BACKUP", "bakpath").trim());
		setCompress(CastUtil.castBoolean(reader.getValue("BACKUP", "compress").trim()));

		// �����ļ���ȡ�������б�
		List<String> sections = reader.getSectionList(TASK_SECTION_NAME_REG);
		for (String section : sections) {
			String recordType = reader.getValue(section, "recordType").trim();
			String oriPath = reader.getValue(section, "oripath").trim();
			String oriFileMatcher = reader.getValue(section, "fileMatcher").trim();
			short orderFieldIndex = CastUtil.castShort(reader.getValue(section, "orderFieldIndex").trim());
			TaskInfo taskInfo = new TaskInfo(oriPath, oriFileMatcher, orderFieldIndex, recordType);
			taskList.add(taskInfo);
		}

		// ����kafka��ص�������
		kafkaProps.setProperty("bootstrap.servers", reader.getValue("KAFKA", "bootstrap.servers").trim());
		kafkaProps.setProperty("acks", reader.getValue("KAFKA", "acks").trim());
		kafkaProps.setProperty("retries", reader.getValue("KAFKA", "retries").trim());
		kafkaProps.setProperty("batch.size", reader.getValue("KAFKA", "batch.size").trim());
		kafkaProps.setProperty("linger.ms", reader.getValue("KAFKA", "linger.ms").trim());
		kafkaProps.setProperty("buffer.memory", reader.getValue("KAFKA", "buffer.memory").trim());
		kafkaProps.setProperty("key.serializer", reader.getValue("KAFKA", "key.serializer").trim());
		kafkaProps.setProperty("value.serializer", reader.getValue("KAFKA", "value.serializer").trim());
		kafkaProps.setProperty("topicName", reader.getValue("KAFKA", "topicName").trim());
		// Դ�ļ���Ϣ
		kafkaProps.setProperty("filePath", reader.getValue("KAFKA", "filePath").trim());
		kafkaProps.setProperty("fileNameReg", reader.getValue("KAFKA", "fileNameReg").trim());
		kafkaProps.setProperty("bakPath", reader.getValue("KAFKA", "bakPath").trim());
		kafkaProps.setProperty("bakFileCompress", reader.getValue("KAFKA", "bakFileCompress").trim());
	}

	/**
	 * ��ȡ������Ϣ�е� Kafka��ص���������
	 * 
	 */
	public Properties getKafkaProps() {
		Properties cpKafkaProps = new Properties();
		for (Entry<Object, Object> entry : kafkaProps.entrySet()) {
			cpKafkaProps.setProperty((String) entry.getKey(), (String) entry.getValue());
		}
		return cpKafkaProps;
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

	public String getOutPutPath() {
		return outPutPath;
	}

	public void setOutPutPath(String outPutPath) {
		if (!outPutPath.endsWith(File.separator)) {
			this.outPutPath = outPutPath + File.separator;
		} else {
			this.outPutPath = outPutPath;
		}
	}

	public String getOutputFilePre() {
		return outputFilePre;
	}

	public void setOutputFilePre(String outputFilePre) {
		this.outputFilePre = outputFilePre;
	}

	public String getOutputFileSub() {
		return outputFileSub;
	}

	public void setOutputFileSub(String outputFileSub) {
		this.outputFileSub = outputFileSub;
	}

	public String getTmpPath() {
		return tmpPath;
	}

	public void setTmpPath(String tmpPath) {
		if (!tmpPath.endsWith(File.separator)) {
			this.tmpPath = tmpPath + File.separator;
		} else {
			this.tmpPath = tmpPath;
		}
	}

	public String getTmpFilePre() {
		return tmpFilePre;
	}

	public void setTmpFilePre(String tmpFilePre) {
		this.tmpFilePre = tmpFilePre;
	}

	public String getTmpFileSub() {
		return tmpFileSub;
	}

	public void setTmpFileSub(String tmpFileSub) {
		this.tmpFileSub = tmpFileSub;
	}

	public String getBakPath() {
		return bakPath;
	}

	public void setBakPath(String bakPath) {
		if (!bakPath.endsWith(File.separator)) {
			this.bakPath = bakPath + File.separator;
		} else {
			this.bakPath = bakPath;
		}
	}

	public boolean isCompress() {
		return isCompress;
	}

	public void setCompress(boolean isCompress) {
		this.isCompress = isCompress;
	}
}
