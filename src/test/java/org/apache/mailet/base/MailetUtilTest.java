package org.apache.mailet.base;

import org.apache.mailet.base.test.MockMailetConfig;

import junit.framework.TestCase;

public class MailetUtilTest extends TestCase {

	private static final String A_PARAMETER = "aParameter";

	MockMailetConfig config;
	
	protected void setUp() throws Exception {
		super.setUp();
		config = new MockMailetConfig();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetInitParameterParameterIsTrue() {
		assertTrue(getParameterValued("true", true));
		assertTrue(getParameterValued("true", false));
		assertTrue(getParameterValued("TRUE", true));
		assertTrue(getParameterValued("TRUE", false));
		assertTrue(getParameterValued("trUE", true));
		assertTrue(getParameterValued("trUE", false));
	}

	public void testGetInitParameterParameterIsFalse() {
		assertFalse(getParameterValued("false", true));
		assertFalse(getParameterValued("false", false));
		assertFalse(getParameterValued("FALSE", true));
		assertFalse(getParameterValued("FALSE", false));
		assertFalse(getParameterValued("fALSe", true));
		assertFalse(getParameterValued("fALSe", false));
	}
	
	public void testGetInitParameterParameterDefaultsToTrue() {
		assertTrue(getParameterValued("fals", true));
		assertTrue(getParameterValued("TRU", true));
		assertTrue(getParameterValued("FALSEest", true));
		assertTrue(getParameterValued("", true));
		assertTrue(getParameterValued("gubbins", true));
	}
	
	public void testGetInitParameterParameterDefaultsToFalse() {
		assertFalse(getParameterValued("fals", false));
		assertFalse(getParameterValued("TRU", false));
		assertFalse(getParameterValued("FALSEest", false));
		assertFalse(getParameterValued("", false));
		assertFalse(getParameterValued("gubbins", false));
	}
	
	private boolean getParameterValued(String value, boolean defaultValue) {
		config.clear();
		config.setProperty(A_PARAMETER, value);
		return MailetUtil.getInitParameter(config, A_PARAMETER, defaultValue);
	}
}
