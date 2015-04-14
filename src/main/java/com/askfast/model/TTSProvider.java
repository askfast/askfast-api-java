package com.askfast.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TTSProvider {

    ACAPELA, VOICE_RSS;

    /**
     * returns the enum based on the name or the value
     * 
     * @param value
     * @return
     */
    @JsonCreator
    public static TTSProvider getByValue(String value) {

        for (TTSProvider provider : values()) {
            if (provider.name().equalsIgnoreCase(value)) {
                return provider;
            }
        }
        return null;
    }
}