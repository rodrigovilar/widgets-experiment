package br.edu.ufcg.virtus.widgets.geneguis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ufcg.embedded.ise.geneguis.PropertyTypeType;
import br.edu.ufcg.embedded.ise.geneguis.TagType;
import br.edu.ufcg.embedded.ise.geneguis.WidgetType;
import br.edu.ufcg.embedded.ise.geneguis.backend.EntryPoint;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.EntityTypeDeployRest;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.PortRest;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.RuleRest;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.TagRuleRest;
import br.edu.ufcg.embedded.ise.geneguis.backend.controller.WidgetRest;
import net.jsourcerer.webdriver.jserrorcollector.JavaScriptError;

public abstract class WebBrowserTestCase {

	static WidgetType EntityTypeSet = WidgetType.EntityTypeSet;
	static WidgetType EntityType = WidgetType.EntityType;
	static WidgetType Entity = WidgetType.Entity;
	static WidgetType PropertyType = WidgetType.PropertyType;
	static WidgetType Property = WidgetType.Property;
	static WidgetType RelationshipType = WidgetType.RelationshipType;
	static WidgetType Relationship = WidgetType.Relationship;
	static WidgetType FieldType = WidgetType.FieldType;
	static WidgetType Field = WidgetType.Field;
	static WidgetType EnumerationValue = WidgetType.EnumerationValue;

	static String SERVER_URL = "http://localhost:8080/";

	public static WebDriver driver;

	@BeforeClass
	public static void openBrowser() throws IOException {
		if (driver == null) {
			FirefoxProfile ffProfile = new FirefoxProfile();
			JavaScriptError.addExtension(ffProfile);
			driver = new FirefoxDriver(ffProfile);
			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		}
	}

	@Before
	public void clear() {
		driver.get(SERVER_URL + "test/reset");
	}

	static <T> void deployEntityType(Class<T> entityType, Class<?> repository) throws Exception {
		EntityTypeDeployRest rest = new EntityTypeDeployRest();
		rest.setEntity(entityType.getName());
		rest.setRepository(repository.getName());
		postJSON(SERVER_URL + "entities", rest);
	}

	static void openApp() {
		WebBrowserTestCase.driver.get(SERVER_URL);
	}

	static void widget(String name, WidgetType type, PortRest... ports) {
		WidgetRest widget = new WidgetRest();
		widget.setName(name);
		widget.setType(type.name());
		widget.setRequiredPorts(Arrays.asList(ports));

		postJSON(SERVER_URL + "widgets", widget);

		post(SERVER_URL + "widgets/" + name + "/code", readWidgetFile(name));
	}

	@SuppressWarnings("unchecked")
	static <T> T postJSON(String url, T data) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			ObjectMapper mapper = new ObjectMapper();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			mapper.setDateFormat(df);

			StringEntity input = new StringEntity(mapper.writeValueAsString(data));
			input.setContentType("application/json");
			HttpPost request = new HttpPost(url);
			request.setEntity(input);
			CloseableHttpResponse response = httpClient.execute(request);

			Assert.assertEquals(201, response.getStatusLine().getStatusCode());

			String json = EntityUtils.toString(response.getEntity(), "UTF-8");

			return (T) mapper.readValue(json, data.getClass());

		} catch (IOException e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}

	static String readWidgetFile(String fileName) {
		URL resource = EntryPoint.class.getResource("/widgets/" + fileName + ".hbs");
		File file = new File(resource.getFile());
		String filePath = file.getAbsolutePath();
		try {
			Path widgetPath = Paths.get(filePath);
			return new String(Files.readAllBytes(widgetPath));
		} catch (Exception e) {
			Assert.fail(e.getMessage());
			return null;
		}
	}

	static String post(String url, String data) {

		try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

			StringEntity input = new StringEntity(data);
			input.setContentType("application/json");
			HttpPost request = new HttpPost(url);
			request.setEntity(input);
			CloseableHttpResponse response = httpClient.execute(request);

			Assert.assertEquals(201, response.getStatusLine().getStatusCode());

			return EntityUtils.toString(response.getEntity());

		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
		return null;
	}

	static void rule(String port, String entityScope, String propertyScope, PropertyTypeType pttype, String widget,
			WidgetType type) {
		RuleRest rule = new RuleRest(widget, entityScope, propertyScope, pttype, port, type.name());
		postJSON(SERVER_URL + "rules", rule);
	}

	static void rule(String port, String entityScope, String propertyScope, String widget, WidgetType type) {
		RuleRest rule = new RuleRest(widget, entityScope, propertyScope, null, port, type.name());
		postJSON(SERVER_URL + "rules", rule);
	}

	static void rule(String port, String entityScope, String widget, WidgetType type) {
		RuleRest rule = new RuleRest(widget, entityScope, "*", null, port, type.name());
		postJSON(SERVER_URL + "rules", rule);
	}

	static void ruleByTag(String port, String tag, String widget, WidgetType type) {
		RuleRest rule = new RuleRest(widget, null, "*", null, port, type.name());
		rule.setTag(tag);
		postJSON(SERVER_URL + "rules", rule);
	}

	static void rule(String port, String widget, WidgetType type) {
		RuleRest rule = new RuleRest(widget, "*", "*", null, port, type.name());
		postJSON(SERVER_URL + "rules", rule);
	}

	static void tagRule(String tagName, TagType tagType, String tagValue, String entityLocator, String fieldLocator) {
		TagRuleRest tagRule = new TagRuleRest(tagName, tagType.name(), tagValue, entityLocator, fieldLocator);
		postJSON(SERVER_URL + "tags", tagRule);
	}
	
	static void clickEntityType(Class<?> entityType) {
		click(By.id("entityType_" + entityType.getSimpleName()));
	}

	static void click(By by) {
		checkId(by).click();
	}

	static WebElement checkId(By by) {
		return WebBrowserTestCase.driver.findElement(by);
	}

	static void checkTitle(Class<?> entityType) {
		checkId(By.id("title_" + entityType.getSimpleName()));
	}


}
