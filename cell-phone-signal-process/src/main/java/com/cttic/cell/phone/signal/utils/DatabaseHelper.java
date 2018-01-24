package com.cttic.cell.phone.signal.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.smart4j.framework.ConfigConstant;
//import com.smart4j.framework.utils.CollectionUtil;
//import com.smart4j.framework.utils.PropsUtil;

public final class DatabaseHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);

	private static ThreadLocal<Connection> CONNECTION_HOLDER;
	private static QueryRunner QUERY_RUNNER;

	// 添加数据库连接池
	private static BasicDataSource DATA_SOURCE;

	private static String DRIVER;
	private static String URL;
	private static String USERNAME;
	private static String PASSWORD;

	public DatabaseHelper(String DRIVER, String URL, String USERNAME, String PASSWORD) {
		DatabaseHelper.DRIVER = DRIVER;
		DatabaseHelper.URL = URL;
		DatabaseHelper.USERNAME = USERNAME;
		DatabaseHelper.PASSWORD = PASSWORD;

		init();
	}

	public static void init(String DRIVER, String URL, String USERNAME, String PASSWORD) {
		DatabaseHelper.DRIVER = DRIVER;
		DatabaseHelper.URL = URL;
		DatabaseHelper.USERNAME = USERNAME;
		DatabaseHelper.PASSWORD = PASSWORD;

		init();
	}

	private static void init() {
		CONNECTION_HOLDER = new ThreadLocal<Connection>();
		QUERY_RUNNER = new QueryRunner();

		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			LOGGER.error("can not load jdbc driver", e);
		}

		DATA_SOURCE = new BasicDataSource();
		DATA_SOURCE.setDriverClassName(DRIVER);
		DATA_SOURCE.setUrl(URL);
		DATA_SOURCE.setUsername(USERNAME);
		DATA_SOURCE.setPassword(PASSWORD);
	}

	/**
	 * 获取数据库连�?
	 */
	public static Connection getConnection() {
		Connection connection = CONNECTION_HOLDER.get();
		if (connection == null) {
			try {
				//				connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
				connection = DATA_SOURCE.getConnection();
			} catch (SQLException e) {
				LOGGER.error("get connection failure", e);
			} finally {
				CONNECTION_HOLDER.set(connection);
			}
		}
		return connection;
	}

	/**
	 * 关闭数据库连�?
	 * 
	 */
	public static void closeConnections() {
		//			Connection conn = CONNECTION_HOLDER.get();
		//			if (conn != null) {
		//				try {
		//					conn.close();
		//				} catch (SQLException e) {
		//					LOGGER.error("close connection failure", e);
		//					throw new RuntimeException(e);
		//				} finally {
		//					CONNECTION_HOLDER.remove();
		//				}
		//			}
		try {
			DATA_SOURCE.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询实体列表（List�?
	 * 
	 * @param entityClass
	 *            实体Class对象
	 * @param sql
	 *            ：要执行的sql语句
	 * @param params：条件参�?
	 * @return：List<T> 实体列表
	 */
	public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
		List<T> entityList;
		try {
			Connection conn = getConnection();
			/**
			 * BeanHandler     : 返回Bean对象 
			 * BeanListHandler : 返回list对象 
			 * BeanMapHandler  : 返回Map对象
			 * ArrayHandler    : 返回Object[]对象 
			 * ArrayListHandler: 返回List对象 
			 * MapHandler      : 返回Map对象
			 * MapListHandler  : 返回List对象 
			 * ScalarHandler   : 返回某列的�??
			 * ColumnListHandler: 返回某列的�?�列�?
			 * keyedHandler     : 返回Map对象，需要指定列�?
			 */
			entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
		} catch (SQLException e) {
			LOGGER.error("query entity list failure", e);
			throw new RuntimeException(e);
		}
		//		finally {
		//			closeConnection();
		//		}
		return entityList;
	}

	/**
	 * 查询实体
	 * @param entityClass
	 * @param sql
	 * @param params
	 * @return
	 */
	public static <T> T queryEntiry(Class<T> entityClass, String sql, Object... params) {
		T entity;
		try {
			Connection conn = getConnection();
			entity = QUERY_RUNNER.query(conn, sql, new BeanHandler<T>(entityClass), params);
		} catch (SQLException e) {
			LOGGER.error("query entity failure", e);
			throw new RuntimeException(e);
		}
		//		finally {
		//			closeConnection();
		//		}
		return entity;
	}

	/**
	 * 执行查询语句sql
	 * 返回 列表 - 列�?? 结果集列�?
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static List<Map<String, Object>> executeQuery(String sql, Object... params) {
		List<Map<String, Object>> result;
		try {
			Connection conn = getConnection();
			result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			LOGGER.error("execute query sql failure", e);
			throw new RuntimeException(e);
		}
		//		finally {
		//			closeConnection();
		//		}
		return result;
	}

	/**
	 * 执行更新语句（包括：update，insert�? delete�?
	 * 返回执行后受影响的行�?
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int executeUpdate(String sql, Object... params) {
		System.out.println("@@@@@@@@@@@@@@ executeUpdate ... sql=" + sql);
		int rows = 0;
		try {
			Connection conn = getConnection();
			rows = QUERY_RUNNER.update(conn, sql, params);
		} catch (SQLException e) {
			LOGGER.error("execute update failure", e);
			throw new RuntimeException(e);
		}
		//		finally {
		//			closeConnection();
		//		}
		return rows;
	}

	/**
	 * 插入�?条记�?
	 * 
	 * @param entityClass
	 * @param fieldMap
	 * @return
	 */
	public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
		if (CollectionUtil.isEmpty(fieldMap)) {
			LOGGER.error("Cannot insert entity: Map<String, Object> fileMap is empty");
			return false;
		}

		String sql = "insert into " + getTableName(entityClass);
		StringBuilder columns = new StringBuilder("(");
		StringBuilder values = new StringBuilder("(");

		for (String fieldName : fieldMap.keySet()) {
			columns.append(fieldName).append(",");
			values.append("?,");
		}

		// 将最后一�?"," 替换�? ")"
		columns.replace(columns.lastIndexOf(","), columns.length(), ")");
		values.replace(values.lastIndexOf(","), values.length(), ")");

		sql = sql + columns + " Values " + values;

		Object[] params = fieldMap.values().toArray();

		// �?查执行后受影响的行是否为1�? ==1说明执行成功�?
		return executeUpdate(sql, params) == 1;
	}

	/**
	 * 更新实体�? 更新�?条记�?
	 * 
	 * @param entityClass
	 * @param id
	 * @param fieldMap
	 * @return
	 */
	public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
		if (CollectionUtil.isEmpty(fieldMap)) {
			LOGGER.error("update entity failure: fieldMap is empty!");
			return false;
		}

		String sql = "update table " + getTableName(entityClass) + " set ";
		StringBuilder columns = new StringBuilder();
		for (String fieldName : fieldMap.keySet()) {
			columns.append(fieldName).append("=?, ");
		}

		sql = sql + columns.substring(0, columns.lastIndexOf(",")) + " where id=?";

		List<Object> paramList = new ArrayList<Object>();
		paramList.addAll(fieldMap.values());
		paramList.add(id);

		Object[] params = paramList.toArray();

		return executeUpdate(sql, params) == 1;
	}

	/**
	 * 删除实体�? 删除�?条记�?
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
		String sql = "delete from " + getTableName(entityClass) + " where id=?";
		return executeUpdate(sql, id) == 1;
	}

	/**
	 * 返回Class对象的简单名称， 即类的名�?
	 * @param entityClass
	 * @return
	 */
	private static String getTableName(Class<?> entityClass) {
		return entityClass.getSimpleName();
	}

}
