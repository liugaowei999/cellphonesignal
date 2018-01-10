package com.cttic.cell.phone.signal.tasks;

import com.cttic.cell.phone.signal.kafka.IKafkaProducer;

public class DataSendTask implements Runnable {
	private IKafkaProducer producer;
	private volatile boolean isStop;

	public DataSendTask(IKafkaProducer producer) {
		this.producer = producer;
	}

	@Override
	public void run() {
		isStop = false;
		producer.connect();
		while (!isStop && !Thread.currentThread().isInterrupted()) {
			producer.start();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				isStop = true;
				// 重新恢复中断信号
				Thread.currentThread().interrupt();
			}
		}
		if (producer != null)
			producer.disConnect();
	}

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

}
