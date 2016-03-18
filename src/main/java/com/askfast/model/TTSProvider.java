package com.askfast.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TTSProvider {

    ACAPELA, VOICE_RSS;

    /**
     * Returns the enum based on the name or the value
     * 
     * @param value The value for which the corresponding tts must be provided
     * @return The corresponding TTS enum
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