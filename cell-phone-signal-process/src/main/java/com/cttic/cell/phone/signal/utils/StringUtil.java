package com.cttic.cell.phone.signal.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串工具类
 * 
 * @author liugw
 *
 */
public final class StringUtil {

	/**
	 * 判断字符串是否为�?
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str != null) {
			str = str.trim();
		}
		return StringUtils.isEmpty(str);
	}

	/**
	 * 判断字符串是否非�?
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
}
