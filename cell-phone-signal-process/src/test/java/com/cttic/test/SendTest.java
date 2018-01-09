package com.cttic.test;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.cttic.cell.phone.signal.configure.LoadConfigure;
import com.cttic.cell.phone.signal.kafka.GenericKafkaProducer;
import com.cttic.cell.phone.signal.kafka.IKafkaProducer;
import com.cttic.cell.phone.signal.tasks.DataSendTask;

public class SendTest {
	private static final String DEFAULT_CONFIG = "cell_data_process_cfg.ini";

	@Test
	public void producerTest() {
		LoadConfigure configure;
		try {
			configure = LoadConfigure.getInstance(DEFAULT_CONFIG);
			Set<Entry<Object, Object>> entrySet = configure.getKafkaProps().entrySet();
			for (Entry<Object, Object> entry : entrySet) {
				System.out.println(entry.getKey() + " - " + entry.getValue());
			}
			IKafkaProducer producer = new GenericKafkaProducer(configure.getKafkaProps());
			DataSendTask sendTask = new DataSendTask(producer);

			Thread sendThread = new Thread(sendTask, "sendTask-thread");
			sendThread.start();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		(new SendTest()).producerTest();
	}
}
