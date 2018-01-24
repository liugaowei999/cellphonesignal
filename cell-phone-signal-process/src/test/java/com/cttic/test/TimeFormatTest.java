package com.cttic.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatTest {

	public static void main(String[] args) {
		String s = "20170525 18:35:79.567";
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat format3 = new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat format4 = new SimpleDateFormat("ss");
		Date date = null;
		try {
			date = format1.parse(s);
			String newDate = format2.format(date);
			System.out.println(newDate);
			System.out.println(format3.format(date));
			System.out.println(format4.format(date));

		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		System.out.println(date);
	}
}
