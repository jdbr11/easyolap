/**
 * Project Name:test-pg
 * File Name:TestEncryption.java
 * Package Name:com.neusoft.encryption 
 * Copyright (c) 2016, Neusoft All Rights Reserved.
 *
 */

package org.easyolap.test;

import org.easyolap.module.utils.CodeTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ClassName:TestDbDemo <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * 
 * @author Administrator
 * @version
 * @since JDK 1.7
 * @see
 */
public class TestCodeTool {
	private static Logger logger = LoggerFactory.getLogger(TestCodeTool.class);

	@BeforeClass
	public void beforeClass() {
		logger.info("this is before class");
	}

	@Test
	public void testBytesToHexString() {
		String hexCode = CodeTool.bytesToHexString("16".getBytes());
		Assert.assertEquals("3136", hexCode);
	}

	@Test
	public void testHexStringToByte() {
		byte[] byteCode = CodeTool.hexStringToByte("3136");
		Assert.assertEquals("16".getBytes(),(byteCode));
	}

	@AfterClass
	public void afterClass() {
		logger.info("this is after class");
	}

}
