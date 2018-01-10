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
	//配置文件名
	private String fileName;
	//配置文件全路径
	private String configFileFullPath;
	//任务列表
	private List<TaskInfo> taskList = new ArrayList<TaskInfo>();

	// 输出目录
	private String outPutPath;

	// 输出文件前缀
	private String outputFilePre;

	// 输出文件后缀
	private String outputFileSub;

	// 临时目录
	private String tmpPath;

	// 临时文件前缀
	private String tmpFilePre;

	// 临时文件后缀
	private String tmpFileSub;

	// 备份目录
	private String bakPath;

	// 是否压缩
	private boolean isCompress;

	// 任务配置域名称正则表达式
	private static final String TASK_SECTION_NAME_REG = "^TASK_.*";

	// Kafka 相关的参数配置
	private Properties kafkaProps = new Properties();

	//单例
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

		// 加载文件读取的任务列表
		List<String> sections = reader.getSectionList(TASK_SECTION_NAME_REG);
		for (String section : sections) {
			String recordType = reader.getValue(section, "recordType").trim();
			String oriPath = reader.getValue(section, "oripath").trim();
			String oriFileMatcher = reader.getValue(section, "fileMatcher").trim();
			short orderFieldIndex = CastUtil.castShort(reader.getValue(section, "orderFieldIndex").trim());
			TaskInfo taskInfo = new TaskInfo(oriPath, oriFileMatcher, orderFieldIndex, recordType);
			taskList.add(taskInfo);
		}

		// 加载kafka相关的配置项
		kafkaProps.setProperty("bootstrap.servers", reader.getValue("KAFKA", "bootstrap.servers").trim());
		kafkaProps.setProperty("acks", reader.getValue("KAFKA", "acks").trim());
		kafkaProps.setProperty("retries", reader.getValue("KAFKA", "retries").trim());
		kafkaProps.setProperty("batch.size", reader.getValue("KAFKA", "batch.size").trim());
		kafkaProps.setProperty("linger.ms", reader.getValue("KAFKA", "linger.ms").trim());
		kafkaProps.setProperty("buffer.memory", reader.getValue("KAFKA", "buffer.memory").trim());
		kafkaProps.setProperty("key.serializer", reader.getValue("KAFKA", "key.serializer").trim());
		kafkaProps.setProperty("value.serializer", reader.getValue("KAFKA", "value.serializer").trim());
		kafkaProps.setProperty("topicName", reader.getValue("KAFKA", "topicName").trim());
		// 源文件信息
		kafkaProps.setProperty("filePath", reader.getValue("KAFKA", "filePath").trim());
		kafkaProps.setProperty("fileNameReg", reader.getValue("KAFKA", "fileNameReg").trim());
		kafkaProps.setProperty("bakPath", reader.getValue("KAFKA", "bakPath").trim());
		kafkaProps.setProperty("bakFileCompress", reader.getValue("KAFKA", "bakFileCompress").trim());
	}

	/**
	 * 获取配置信息中的 Kafka相关的配置属性
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
		//重新初始化log4j的配置文件
		if (initLog4j) {
			String logConfigFile = configPath + "log4j.properties";
			PropertyConfigurator.configure(logConfigFile);
		}
		return configPath + fileName;
	}

	/**
	 * 获取任务列表
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
