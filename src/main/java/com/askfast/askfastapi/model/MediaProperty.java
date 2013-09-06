package com.askfast.askfastapi.model;

import java.util.HashMap;
import java.util.Map;


public class MediaProperty
{
    public enum MediaPropertyKey
    {
        RedirectTimeOut( "timeout" ), AnswerInput( "answer_input" ), Length( "length" );

        @SuppressWarnings("unused")
        private String name;

        private MediaPropertyKey( String name )
        {
            this.name = name;
        }
    }
    
    public enum MediumType
    {
        Broadsoft, GTalk, Skype, SMS;
    }

    private MediumType medium;
    private Map<MediaPropertyKey, String> properties;

    public MediumType getMedium()
    {
        return medium;
    }

    public void setMedium( MediumType medium )
    {
        this.medium = medium;
    }

    public Map<MediaPropertyKey, String> getProperties()
    {
        return properties;
    }

    public void addProperty( MediaPropertyKey key, String value )
    {
        properties = properties != null ? properties : new HashMap<MediaPropertyKey, String>();
        properties.put( key, value );
    }
}