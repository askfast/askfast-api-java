package com.askfast.model;

import com.fasterxml.jackson.annotation.JsonCreator;


public enum AdapterType
{
    XMPP( "xmpp" ),
    SMS( "sms" ),
    CALL( "call" ),
    EMAIL( "email" ),
    FACEBOOK( "facebook" ),
    USSD( "ussd" ),
    PUSH( "notificare" ),
    TWITTER( "twitter" );

    private String value;

    private AdapterType( String value )
    {
        this.value = value;
    }
    
    public String getName()
    {
        return value;
    }

    /**
     * returns the enum based on the name or the value
     * 
     * @param value The value that is searched on
     * @return The corresponding type that matches
     */
    public static AdapterType getByValue(String value) {

        for (AdapterType type : values()) {
            if (type.getName().equalsIgnoreCase(value) || type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
    
    @JsonCreator
    public static AdapterType fromJson( String name )
    {
        return getByValue(name);
    }
}