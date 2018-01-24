package com.cttic.cell.phone.signal;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cttic.cell.phone.signal.configure.LoadBaseStationInfo;
import com.cttic.cell.phone.signal.configure.LoadConfigure;
import com.cttic.cell.phone.signal.kafka.GenericKafkaProducer;
import com.cttic.cell.phone.signal.kafka.IKafkaProducer;
import com.cttic.cell.phone.signal.tasks.DataConvertTask;
import com.cttic.cell.phone.signal.tasks.DataSendTask;
import com.cttic.cell.phone.signal.utils.DatabaseHelper;

public class CellPhoneSignalApplication {
	private static final Logger LOGGER = LoggerFactory.getLogger(CellPhoneSignalApplication.class);
	private static final String DEFAULT_CONFIG = "cell_data_process_cfg.ini";

	public static void main(String[] args) {
		try {
			// ���������ļ�
			LoadConfigure configure = LoadConfigure.getInstance(DEFAULT_CONFIG);

			// ��ʼ�����ݿ�
			DatabaseHelper.init(configure.getDRIVER(), configure.getURL(), configure.getUSERNAME(),
					configure.getPASSWORD());
			LoadBaseStationInfo.load();

			// �����ļ�Ԥ��������
			Thread dataTask = new Thread(new DataConvertTask(configure), "DataConvertTask-Thread");
			dataTask.start();

			// ����kafka��Ϣ����������
			IKafkaProducer producer = new GenericKafkaProducer(configure.getKafkaProps());
			Thread sendTask = new Thread(new DataSendTask(producer), "KafkaProducer-Thread");
			sendTask.start();

			while (true) {
				if (!dataTask.isAlive()) {
					LOGGER.info(
							"Beacause DataConvertTask-Thread is exit abnormal! then KafkaProducer-Thread will be interrupted.");
					sendTask.interrupt();
					break;
				}

				if (!sendTask.isAlive()) {
					LOGGER.info(
							"Beacause KafkaProducer-Thread is exit abnormal! then DataConvertTask-Thread will be interrupted.");
					dataTask.interrupt();
					break;
				}
				TimeUnit.SECONDS.sleep(10);
			}

		} catch (Exception e) {
			LOGGER.error("Load the configure file failure!", e);
			throw new RuntimeException(e);
		}
	}
}
