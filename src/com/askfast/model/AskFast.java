
package com.askfast.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.askcs.dialog.sdk.QuestionBuilder;
import com.askcs.dialog.sdk.model.Answer;
import com.askcs.dialog.sdk.model.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class AskFast
{
    private Question cQuestion;
    private String baseURL;

    private String getBaseURL()
    {
        return baseURL;
    }

    private void setBaseURL( String baseURL )
    {
        this.baseURL = baseURL;
    }

    public AskFast( String url )
    {
        setBaseURL( url );
        if ( cQuestion == null )
        {
            cQuestion = new Question( "1", "text://", null );
        }
    }

    public Response say( String value )
    {
        cQuestion = new Question( UUID.randomUUID().toString(), value,
            Question.QUESTION_TYPE_COMMENT );
        return endDialog();
    }

    public Response ask( String askText, String next )
    {
        cQuestion.setQuestion_text( askText );
        cQuestion.setType( Question.QUESTION_TYPE_CLOSED );
        if ( next != null )
        {
            cQuestion.setAnswers( new ArrayList<Answer>(
                Arrays.asList( new Answer( null, next ) ) ) );
        }
        return Response.ok().build();
    }

    public Response addAnswer( String answerText, String next )
    {
        if ( cQuestion == null || next == null )
        {
            return Response.status( Status.NOT_ACCEPTABLE ).build();
        }
        ArrayList<Answer> tAllAnswers = cQuestion.getAnswers();
        if ( tAllAnswers == null )
        {
            tAllAnswers = new ArrayList<Answer>();
        }

        Answer answer = new Answer( answerText, next );
        tAllAnswers.add( answer );
        cQuestion.setAnswers( tAllAnswers );

        return Response.ok( tAllAnswers ).build();
    }

    public Response redirect( String redirectText, String next )
    {
        ObjectMapper om = new ObjectMapper();
        ObjectNode node = om.createObjectNode();
        node.put( "type", Question.QUESTION_TYPE_REFERRAL );
        node.put( "url", next );
        if ( redirectText.contains( "http" ) || redirectText.contains( "https" ) )
        {
            node.put( "question_text", redirectText );
        }
        else
        {
            node.put( "question_text", "text://" + redirectText );
        }
        return Response.ok( node.toString() ).build();
    }

    public Response getAnswerText( String answerId )
    {
        Iterator<Answer> answerIterator = cQuestion.getAnswers().iterator();
        String result = null;
        while ( answerIterator.hasNext() )
        {
            Answer answer = answerIterator.next();
            if ( answer.getAnswer_id().equals( answerId ) )
            {
                result = answer.getAnswer_text();
                break;
            }
        }

        if ( result != null )
        {
            return Response.ok( result ).build();
        }
        else
        {
            return Response.status( Status.NOT_ACCEPTABLE ).build();
        }
    }

    @SuppressWarnings( "deprecation" )
    public Response endDialog()
    {
        String questionBuiltString = QuestionBuilder.build( cQuestion,
            getBaseURL(),
            null );
        return Response.ok( questionBuiltString ).build();
    }
}
