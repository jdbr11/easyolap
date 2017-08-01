/**
 * Project Name:common-utils
 * File Name:UtilProperties.java
 * Package Name:com.neusoft.module.utils
 * Date:2016年10月9日下午8:03:50
 * Copyright (c) 2016, Neusoft All Rights Reserved.
 *
 */

package org.easyolap.module.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName:UtilProperties <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年10月9日 下午8:03:50 <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.6
 * @see
 */
public class UtilProperties {
	private static Logger logger = LoggerFactory.getLogger(UtilProperties.class);
 
	public static String getValueByKey(String proFile, String key) {

		Properties props = new Properties();
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(proFile);

			props.load(in);
			String value = props.getProperty(key);
			return value;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public static Properties load(String proFile) throws IOException {
		InputStream in = null;
		try {
			Properties props = new Properties();
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(proFile);

			props.load(in);
			return props;
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static Map<Object, Object> getAllValue(String proFile) {
		Map<Object, Object> result = new HashMap<Object, Object>();
		
		try {
			Properties props = load(proFile);
			if (!props.isEmpty()) {
				logger.debug("proFile is not empty :"+proFile);
				Iterator it = props.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					Object key = entry.getKey();
					Object value = entry.getValue();
					result.put(key, value);
				}
				return result;
			}else{
				logger.debug("proFile is empty :"+proFile);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	// 写入properties信息
	public static void writeProperties(String filePath, String parameterName, String parameterValue) throws IOException {
		Properties prop = new Properties();
		InputStream fis = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
		// 从输入流中读取属性列表（键和元素对）
		prop.load(fis);
		// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
		// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
		OutputStream fos = new FileOutputStream(Thread.currentThread().getContextClassLoader().getResource(filePath).getPath());
		prop.setProperty(parameterName, parameterValue);
		// 以适合使用 load 方法加载到 Properties 表中的格式，
		// 将此 Properties 表中的属性列表（键和元素对）写入输出流
		prop.store(fos, "Update '" + parameterName + "' value");
		fos.flush();
		fos.close();
	}
}
