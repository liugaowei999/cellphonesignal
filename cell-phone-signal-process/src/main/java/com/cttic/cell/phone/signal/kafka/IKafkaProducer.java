package com.cttic.cell.phone.signal.kafka;

public interface IKafkaProducer {

	public void connect();

	public void send(String message);

	//	public void send(KeyedMessage<String, byte[]> km);

	public void start();

	public void disConnect();
}
