package com.askfast.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum AccountType
{
    TRIAL, PRE_PAID, POST_PAID;
    
    @JsonCreator
    public static AccountType fromJson(String name) {

        if (name != null) {
            return AccountType.valueOf(name.toUpperCase());
        }
        return null;
    }
}
