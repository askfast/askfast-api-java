package com.askfast.model;

import com.askfast.util.JSONUtil;

public abstract class ModelBase
{
    public String toJSON()
    {
        String json = "{}";
        try
        {
            json = JSONUtil.serialize( this );
        }
        catch ( Exception e )
        {
        }
        return json;
    }
    
    public static <T extends ModelBase> T fromJSON(String json_string, Class<T> classType)
    {
        T dialogObject = null;
        try
        {
            dialogObject = JSONUtil.deserialize( json_string, classType );
        }
        catch ( Exception e )
        {
        }
        return dialogObject;
     }
}
