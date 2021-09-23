package xyz.binfish.misa.util;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ClassLoaderUtil extends ClassLoader {

	private static Map<String, byte[]> dataLoadedClasses = new HashMap();
	private static ClassLoaderUtil instance = null;

	private ClassLoaderUtil() { }

	public static ClassLoaderUtil getInstance() {
		if(instance == null) {
			instance = new ClassLoaderUtil();
		}

		return instance;
	}

	public static void close() {
		instance = null;
	}

	/*
	 * Loads all the classes in the given path.
	 *
	 * @param packageName the package path that should be loaded.
	 * @param classes     the ArrayList to storage the loaded classes.
	 * @return ArrayList with classes.
	 */
	public ArrayList<Class> getListClassesFromJar(String packageName, ArrayList<Class> classes) {
		String convertedPackage = packageName.replace('.', '/');

		URL toJar = getClass()
			.getProtectionDomain()
			.getCodeSource()
			.getLocation();

		try {
			JarInputStream jar = new JarInputStream(toJar.openStream());
			JarEntry jarEntry = null;

			while((jarEntry = jar.getNextJarEntry()) != null) {
				String name = jarEntry.getName();

				if(name.startsWith(convertedPackage) && name.endsWith(".class")) {
					int entrySize = (int) jarEntry.getSize();
					byte[] entryData = new byte[entrySize];

					jar.read(entryData, 0, entrySize);

					String className = name
						.replace('/', '.')
						.replace(".class", "");

					dataLoadedClasses.put(className, entryData);
					classes.add( loadClass(className) );
				}
			}

			jar.close();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return classes;
	}

	/*
	 * Loads all the classes in the given package path, with using recursion.
	 * Not working in JAR file.
	 * 
	 * @param packageName the package path that should be loaded.
	 * @param classes     the ArrayList to storage the loaded classes.
	 * @return ArrayList with classes.
	 */
	public ArrayList<Class> getListClassesFromDirectoryRecursive(String packageName, ArrayList<Class> classes) {
		//URL resource = java.lang.ClassLoader.getSystemClassLoader().getResource(packageName.replace('.', '/'));
		URL resource = super.getSystemClassLoader().getResource(packageName.replace('.', '/'));

		if(resource == null) {
			throw new RuntimeException("No resource for: " + packageName);
		}

		File directory = null;

		try {
			directory = new File(resource.toURI());
		} catch(URISyntaxException e) {
			throw new RuntimeException(packageName + " (" + resource + ") does not appear to be a valid URL / URI.", e);
		} catch(IllegalArgumentException e) {
		}

		if(directory.exists() && directory.isDirectory()) {
			for(File file : directory.listFiles()) {
				String filename = file.getName();

				if(file.isDirectory()) {
					getListClassesFromDirectoryRecursive((packageName + '.' + filename), classes);
				} else {
					if(filename.endsWith(".class")) {
						try {
							classes.add(Class.forName(packageName + '.' + filename.substring(0, filename.length() - 6)));
						} catch(ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}

			return classes;
		}

		return null;
	}

	@Override
	public Class findClass(String name) throws ClassNotFoundException {
		byte[] data = dataLoadedClasses.getOrDefault(name, new byte[0]);

		if(data.length == 0) {
			throw new ClassNotFoundException();
		}

		return defineClass(name, data, 0, data.length, null);
	}

	@Override
	public Class loadClass(String name) throws ClassNotFoundException {
		try {
			return super.loadClass(name);
		} catch(ClassNotFoundException e) {
			return findClass(name);
		}
	}
}
