package xyz.binfish.misa;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import xyz.binfish.misa.util.FileResourcesUtil;

public class AppInfo {

	private static AppInfo instance;

	public final String version;
	public final String groupId;
	public final String artifactId;

	private AppInfo() {
		Properties props = new Properties();

		try (InputStream input = FileResourcesUtil.getFileFromResourceAsStream("app.properties")) {
			props.load(input);
		} catch(IOException e) {
			e.printStackTrace();
		}

		this.version = props.getProperty("project.version");
		this.groupId = props.getProperty("project.groupId");
		this.artifactId = props.getProperty("project.artifactId");
	}

	public static AppInfo getAppInfo() {
		if(instance == null) {
			instance = new AppInfo();
		}
		return instance;
	}
}
