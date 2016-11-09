package com.miracle.test;

import junit.framework.Assert;
import org.junit.Test;
import wasdev.sample.functions.*;

public class MessageTest {

	@Test
	public void testGetMessage() {

		Assert.assertEquals(Message.getMessage(), "Watson");

	}

}
