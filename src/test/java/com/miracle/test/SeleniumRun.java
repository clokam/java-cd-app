package com.miracle.test;

import java.io.IOException;
import java.util.Date;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class SeleniumRun {
	@SuppressWarnings("unused")
	public static void main(String args[]) throws IOException {
		 System.out.println("Selenium tests being started..");
		 JUnitCore junit = new JUnitCore();
		 junit.run(HelloWorldTest.class);
	}
}
