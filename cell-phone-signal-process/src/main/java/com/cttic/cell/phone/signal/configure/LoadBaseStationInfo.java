package com.cttic.cell.phone.signal.configure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cttic.cell.phone.signal.pojo.BaseStationInfo;
import com.cttic.cell.phone.signal.utils.CastUtil;
import com.cttic.cell.phone.signal.utils.DatabaseHelper;

public class LoadBaseStationInfo {

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

	public static void load() {
		System.out.println("============== load ========================");
		//		DatabaseHelper.getConnection();

		List<BaseStationInfo> queryEntityList = DatabaseHelper.queryEntityList(BaseStationInfo.class,
				"select * from XJ_BASE.BASE_STATION_INFO", null);

		for (BaseStationInfo baseStationInfo : queryEntityList) {
			System.out.println(baseStationInfo);
			insertMap1(baseStationInfo);
			insertMap2(baseStationInfo);
		}

		System.out.println(queryEntityList.size());
	}
}
