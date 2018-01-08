package com.cttic.cell.phone.signal.run;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cttic.cell.phone.signal.configure.LoadConfigure;
import com.cttic.cell.phone.signal.tasks.DataConvertTask;

public class DataProcessStarter {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataProcessStarter.class);
	private static final String DEFAULT_CONFIG = "cell_data_process_cfg.ini";

	public static void main(String[] args) {
		try {
			LoadConfigure configure = LoadConfigure.getInstance(DEFAULT_CONFIG);
			DataConvertTask dataConvertTask = new DataConvertTask(configure.getTaskList());
			Thread taskThread = new Thread(dataConvertTask, "DataConvertTask-thread");
			taskThread.start();

		} catch (Exception e) {
			LOGGER.error("Load the configure file failure!", e);
			throw new RuntimeException(e);
		}
	}
}
