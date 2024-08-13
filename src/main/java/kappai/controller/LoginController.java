package kappai.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.receiver.RequestReplyMessageReceiver;
import com.solace.messaging.resources.TopicSubscription;

import kappai.dao.UserDAO;
import kappai.model.UserVO;

/**
 * This class demonstrates the usage of the Solace Java API to create a Replier class.
 * This implementation focuses on the blocking behaviour of the API.
 * The mechanism of the Request-Reply pattern is defined in more detail over here : <a href="https://tutorials.solace.dev/jcsmp/request-reply/">Solace Request/Reply pattern</a>
 * <p>
 * Refer to the DirectRequestorBlocking class for the request component of the flow.
 */
public class LoginController {

    private static final String SAMPLE_NAME = LoginController.class.getSimpleName();
    private static final String TOPIC = "gf/user/login/req";  // used as the topic "root"
    private static final String API = "Java";
    private static volatile boolean isShutdown = false;          // are we done yet?

    public static void main(String... args) throws IOException {

        //1. Make sure that you have all the connection parameters.
        if (args.length < 3) {  // Check command line arguments
            System.out.printf("Usage: %s <host:port> <message-vpn> <client-username> [password]%n%n", SAMPLE_NAME);
            System.exit(-1);
        }
        System.out.println(API + " " + SAMPLE_NAME + " initializing...");

        //2. Set up the properties including username, password, vpnHostUrl and other control parameters.
        final Properties properties = new Properties();
        setupPropertiesForConnection(properties, args);

        //3. Create the MessagingService object and establishes the connection with the Solace event broker
        final MessagingService messagingService = com.solace.messaging.MessagingService.builder(ConfigurationProfile.V1).fromProperties(properties).build();
        messagingService.connect();  // blocking connect to the broker

        //4. Register event handlers and callbacks for connection error handling.
        setupConnectivityHandlingInMessagingService(messagingService);

        //5. Build and start the Receiver object
        RequestReplyMessageReceiver requestReplyMessageReceiver = messagingService.requestReply().createRequestReplyMessageReceiverBuilder().build(TopicSubscription.of(TOPIC));
        requestReplyMessageReceiver.start();
        //5-A. Set up an event handler for situations where the reply message could not be published.
        requestReplyMessageReceiver.setReplyFailureListener(failedReceiveEvent -> System.out.println("### FAILED RECEIVE EVENT " + failedReceiveEvent));

        //6. Create an OutboundMessageBuilder for building the outbound reply message
        final OutboundMessageBuilder outboundMessageBuilder = messagingService.messageBuilder();

        //7. Define the handler for the incoming message.
        final RequestReplyMessageReceiver.RequestMessageHandler messageHandler = (inboundMessage, replier) -> {

            //This SOP is just for demo purposes, ideally considering the slow nature of console I/O, any such action should be avoided in message processing
//          System.out.println("The inbound message is : " + inboundMessage.dump()); // Enable this for learning purposes as it logs a String representation of the whole Message

        	/************* THIS IS WHERE DAO IS CALLED AND RESPONSE IS RECIEVED********/

        	final String stringPayload = inboundMessage.getPayloadAsString();
            System.out.println("The converted message payload is : " + stringPayload);
            
            //Call DAO
           
            String jsonResponse = callLoginDAO(stringPayload);
            
            final OutboundMessage outboundMessage = outboundMessageBuilder.build(jsonResponse);

            //This SOP is just for demo purposes, ideally considering the slow nature of console I/O, any such action should be avoided in message processing
            System.out.println("The outbound message is : " + outboundMessage.getPayloadAsString());
            
            /************* DAO CALLED AND RESPONSE DONE ********/


            //7-D. Post the reply to the incoming message
            replier.reply(outboundMessage);
        };

        //8. Loop to identify message discards or errors and terminate if required. This should be handled in a more resilient manner
        System.out.println(API + " " + SAMPLE_NAME + " connected, and running.");
        while (System.in.available() == 0 && !isShutdown) {
            requestReplyMessageReceiver.receiveMessage(messageHandler, 1000);
        }
        isShutdown = true;
        requestReplyMessageReceiver.terminate(500);
        messagingService.disconnect();
        System.out.println("Main thread quitting.");
        System.exit(0);
    }

    private static void setupPropertiesForConnection(final Properties properties, final String... args) {
        properties.setProperty(SolaceProperties.TransportLayerProperties.HOST, args[0]);          // host:port
        properties.setProperty(SolaceProperties.ServiceProperties.VPN_NAME, args[1]);     // message-vpn
        properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_USER_NAME, args[2]);      // client-username
        if (args.length > 3) {
            properties.setProperty(SolaceProperties.AuthenticationProperties.SCHEME_BASIC_PASSWORD, args[3]);  // client-password
        }
        properties.setProperty(SolaceProperties.ServiceProperties.RECEIVER_DIRECT_SUBSCRIPTION_REAPPLY, "true");  // subscribe Direct subs after reconnect
        properties.setProperty(SolaceProperties.TransportLayerProperties.RECONNECTION_ATTEMPTS, "20");  // recommended settings
        properties.setProperty(SolaceProperties.TransportLayerProperties.CONNECTION_RETRIES_PER_HOST, "5");
        // https://docs.solace.com/Solace-PubSub-Messaging-APIs/API-Developer-Guide/Configuring-Connection-T.htm
    }

    private static void setupConnectivityHandlingInMessagingService(final MessagingService messagingService) {
        messagingService.addServiceInterruptionListener(serviceEvent -> {
            System.out.println("### SERVICE INTERRUPTION: " + serviceEvent.getCause());
            isShutdown = true;
        });
        messagingService.addReconnectionAttemptListener(serviceEvent -> System.out.println("### RECONNECTING ATTEMPT: " + serviceEvent));
        messagingService.addReconnectionListener(serviceEvent -> System.out.println("### RECONNECTED: " + serviceEvent));
    }
    
    
    /*
     * callLoginDAO is the method to call the UserDAO and it's methods
     * It takes in an input json string and returns and output json string
     */
    private static String callLoginDAO(String jsonInput) {
    	
    	String jsonResponse = null;

    	try {
			
    		//extract Json
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
   
			System.out.println("username="+jsonObject.getString("username"));
			System.out.println("password="+jsonObject.getString("password"));
			
			//set VO
	    	UserDAO userDAO = new UserDAO();
	        UserVO userVOInput = new UserVO();
	        UserVO userVOOutput = new UserVO();
	        
	        userVOInput.setUsername(jsonObject.getString("username"));
	        userVOInput.setPassword(jsonObject.getString("password"));
	        
	        userVOOutput = userDAO.loginUser(userVOInput);
	        //System.out.println("userVOInput="+userVOInput);
	        //System.out.println("userVOOutput="+userVOOutput);
	        
	        if (userVOOutput!=null && userVOOutput.getFirstName()!=null) {
	        	
	        	//login successful
	        	jsonResponse ="{\"status\":\"success\", \"firstName\":\""+userVOOutput.getFirstName()+ "\"}";
	        }
	        else {
	        	//login failed
	        	jsonResponse ="{\"status\":\"error\"}";
	        	
	        }
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
    }
}