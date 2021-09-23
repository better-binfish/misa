package xyz.binfish.misa.database;

import xyz.binfish.misa.database.Database;
import xyz.binfish.misa.database.DatabaseManager;

import java.io.File;

public abstract class FileDatabase extends Database {

	/*
	 * The directory/folder the database is stored in.
	 */
	private String directory;

	/*
	 * The name of the database file.
	 */
	private String filename;

	/*
	 * The extension of the database file.
	 */
	private String extension;

	/*
	 * The database file.
	 */
	private File file;

	/*
	 * Creating a new instance of the file database.
	 *
	 * @param dbm the database manager class instance.
	 */
	public FileDatabase(DatabaseManager dbm) {
		super(dbm);

		file = null;
	}

	/*
	 * Returns the directory name the database is stored in.
	 *
	 * @return the database directory.
	 */
	public String getDirectory() {
		return directory;
	}

	/*
	 * Sets the directory name the database is stored in.
	 *
	 * @param directory the directory the database is stored in.
	 */
	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/*
	 * Returns the database file name.
	 *
	 * @return the database file name.
	 */
	public String getFilename() {
		return filename;
	}

	/*
	 * Sets the database file name.
	 *
	 * @param filename the database file name.
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/*
	 * Returns the database extension of the file name.
	 *
	 * @return the database extension.
	 */
	public String getExtension() {
		return extension;
	}

	/*
	 * Sets the database file name extension.
	 *
	 * @param extension the database extension.
	 */
	public void setExtension(String extension) {
		if(!extension.startsWith(".")) {
			extension = "." + extension;
		}

		this.extension = extension;
	}

	/*
	 * Returns the database file object.
	 *
	 * @return File.
	 */
	public File getFile() {
		return file;
	}

	/*
	 * Sets and creates the database file object from the given values.
	 *
	 * @param directory the directory the database is stored in.
	 * @param filename  the database file name.
	 * @param extension the database file extension.
	 * @throws RuntimeException
	 */
	protected void setFile(String directory, String filename, String extension) throws RuntimeException {
		setExtension(extension);
		setDirectory(directory);
		setFilename(filename);

		File storingDirectory = new File(getDirectory());
		if(!storingDirectory.exists()) {
			storingDirectory.mkdir();
		}

		file = new File(storingDirectory.getAbsolutePath() + File.separator + getFilename() + getExtension());
	}

}
