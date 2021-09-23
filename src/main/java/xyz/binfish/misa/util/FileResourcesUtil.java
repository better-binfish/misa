package xyz.binfish.misa.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URISyntaxException;

import xyz.binfish.misa.exception.FailedToLoadResourceException;

public class FileResourcesUtil {

	/*
	 * Get file as InputStream at specified path in resources.
	 *
	 * @param path the path to file in resources.
	 * @return InputStream object.
	 */
	public static InputStream getFileFromResourceAsStream(String path) {
		InputStream inputStream = FileResourcesUtil.class.getClassLoader()
			.getResourceAsStream(path);

		if(inputStream == null) {
			throw new FailedToLoadResourceException("File not found by path: " + path);
		} else {
			return inputStream;
		}
	}

	/*
	 * Get file as File at specified path in resources.
	 * Not working in JAR file.
	 *
	 * @param path the path to file in resources.
	 * @return File object. 
	 * @throws URISyntaxException to indicate that a string 
	 * could not be parsed as a URI reference.
	 */
	public static File getFileFromResource(String path) throws URISyntaxException {
		URL resource = FileResourcesUtil.class.getClassLoader().getResource(path);

		if(resource == null) {
			throw new FailedToLoadResourceException("File not found by path: " + path);
		} else {
			return new File(resource.toURI());
		}
	}
}
