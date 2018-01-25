package com.cttic.cell.phone.signal.configure;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cttic.cell.phone.signal.pojo.BaseStationInfo;
import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.DatabaseHelper;

public class LoadBaseStationInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 10000001L;
	private static Map<String, BaseStationInfo> baseStationInfoMap = new HashMap<String, BaseStationInfo>();

	public static BaseStationInfo find(String cellId, String Lac, String cType) {
		BaseStationInfo stationInfo = new BaseStationInfo();
		stationInfo.setCID(CastUtil.castInt(cellId));
		stationInfo.setLAC(CastUtil.castInt(Lac));
		stationInfo.setcType(cType);

		String key1 = getKey1(stationInfo);
		String key2 = getKey2(stationInfo);
		if (baseStationInfoMap.containsKey(key1)) {
			return baseStationInfoMap.get(key1);
		} else if (baseStationInfoMap.containsKey(key2)) {
			return baseStationInfoMap.get(key2);
		}
		return null;
	}

	private static void insertMap1(BaseStationInfo baseStationInfo) {
		String key = getKey1(baseStationInfo);
		baseStationInfoMap.put(key, baseStationInfo);
	}

	private static void insertMap2(BaseStationInfo baseStationInfo) {
		String key = getKey2(baseStationInfo);
		baseStationInfoMap.put(key, baseStationInfo);
	}

	private static String getKey1(BaseStationInfo baseStationInfo) {
		return baseStationInfo.getCID() + "|" + baseStationInfo.getLAC() + "|" + baseStationInfo.getcType();
	}

	private static String getKey2(BaseStationInfo baseStationInfo) {
		return baseStationInfo.getCID() + "|" + baseStationInfo.getLAC();
	}

	public static void load(LoadConfigure configure) {
		try {
			// 加载成功
			if (loadFromDB()) {
				// 序列化处理
				if (configure.isSerial()) {
					javaSerial(configure.getSerialPath());
				}
			} else {
				// 从数据库加载配置数据失败
				if (configure.isSerial()) {
					javaDeSerial(configure.getSerialPath());
				} else {
					throw new RuntimeException("Load BaseStationInfo failed!");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Load BaseStationInfo failed!", e);
		}
	}

	public static boolean loadFromDB() {
		System.out.println("============== load BaseStationInfo ========================");
		try {
			List<BaseStationInfo> queryEntityList = DatabaseHelper.queryEntityList(BaseStationInfo.class,
					"select * from XJ_BASE.BASE_STATION_INFO", null);

			for (BaseStationInfo baseStationInfo : queryEntityList) {
				System.out.println(baseStationInfo);
				insertMap1(baseStationInfo);
				insertMap2(baseStationInfo);
			}
			System.out.println(queryEntityList.size());
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Java 原生的序列化
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void javaSerial(String serialPath) throws FileNotFoundException, IOException {
		System.out.println(baseStationInfoMap.size());
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				new FileOutputStream(serialPath + "basestationinfo.dat"));
		objectOutputStream.writeObject(baseStationInfoMap);
		objectOutputStream.close();
	}

	/**
	 * Java 原生的反序列化
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void javaDeSerial(String serialPath)
			throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream objectInputStream = new ObjectInputStream(
				new FileInputStream(serialPath + "basestationinfo.dat"));
		baseStationInfoMap = (Map<String, BaseStationInfo>) objectInputStream.readObject();
		objectInputStream.close();
		System.out.println("======================= 反序列化文件 basestationinfo.dat Start ================");
		System.out.println(baseStationInfoMap.size());
		for (Map.Entry<String, BaseStationInfo> entry : baseStationInfoMap.entrySet()) {
			System.out.printf("key=[%30s], vlaue=[%s]\r\n", entry.getKey(), entry.getValue());
		}
		System.out.println("======================= 反序列化文件 basestationinfo.dat End   ================");
	}
}
