package com.cttic.cell.phone.signal.kafka;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.GzipException;
import com.cttic.cell.phone.signal.utils.StringUtil;
import com.cttic.cell.phone.signal.utils.zip.FileUtil;
import com.cttic.cell.phone.signal.utils.zip.GZipUtils;

public class GenericKafkaProducer implements IKafkaProducer {
	private static final Logger LOGGER = LoggerFactory.getLogger(GenericKafkaProducer.class);

	Producer<String, String> producer;
	//	Producer<String[], String[]> producerBatcher;
	private String topicnames;
	private AtomicLong lMessageKey = new AtomicLong(0);
	private Properties props;

	public GenericKafkaProducer(Properties props) {
		this.props = props;
		topicnames = props.getProperty("topicName");
		if (StringUtil.isEmpty(topicnames)) {
			LOGGER.error("The topic name is empty! please provide the topicName in the configure file!");
			throw new RuntimeException("The topic name is empty! please provide the topicName in configure file!");
		}
	}

	@Override
	public void connect() {
		Properties connectProps = new Properties();
		if (props.getProperty("flag") != null) {
			connectProps = props;
		} else {
			connectProps = new Properties();
			connectProps.setProperty("bootstrap.servers", props.getProperty("bootstrap.servers"));
			connectProps.setProperty("acks", props.getProperty("acks"));
			connectProps.setProperty("retries", props.getProperty("retries"));
			connectProps.setProperty("batch.size", props.getProperty("batch.size"));
			connectProps.setProperty("linger.ms", props.getProperty("linger.ms"));
			connectProps.setProperty("buffer.memory", props.getProperty("buffer.memory"));
			connectProps.setProperty("key.serializer", props.getProperty("key.serializer"));
			connectProps.setProperty("value.serializer", props.getProperty("value.serializer"));
		}

		producer = new KafkaProducer<String, String>(connectProps);
		//		producerBatcher = new KafkaProducer<String[], String[]>(props);
	}

	//	public void send(String[] keys, String[] messages) {
	//		if (producerBatcher == null) {
	//			connect();
	//		}
	//		producerBatcher.send(new ProducerRecord<String[], String[]>(topicnames, keys, messages));
	//	}

	@Override
	public void send(String message) {
		if (producer == null) {
			connect();
		}
		//		System.out.println("topicnames:" + topicnames);
		//		System.out.println("message:" + message);
		//		System.out.println("lMessageKey:" + lMessageKey.get());
		Future<RecordMetadata> future = producer.send(
				new ProducerRecord<String, String>(topicnames, Long.toString(lMessageKey.incrementAndGet()), message));
		//		try {
		//			RecordMetadata recordMetadata = future.get();
		//			//			System.out.println("send return:[" + recordMetadata.toString() + "]");
		//			//			recordMetadata.
		//		} catch (InterruptedException e) {
		//			disConnect();
		//			e.printStackTrace();
		//		} catch (ExecutionException e) {
		//			disConnect();
		//			e.printStackTrace();
		//		}
	}

	private void productFromFile() throws GzipException {
		String filePath = props.getProperty("filePath");
		String fileNameReg = props.getProperty("fileNameReg");
		String fileBakPath = props.getProperty("bakPath");
		if (!fileBakPath.endsWith(File.separator)) {
			fileBakPath += File.separator;
		}

		List<File> allFiles = new ArrayList<>();
		List<File> files = FileUtil.getFiles(filePath);
		for (File file : files) {
			if (file.getName().matches(fileNameReg)) {
				allFiles.add(file);
			}
		}

		int totalCount = 0;
		for (File f : allFiles) {
			totalCount = 0;
			try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(f)); // , "GB2312"
					BufferedReader bufferedReader = new BufferedReader(fileReader);) {

				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					totalCount++;
					send(line);
				}
			} catch (Exception e) {
				LOGGER.error("Process file=[" + f.getAbsolutePath() + "] error!", e);
				throw new RuntimeException(e);
			}
			LOGGER.debug("Processed file " + f.getAbsolutePath() + ": send to kafka count:" + totalCount);
			if (FileUtil.rename(f, new File(fileBakPath + f.getName()))) {
				LOGGER.debug("File:[" + f.getAbsolutePath() + "] backup to [" + fileBakPath + "] success.");
				if (CastUtil.castBoolean(props.getProperty("bakFileCompress", "false"))) {
					GZipUtils.compress(new File(fileBakPath + f.getName()), true);
				}
			} else {
				LOGGER.error("File:[" + f.getAbsolutePath() + "] backup to [" + fileBakPath + "] failure.");
				throw new RuntimeException("Rename file to sendbak dirctory failure!");
			}
		}
	}

	//	private void batcherProductFromFile() {
	//		String filePath = props.getProperty("filePath");
	//		String fileNameReg = props.getProperty("fileNameReg");
	//
	//		List<File> allFiles = new ArrayList<>();
	//		List<File> files = FileUtil.getFiles(filePath);
	//		for (File file : files) {
	//			if (file.getName().matches(fileNameReg)) {
	//				allFiles.add(file);
	//			}
	//		}
	//
	//		int totalCount = 0;
	//		for (File f : allFiles) {
	//			totalCount = 0;
	//			List<String> keyList = new ArrayList<>();
	//			List<String> messageList = new ArrayList<>();
	//			//String[] array = (String[]) keys.toArray();
	//			try (InputStreamReader fileReader = new InputStreamReader(new FileInputStream(f)); // , "GB2312"
	//					BufferedReader bufferedReader = new BufferedReader(fileReader);) {
	//
	//				String line = null;
	//				while ((line = bufferedReader.readLine()) != null) {
	//					totalCount++;
	//					keyList.add(Long.toString(lMessageKey.incrementAndGet()));
	//					messageList.add(line);
	//					send(CollectionUtil.toArray(keyList), CollectionUtil.toArray(messageList));
	//				}
	//			} catch (Exception e) {
	//				LOGGER.error("Process file=[" + f.getAbsolutePath() + "] error!", e);
	//				throw new RuntimeException(e);
	//			}
	//			System.out.println(f.getAbsolutePath() + ": send to kafka count:" + totalCount);
	//		}
	//	}

	@Override
	public void disConnect() {
		if (producer != null) {
			producer.close();
			producer = null;
		}

	}

	@Override
	public void start() {
		try {
			productFromFile();
		} catch (GzipException e) {
			LOGGER.error("Compress the sendbak file failure.", e);
		}
		//		batcherProductFromFile();
	}

}
