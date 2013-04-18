
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


@Path( "/shravan1" )
public class Shravan1
{
    private ArrayList<String> cSampleAnswers = new ArrayList<String>(
        Arrays.asList( "Yup", "Nope" ) );
    private ArrayList<String> cSampleResponses = new ArrayList<String>(
        Arrays.asList( "Thanks for making time!",
            "We will miss you!",
            "Something went wrong in this conversation.." ) );

    static final Logger log = Logger.getLogger( Shravan1.class.getName() );

    @GET
    @Produces( "text/plain" )
    public Response firstQuestion(
        @QueryParam( "preferred_medium" ) String preferred_medium,
        @QueryParam( "responder" ) String responder )
    {
        AskFast askFast = new AskFast( getUrl() );
        askFast.ask( "Are you coming to my bday party at Rotterdam?", null );
        askFast.addAnswer( cSampleAnswers.get( 0 ), getUrl() + "/questions/10" );
        askFast.addAnswer( cSampleAnswers.get( 1 ), getUrl() + "/questions/11" );
        askFast.addAnswer( "Appointment", getUrl() + "/questions/12" );
        return askFast.endDialog();
    }

    protected static String getUrl()
    {
        return DialogSettings.HOST + "/shravan1";
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
        else if ( question_no.equals( "12" ) )
        {
            result = "Transferring you to Appointment agent";
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
        log.setLevel( Level.WARNING );
        log.info( "answer entered is: " + answer_json );

        AskFast askFast = new AskFast( getUrl() );
        if ( answerId.equals( "1" ) )
        {
            askFast.ask( "Are you coming to my bday party at Rotterdam?", null );
            askFast.addAnswer( "Yup", getUrl() + "/questions/10" );
            askFast.addAnswer( "Nope", getUrl() + "/questions/11" );
        }
        else if ( answerId.equals( "10" ) )
        {
            return askFast.say( cSampleResponses.get( 0 ) );
        }
        else if ( answerId.equals( "11" ) )
        {
            return askFast.say( cSampleResponses.get( 1 ) );
        }
        else if ( answerId.equals( "12" ) )
        {
            return askFast.redirect( getUrl() + "/questions/" + answerId,
                DialogSettings.HOST + "/questionanswer" );
        }
        else if ( answerId.equals( "3" ) )
        {
            return askFast.say( cSampleResponses.get( 2 ) );
        }
        return askFast.endDialog();
    }
}
