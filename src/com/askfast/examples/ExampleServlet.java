
package com.askfast.examples;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import com.askfast.config.DialogSettings;
import com.askfast.model.AskFast;
import com.askfast.model.AskFast.AskType;


@SuppressWarnings( "serial" )
public class ExampleServlet extends HttpServlet
{
    private ArrayList<String> cSampleAnswers = new ArrayList<String>(
        Arrays.asList( "Yup", "Nope" ) );
    private ArrayList<String> cSampleResponses = new ArrayList<String>(
        Arrays.asList( "Thanks for your accepting!",
            "Thanks for your reply!",
            "Something went wrong in this conversation.." ) );
    AskFast cAskFast;
    private String url;
    static final Logger log = Logger.getLogger( ExampleServlet.class.getName() );

    public void doGet( HttpServletRequest req, HttpServletResponse resp )
    throws IOException
    {
        //setUrl( req.getServerName() + req.getServletPath() );
        cAskFast = new AskFast( getUrl() );
        resp.setContentType( "text/plain" );
        resp.getOutputStream().println( firstQuestion().getEntity().toString() );
    }

    public void doPost( HttpServletRequest req, HttpServletResponse resp )
    throws IOException
    {
        String[] questionId = req.getParameterValues( "question_id" );
        String[] value = req.getParameterValues( "value" );
        String[] preferred_medium = req.getParameterValues( "preferred_medium" );

        log.setLevel( Level.INFO );
        log.info( String.format( "question_id: %s with value %s and preferred_medium %s",
            questionId[0],
            value[0],
            preferred_medium[0] ) );

        resp.getOutputStream().println( answerQuestion( value[0],
            questionId[0],
            preferred_medium[0] ).getEntity().toString() );
    }

    public Response answerQuestion( String answer_json, String answerId,
        String preferred_medium )
    {
        cAskFast = new AskFast( getUrl() );
        if ( answerId.equals( "1" ) )
        {
            cAskFast.ask( "Are you coming to my bday party?",
                AskType.CLOSED,
                null );
            cAskFast.addAnswer( cSampleAnswers.get( 0 ), getUrl()
            + "?question_id=10" );
            cAskFast.addAnswer( cSampleAnswers.get( 1 ), getUrl()
            + "?question_id=11" );
        }
        else if ( answerId.equals( "10" ) )
        {
            cAskFast.say( cSampleResponses.get( 0 ) );
        }
        else if ( answerId.equals( "11" ) )
        {
            cAskFast.say( cSampleResponses.get( 1 ) );
        }
        else if ( answerId.equals( "3" ) )
        {
            cAskFast.say( cSampleResponses.get( 2 ) );
        }

        Logger log = Logger.getLogger( Shravan1.class.getName() );
        log.setLevel( Level.INFO );
        Response endDialog = cAskFast.endDialog();
        log.info( "ending dialog " + endDialog.getEntity().toString() );
        return endDialog;
    }

    public Response firstQuestion()
    {
        cAskFast.ask( "Any plans for the day?", AskType.CLOSED, null );
        cAskFast.addAnswer( cSampleAnswers.get( 0 ), getUrl()
        + "?question_id=10" );
        cAskFast.addAnswer( cSampleAnswers.get( 1 ), getUrl()
        + "?question_id=11" );
        return cAskFast.endDialog();
    }

    public String getUrl()
    {
        return DialogSettings.HOST + "/exampleservlet";
    }
}
