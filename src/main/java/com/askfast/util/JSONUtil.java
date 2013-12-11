package com.askfast.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil
{
//    private static final Logger log = Logger.getLogger( JSONUtil.class.getName() );

    public static <T> T deserialize( String jsonString, Class<T> DeserializeClass )
    throws Exception
    {
        T deserializedEntity = null;
        if ( jsonString != null && !jsonString.isEmpty() )
        {
            deserializedEntity = new ObjectMapper().readValue( jsonString, DeserializeClass );
        }
        return deserializedEntity;
    }

    public static String serialize( Object objectToBeSerialized ) throws Exception
    {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.setSerializationInclusion( Include.NON_NULL );
        String result = null;
        if ( oMapper.canSerialize( objectToBeSerialized.getClass() ) )
        {
            result = oMapper.writeValueAsString( objectToBeSerialized );
        }
        return result;
    }
}
