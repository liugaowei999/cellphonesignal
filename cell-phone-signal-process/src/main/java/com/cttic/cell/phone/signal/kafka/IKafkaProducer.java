package com.cttic.cell.phone.signal.kafka;

public interface IKafkaProducer {

	public void connect();

	public void send(String message);

	public void start();

	public void disConnect();
}
