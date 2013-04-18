package com.askfast.examples;


import java.util.ArrayList;
import java.util.Arrays;

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
import com.askfast.model.AskFast.AskType;


@Path( "questionanswer" )
public class QuestionAnswer
{
    private ArrayList<String> cSampleAnswers = new ArrayList<String>(
        Arrays.asList( "Yup", "Nope" ) );
    private ArrayList<String> cSampleResponses = new ArrayList<String>(
        Arrays.asList( "Thanks for accepting the invitation!",
            "Thanks for responding to the invitation!",
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
        AskFast askFast = new AskFast( getUrl() );
        askFast.ask( "Are you free for the meeting", AskType.CLOSED, null );
        askFast.addAnswer( cSampleAnswers.get( 0 ), getUrl() + "/questions/10" );
        askFast.addAnswer( cSampleAnswers.get( 1 ), getUrl() + "/questions/11" );
        return askFast.endDialog();
    }

    @Path( "/questions/{question_no}" )
    @GET
    @Produces( "text/plain" )
    @Consumes( "*/*" )
    public Response getQuestionText(
        @PathParam( "question_no" ) String question_no,
        @QueryParam( "preferred_medium" ) String prefered_mimeType )
    {
        String result = null;
        if ( question_no.equals( "10" ) )
        {
            result = cSampleResponses.get( 0 );
        }
        else if ( question_no.equals( "11" ) )
        {
            result = cSampleResponses.get( 1 );
        }
        else
        {
            result = cSampleResponses.get( 2 );
        }
        return Response.ok( result ).build();
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
        AskFast askFast = new AskFast( getUrl() );
        if ( answerId.equals( "1" ) )
        {
            askFast.ask( "Are you free for the meeting", AskType.CLOSED, null );
            askFast.addAnswer( cSampleAnswers.get( 0 ), getUrl()
            + "/questions/10" );
            askFast.addAnswer( cSampleAnswers.get( 1 ), getUrl()
            + "/questions/11" );
        }
        else if ( answerId.equals( "10" ) )
        {
            askFast.say( cSampleResponses.get( 0 ) );
        }
        else if ( answerId.equals( "11" ) )
        {
            askFast.say( cSampleResponses.get( 1 ) );
        }
        else if ( answerId.equals( "3" ) )
        {
            askFast.say( cSampleResponses.get( 2 ) );
        }
        return askFast.endDialog();
    }
}
