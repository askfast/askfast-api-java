package com.askfast.model;

public class TTSInfo {
    
    protected String codec = "WAV";
    protected String speed = "0";
    protected TTSProvider provider = null;
    protected Language language = Language.DUTCH;
    protected String voiceUsed = "";
    
    public TTSInfo() {}
    
    public TTSInfo(TTSProvider provider, Language language, String voiceUsed) {
        this.provider = provider;
        this.language = language;
        this.voiceUsed = voiceUsed;
    }
    
    public String getCodec() {
        return codec;
    }
    
    public void setCodec( String codec ) {
        this.codec = codec;
    }
    
    public String getSpeed() {
        return speed;
    }
    
    public void setSpeed( String speed ) {
        this.speed = speed;
    }
    
    public TTSProvider getProvider() {
        return provider;
    }
    
    public void setProvider( TTSProvider provider ) {
        this.provider = provider;
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public void setLanguage( Language language ) {
        this.language = language;
    }
    
    public String getVoiceUsed() {
        return voiceUsed;
    }
    
    public void setVoiceUsed( String voiceUsed ) {
        this.voiceUsed = voiceUsed;
    }
}
