package com.cttic.cell.phone.signal.tasks;

import com.cttic.cell.phone.signal.kafka.IKafkaProducer;

public class DataSendTask implements Runnable {
	private IKafkaProducer producer;

	public DataSendTask(IKafkaProducer producer) {
		this.producer = producer;
	}

	@Override
	public void run() {
		producer.connect();
		producer.start();
		producer.disConnect();

	}

}
