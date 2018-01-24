package com.cttic.cell.phone.signal.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public class IniReader {
	protected HashMap sections = new HashMap();
	private transient String currentSecion;
	private transient Properties current;
	private List<String> sectionList = new ArrayList<>();

	/** 
	 * 构造函数 
	 * @param filename 
	 * @throws IOException 
	 */
	public IniReader(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		read(reader);
		reader.close();
	}

	/** 
	 * 读取文件 
	 * @param reader 
	 * @throws IOException 
	 */
	protected void read(BufferedReader reader) throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			parseLine(line);
		}
	}

	/** 
	 * 解析配置文件行 
	 * @param line 
	 */
	@SuppressWarnings("unchecked")
	protected void parseLine(String line) {
		line = line.trim();
		if (line.matches("\\[.*\\]")) {
			currentSecion = line.replaceFirst("\\[(.*)\\]", "$1");
			sectionList.add(currentSecion);
			current = new Properties();
			sections.put(currentSecion, current);
		} else if (line.matches(".*=.*")) {
			if (current != null) {
				int i = line.indexOf('=');
				String name = line.substring(0, i);
				String value = line.substring(i + 1);
				current.setProperty(name.trim(), value.trim());
			}
		}
	}

	/** 
	 * 获取值 , 如果不存在则异常退出
	 * 
	 * @param section 
	 * @param name 
	 * @return 
	 */
	public String getValue(String section, String name) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			throw new RuntimeException("[" + section + "] is missing!");
		}

		String value = p.getProperty(name);
		if (value == null) {
			throw new RuntimeException("[" + section + "] element: <" + name + "> is missing!");
		}
		return value;
	}

	/** 
	 * 获取值 , 如果不存在则返回null
	 * 
	 * @param section 
	 * @param name 
	 * @return 
	 */
	public String getValue(String section, String name, boolean isMust) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			if (isMust) {
				throw new RuntimeException("[" + section + "] element: <" + name + "> is missing!");
			}

			return null;
		}

		String value = p.getProperty(name);
		if (value == null) {
			if (isMust) {
				throw new RuntimeException("[" + section + "] element: <" + name + "> is missing!");
			}
		}
		return value;
	}

	/** 
	 * 获取值 , 如果不存在则返回null
	 * 
	 * @param section 
	 * @param name 
	 * @return 
	 */
	public String getValue(String section, String name, String defaultValue) {
		Properties p = (Properties) sections.get(section);
		if (p == null) {
			return defaultValue;
		}

		String value = p.getProperty(name);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	/** 
	 * 获取section
	 * @param section 
	 * @param name 
	 * @return 
	 */

	public Properties getSection(String section) {
		Properties p = (Properties) sections.get(section);
		return p;
	}

	/** 
	 * 是否包含key 
	 * @param section 
	 * @param name 
	 * @return 
	 */
	public boolean containsKey(String section, String key) {
		Properties p = (Properties) sections.get(section);
		return p.contains(key);
	}

	/**
	 * 获取配置文件中所有配置区域的名称列表
	 * @return
	 */
	public List<String> getSectionList() {
		List<String> sections = new ArrayList<>();
		sections.addAll(sectionList);
		return sections;
	}

	/**
	 * 获取配置文件中所有配置区域的名称列表
	 * @return
	 */
	public List<String> getSectionList(String matcher) {
		List<String> sections = new ArrayList<>();
		for (String section : sectionList) {
			if (section.matches(matcher)) {
				sections.add(section);
			}
		}
		return sections;
	}

}