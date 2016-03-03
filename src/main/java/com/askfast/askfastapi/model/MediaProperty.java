package com.askfast.askfastapi.model;


import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;


public class MediaProperty
{
    public enum MediaPropertyKey
    {
        /**
         * defines the timeout associated with the call
         */
        TIMEOUT,
        /**
         * defines if the answer is given via dtmf, text etc
         */
        ANSWER_INPUT,
        /**
         * defines the length of th answer input. Typically dtmf
         */
        ANSWER_INPUT_MIN_LENGTH,
        ANSWER_INPUT_MAX_LENGTH,
        /**
         * defines a subtype for the question type. <br>
         * E.g. open question with type: audio refers to voicemail
         */
        TYPE,
        /**
         * defines the length of the voicemail to be recorded
         */
        VOICE_MESSAGE_LENGTH, 
        /**
         * defines the endpoint where the recording message must be posted to
         */
        VOICE_MESSAGE_URL,
        /**
         * defines the number of times the question should repeat in case of a wrong answer input.
         * works only for phonecalls so as to end a call with repeated input errors.
         */ 
        RETRY_LIMIT,
        /**
         * boolean flag to indicate if a voice mssage start should be indicated by a beep or not
         */
        VOICE_MESSAGE_BEEP,
        /**
         * boolean flag to indicate if the call should terminate when a dtmf is pressed
         */
        DTMF_TERMINATE,
        /**
         * defines the speed at which the TTS is spoken by the TTS engine
         */
        TSS_SPEED,
        /**
         * defines which caller id should be when redirecting a call. If this is set to true
         * the caller id of the connected user will be used. If false the adapterid caller id will
         * be used. (Default: false)
         */
        USE_EXTERNAL_CALLERID,
        /**
         * Pre-connect message. Gives opertunity to play a message before connecting the redirected call
         */
        USE_PRECONNECT,
        /**
         * The conference room name that has to be used to add a call to.
         */
        CONFERENCE_ROOM_NAME,
        /**
         * The conference starts when this flag is set to true. If not found,
         * the conference starts as soon as there are two people in the
         * conference
         */
        CONFERENCE_START_ON_CONNECT,
        /**
         * The conference ends when this flag is set to true. If not found, the
         * conference ends as soon as all participants leave the conference
         */
        CONFERENCE_END_ON_DISCONNECT,
        /**
         * A valid url must be set against this key. This audio is played when
         * participant is waiting in a conference room to be connected to other
         * participants
         */
        CONFERENCE_WAIT_URL,
        /**
         * Define how long you want the call to be in Secs
         */
        CALL_LENGTH,
        /**
         * Any time during the conference, if this is set to true. Will exit
         * the conference and perform the next sequence of control events
         */
        CONFERENCE_EXIT_ON_STAR, 
        /**
         * If this is set to true. Will generate the call transcript and POST it to the 
         * {@link MediaPropertyKey#TRANSCRIPT_URL}
         */
        TRANSCRIPT,
        /**
         * The URL to which the transcript is POSTed to. This is used only
         * if {@link MediaPropertyKey#TRANSCRIPT} is set to true
         */
        TRANSCRIPT_URL;
        
        @JsonCreator
        public static MediaPropertyKey fromJson(String name) {
            return valueOf(name.toUpperCase());
        }
    }

    public enum MediumType {

        EMAIL, BROADSOFT, GTALK, SKYPE, SMS, TWITTER;
        @JsonCreator
        public static MediumType fromJson(String name) {

            return valueOf(name.toUpperCase());
        }
    }
    
    private MediumType medium;
    private Map<MediaPropertyKey, String> properties;
    
    public MediaProperty() {
        properties = new HashMap<MediaPropertyKey, String>();
    }
    

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
        properties.put( key, value );
    }
}