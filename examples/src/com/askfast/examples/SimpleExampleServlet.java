package com.askfast.examples;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.askfast.askfastapi.AskFast;

@SuppressWarnings("serial")
public class SimpleExampleServlet extends HttpServlet
{
	private ArrayList<String> cSampleAnswers = new ArrayList<String>(Arrays.asList("Yup", "Nope"));
        private ArrayList<String> cSampleResponses = new ArrayList<String>(
            Arrays.asList( "Thanks for your accepting!", "Thanks for your reply!",
                           "Something went wrong in this conversation.." ) );
	com.askfast.askfastapi.AskFast askFast;
	static final Logger log = Logger.getLogger(SimpleExampleServlet.class.getName());

	
    public void doGet( HttpServletRequest req, HttpServletResponse resp ) throws IOException
    {
        /**
         * if adapterId, accountId, refreshToken and toAddress is found as part of the query
         * parameter then initiate an outbound call using the firstQuestion()
         */
        if ( req.getParameter( "adapterId" ) != null && req.getParameter( "accountId" ) != null
            && req.getParameter( "refreshToken" ) != null && req.getParameter( "toAddress" ) != null )
        {
            askFast = new AskFast( req, req.getParameter( "accountId" ), req.getParameter( "refreshToken" ), null );
            String response;
            try
            {
                response = initiateOutboundCall( askFast, req.getParameter( "adapterId" ),
                    req.getParameter( "toAddress" ), req );
            }
            catch ( Exception e )
            {
                response = "Error caught while trying to initiate outboundCall. Message: "+ e.getLocalizedMessage();
            }
            //write the response back
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(response);
            resp.getWriter().flush();
            resp.getWriter().close();
        }
        else //generate a simple closed question
        {
            askFast = new AskFast( req );
            firstQuestion();
            askFast.render( resp );
        }
    }

    /**
     * collect the feedback of the answers
     */
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String questionId = req.getParameter("question_id");
		String preferredMedium = req.getParameter("preferred_medium");

		log.setLevel(Level.INFO);
		log.info(String.format(	"question_id: %s with and preferred_medium %s",
								questionId, preferredMedium));

		askFast = new AskFast(req);
		answerQuestion(questionId, preferredMedium);
		
		Logger log = Logger.getLogger(SimpleExampleServlet.class.getName());
		log.setLevel(Level.INFO);
		log.info("ending dialog " + askFast.render());
		
		askFast.render(resp);
	}

	public void answerQuestion(String answerId, String preferred_medium)
	{
		
		if (answerId.equals("1"))
		{
			askFast.ask("Are you coming to my bday party?", "");
			askFast.addAnswer(cSampleAnswers.get(0), getServletPath() + "?question_id=10");
			askFast.addAnswer(cSampleAnswers.get(1), getServletPath() + "?question_id=11");
		}
		else if (answerId.equals("10"))
		{
			askFast.say(cSampleResponses.get(0));
		}
		else if (answerId.equals("11"))
		{
			askFast.say(cSampleResponses.get(1));
		}
		else if (answerId.equals("3"))
		{
			askFast.say(cSampleResponses.get(2));
		}		
	}

	public void firstQuestion()
	{
		askFast.ask("Any plans for the day?", "");
		askFast.addAnswer(cSampleAnswers.get(0), getServletPath() + "?question_id=10");
		askFast.addAnswer(cSampleAnswers.get(1), getServletPath() + "?question_id=11");
	}
	
    public String getServletPath()
    {
        return "/simple_example";
    }
    
    private String initiateOutboundCall( AskFast askFast, String adapterId, String toAddress, HttpServletRequest req )
    throws Exception
    {
        return askFast.outBoundCall( adapterId, toAddress, askFast.getBaseURL() + req.getRequestURI() );
    }
}
