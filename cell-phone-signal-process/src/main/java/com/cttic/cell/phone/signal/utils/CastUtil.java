package com.cttic.cell.phone.signal.utils;

public class CastUtil {

	/**
	 * 转为String类型
	 * 
	 * @param obj
	 * @return
	 */
	public static String castString(Object obj) {
		return castString(obj, "");
	}

	/**
	 * 转为String类型（提供默认�?�）
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	private static String castString(Object obj, String defaultValue) {
		// TODO Auto-generated method stub
		return obj != null ? String.valueOf(obj) : defaultValue;
	}

	/**
	 * 转为double类型
	 * 
	 * @param obj
	 * @return
	 */
	public static double castDouble(Object obj) {
		return castDouble(obj, 0);
	}

	/**
	 * 转为double类型（提供默认�?�）
	 * 
	 * @param obj
	 * @param defauleValue
	 * @return
	 */
	public static double castDouble(Object obj, double defauleValue) {
		double doubleValue = defauleValue;
		if (obj != null) {
			String strValue = castString(obj);
			if (StringUtil.isNotEmpty(strValue)) {
				try {
					doubleValue = Double.parseDouble(strValue);
				} catch (NumberFormatException e) {
					doubleValue = defauleValue;
				}
			}
		}
		return doubleValue;
	}

	/**
	 * 转为long类型（有默认值）
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static long castLong(Object obj, long defaultValue) {
		long longValue = defaultValue;
		if (obj != null) {
			String strValue = castString(obj);
			if (StringUtil.isNotEmpty(strValue)) {
				try {
					longValue = Long.parseLong(strValue);
				} catch (NumberFormatException e) {
					longValue = defaultValue;
				}
			}
		}
		return longValue;
	}

	/**
	 * 转为long类型
	 * 
	 * @param obj
	 * @return
	 */
	public static long castLong(Object obj) {
		return castLong(obj, 0);
	}

	/**
	 * 转为int类型（有默认值）
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static int castInt(Object obj, int defaultValue) {
		int intValue = defaultValue;
		if (obj != null) {
			String strValue = castString(obj);
			if (StringUtil.isNotEmpty(strValue)) {
				try {
					intValue = Integer.parseInt(strValue);
				} catch (NumberFormatException e) {
					intValue = defaultValue;
				}
			}
		}
		return intValue;
	}

	/**
	 * 转为int类型
	 * 
	 * @param obj
	 * @return
	 */
	public static int castInt(Object obj) {
		return castInt(obj, 0);
	}

	/**
	 * 转为short类型（有默认值）
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static short castShort(Object obj, short defaultValue) {
		short shortValue = defaultValue;
		if (obj != null) {
			String strValue = castString(obj);
			if (StringUtil.isNotEmpty(strValue)) {
				try {
					shortValue = Short.parseShort(strValue);
				} catch (NumberFormatException e) {
					shortValue = defaultValue;
				}
			}
		}
		return shortValue;
	}

	/**
	 * 转为short类型
	 * 
	 * @param obj
	 * @return
	 */
	public static short castShort(Object obj) {
		return castShort(obj, (short) 0);
	}

	/**
	 * 转为boolean类型（有默认值）
	 * 
	 * @param obj
	 * @param defaultValue
	 * @return
	 */
	public static boolean castBoolean(Object obj, boolean defaultValue) {
		boolean booleanValue = defaultValue;
		if (obj != null) {
			booleanValue = Boolean.parseBoolean(castString(obj));
		}
		return booleanValue;
	}

	/**
	 * 转为boolean类型
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean castBoolean(Object obj) {
		return castBoolean(obj, false);
	}

}
