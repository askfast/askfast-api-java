package com.askfast.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * language maps to language-country enum value
 * 
 * @author Shravan
 *
 */
public enum Language {
        CATALAN("ca-es"),
        CHINESE("zh-cn"),
        CHINESE_HONGKONG("zh-hk"),
        CHINESE_TAIWAN("zh-tw"),
        DANISH("da-dk"),
        DUTCH("nl-nl", "nl"),
        ENGLISH_AUSTRALIA("en-au"),
        ENGLISH_CANADA("en-ca"),
        ENGLISH_GREATBRITAIN("en-gb"),
        ENGLISH_INDIA("en-in"),
        ENGLISH_UNITEDSTATES("en-us", "en"),
        FINNISH("fi-fi"),
        FRENCH_CANADA("fr-ca"),
        FRENCH_FRANCE("fr-fr"),
        GERMAN("de-de"),
        ITALIAN("it-it"),
        JAPANESE("ja-jp"),
        KOREAN("ko-kr"),
        NORWEGIAN("nb-no"),
        POLISH("pl-pl"),
        PORTUGUESE_BRAZIL("pt-br"),
        PORTUGUESE_PORTUGAL("pt-pt"),
        RUSSIAN("ru-ru"),
        SPANISH_MEXICO("es-mx"),
        SPANISH_SPAIN("es-es"),
        SWEDISH_SWEDEN("sv-se");

    String code = null;
    String secondaryCode = null;

    private Language(String languageCode) {

        this(languageCode, null);
    }

    private Language(String languageCode, String secondaryLangCode) {

        this.code = languageCode;
        this.secondaryCode = secondaryLangCode;
    }

    public String getCode() {

        return code;
    }

    public String getSecondaryCode() {

        return secondaryCode;
    }

    /**
     * Returns the enum based on the name or the value. if it doesnt match any
     * return {@link Language#DUTCH} by default
     * 
     * @param nameOrValue
     *            The value corresponding to which the language is to be fetched
     * @return Returns the Language enum based on the value given
     */
    public static Language getByValue(String nameOrValue) {

        for (Language type : values()) {
            //check if the given name matches the code (e.g nl-nl), or name (DUTCH) or atleast the secondaryCode (for backward compatibility e.g. nl)  
            if (type.getCode().equalsIgnoreCase(nameOrValue) || type.name().equalsIgnoreCase(nameOrValue) ||
                (type.getSecondaryCode() != null && type.getSecondaryCode().equals(nameOrValue))) {
                return type;
            }
        }
        return Language.DUTCH;
    }

    @JsonCreator
    public static Language fromJson(String name) {

        return getByValue(name);
    }
}
