package com.askfast.examples;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.askfast.config.DialogSettings;
import com.askfast.model.AskFast;


@Path( "questionanswer" )
public class QuestionAnswer extends AskFast
{
    public QuestionAnswer()
    {
        super( getUrl() );
    }

    private ArrayList<String> cSampleAnswers = new ArrayList<String>(
        Arrays.asList( "Yup", "Nope" ) );
    private ArrayList<String> cSampleResponses = new ArrayList<String>(
        Arrays.asList( "Thanks for your accepting!",
            "Thanks for your reply!",
            "Something went wrong in this conversation.." ) );

    protected static String getUrl()
    {
        return DialogSettings.HOST + "/questionanswer";
    }

    @GET
    @Produces( "text/plain" )
    public Response firstQuestion(
        @QueryParam( "preferred_medium" ) String preferred_medium,
        @QueryParam( "responder" ) String responder )
    {
        ask( "Are you free for the meeting", null );
        addAnswer( cSampleAnswers.get( 0 ), getUrl() + "/questions/10" );
        addAnswer( cSampleAnswers.get( 1 ), getUrl() + "/questions/11" );
        return endDialog();
    }

    @Path( "/questions/{question_no}" )
    @GET
    @Produces( "text/plain" )
    @Consumes( "*/*" )
    public Response getQuestionText(
        @PathParam( "question_no" ) String question_no,
        @QueryParam( "preferred_medium" ) String prefered_mimeType )
    {
        if ( question_no.equals( "10" ) )
        {
            return say( cSampleResponses.get( 0 ) );
        }
        else if ( question_no.equals( "11" ) )
        {
            return say( cSampleResponses.get( 1 ) );
        }
        else
        {
            return say( cSampleResponses.get( 2 ) );
        }
    }

    @Path( "/questions/{id}" )
    @POST
    @Produces( "application/json" )
    @Consumes( "*/*" )
    public Response answerQuestion( String answer_json,
        @PathParam( "id" ) String answerId,
        @QueryParam( "preferred_medium" ) String preferred_medium,
        @QueryParam( "responder" ) String responder )
    {
        if ( answerId.equals( "1" ) )
        {
            ask( "Are you free for the meeting", null );
            addAnswer( cSampleAnswers.get( 0 ), getUrl() + "/questions/10" );
            addAnswer( cSampleAnswers.get( 1 ), getUrl() + "/questions/11" );
        }
        else if ( answerId.equals( "10" ) )
        {
            say( cSampleResponses.get( 0 ) );
        }
        else if ( answerId.equals( "11" ) )
        {
            say( cSampleResponses.get( 1 ) );
        }
        else if ( answerId.equals( "3" ) )
        {
            say( cSampleResponses.get( 2 ) );
        }

        Logger log = Logger.getLogger( Shravan1.class.getName() );
        log.setLevel( Level.INFO );
        Response endDialog = endDialog();
        log.info( "ending dialog " + endDialog.getEntity().toString() );
        return endDialog;
    }
}
