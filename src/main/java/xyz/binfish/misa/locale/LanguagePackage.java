package xyz.binfish.misa.locale;

public class LanguagePackage extends JsonReaderAdapter {

	private final String code;
	private final String nativeName;
	private final String englishName;

	LanguagePackage(String language, String country, String nativeName, String englishName) {
		super(String.format("lang/%s_%s.json", language, country));
		this.code = language + "_" + country;
		this.nativeName = nativeName;
		this.englishName = englishName;
	}

	/*
	 * Gets the language code for the current language.
	 *
	 * @return the language code for the current language.
	 */
	public String getCode() {
		return code;
	}

	/*
	 * Gets the native name for the current language.
	 *
	 * @return the native name for the current language.
	 */
	public String getNativeName() {
		return nativeName;
	}

	/*
	 * Gets the english version of the name for the current language.
	 *
	 * @return the english version of the name for the current language.
	 */
	public String getEnglishName() {
		return englishName;
	}
}
