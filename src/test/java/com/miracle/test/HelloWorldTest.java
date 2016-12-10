package com.miracle.test;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.junit.ConcurrentParameterized;
import com.saucelabs.junit.SauceOnDemandTestWatcher;

@RunWith(ConcurrentParameterized.class)
public class HelloWorldTest implements SauceOnDemandSessionIdProvider {

	public String username = "kimandi";
	public String accesskey = "a48c69a7-bf64-4920-90bc-6e217d00ff1c";


	public SauceOnDemandAuthentication authentication = new SauceOnDemandAuthentication(
			username, accesskey);
	@Rule
	public SauceOnDemandTestWatcher resultReportingTestWatcher = new SauceOnDemandTestWatcher(
			this, authentication);
	@Rule
	public TestName name = new TestName() {
		@Override
		public String getMethodName() {
			return String.format("%s : (%s %s %s)", super.getMethodName(), os,
					browser, version);
		};
	};
	private final String browser, os, version, deviceName, deviceOrientation;
	private String sessionId;
	private WebDriver driver;
	String pageName = "";

	@Before
	public void setUp() throws Exception {
		DesiredCapabilities capabilities = new DesiredCapabilities();

		if (browser != null) {
			capabilities.setCapability(CapabilityType.BROWSER_NAME, browser);
		}
		if (version != null) {
			capabilities.setCapability(CapabilityType.VERSION, version);
		}
		if (deviceName != null) {
			capabilities.setCapability("deviceName", deviceName);
		}
		if (deviceOrientation != null) {
			capabilities.setCapability("device-orientation", deviceOrientation);
		}

		capabilities.setCapability(CapabilityType.PLATFORM, os);

		String methodName = name.getMethodName();
		capabilities.setCapability("name", methodName);

		this.driver = new RemoteWebDriver(new URL("http://"
				+ URLEncoder.encode(authentication.getUsername(), "UTF-8")
				+ ":" + authentication.getAccessKey()
				+ "@ondemand.saucelabs.com:80/wd/hub"), capabilities);
		this.sessionId = (((RemoteWebDriver) driver).getSessionId()).toString();

		String message = String.format(
				"SauceOnDemandSessionID=%1$s job-name=%2$s", this.sessionId,
				methodName);
		System.out.println(message);
	}

	public HelloWorldTest(String os, String version, String browser,
			String deviceName, String deviceOrientation) {
		super();
		this.os = os;
		this.version = version;
		this.browser = browser;
		this.deviceName = deviceName;
		this.deviceOrientation = deviceOrientation;

		String classname = Thread.currentThread().getStackTrace()[1]
				.getFileName();
		pageName = FilenameUtils.removeExtension(classname);

	}

	@ConcurrentParameterized.Parameters
	public static LinkedList browsersStrings() {
		LinkedList browsers = new LinkedList();
		browsers.add(new String[] { "Windows 7", "41", "chrome", null, null });
		return browsers;
	}

	public WebDriver getwebdriver() {
		return driver;
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
	}

	public String getSessionId() {
		return sessionId;
	}

	@Test
	public void CPTK_1000_spl100_New_flow() throws Exception {

			driver.manage().window().maximize();
			driver.get("http://java-cd-app-test.mybluemix.net/");
			String textfromid = driver.findElement(By.id("message")).getText();
			if(textfromid.endsWith("Hello World Watson!"))
			{
				System.out.println("Integration Test is a Success!");
			}else{
				throw new Exception("Failure");
			}
		}

}
