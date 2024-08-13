package kappai.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.receiver.RequestReplyMessageReceiver;
import com.solace.messaging.resources.TopicSubscription;

import kappai.dao.ItemDAO;
import kappai.dao.UserDAO;
import kappai.model.ItemVO;
import kappai.model.UserVO;

/**
 * This class demonstrates the usage of the Solace Java API to create a Replier class.
 * This implementation focuses on the blocking behaviour of the API.
 * The mechanism of the Request-Reply pattern is defined in more detail over here : <a href="https://tutorials.solace.dev/jcsmp/request-reply/">Solace Request/Reply pattern</a>
 * <p>
 * Refer to the DirectRequestorBlocking class for the request component of the flow.
 * 
 * 
 * Supported topics in vafriable requestTopic: 
 * gf/item/req/get/list
 * gf/item/req/get/item
 * gf/item/req/create/item
 * gf/item/req/delete/item
 * gf/item/req/update/item
 * gf/item/req/search/item
 * 
 */
public class ItemController {

    private static final String SAMPLE_NAME = ItemController.class.getSimpleName();
    private static final String TOPIC = "gf/item/req/>";
    //private static final String TOPIC = "gf/item/list/get/req";  // used as the topic "root"

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
        	//System.out.println("The inbound message is : " + inboundMessage.dump()); // Enable this for learning purposes as it logs a String representation of the whole Message

        	/************* THIS IS WHERE DAO IS CALLED AND RESPONSE IS RECIEVED********/

        	final String stringPayload = inboundMessage.getPayloadAsString();
            System.out.println("The converted message payload is : " + stringPayload);
            
            //Call DAO based on topic            
            String requestTopic = inboundMessage.getDestinationName();
            
            String jsonResponse = callDAO (requestTopic, stringPayload);
                     
            
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
     * callDAO is the generic method to call appropriate DAO methods based on topic
     * topic represents and controlls which DAO method needs to be invoked
     */
    
    private static String callDAO(String requestTopic, String jsonInput) {
    	String jsonResponse = null;
    	System.out.println("requestTopic="+requestTopic);

        switch (requestTopic) {
        
        case "gf/item/req/get/list" :
        	jsonResponse = callViewItemList(jsonInput);
        	break;
        	
        case "gf/item/req/add/item" :
        	jsonResponse = callAddItem(jsonInput);
        	break;	
        	
        case "gf/item/req/get/item" :
        	jsonResponse = callViewItem(jsonInput);
        	break;
        case "gf/item/req/create/item" :
        	jsonResponse = callCreateItem(jsonInput);
        	break;
        case "gf/item/req/delete/item" :
        	jsonResponse = callDeleteItem(jsonInput);
        	break;
        case  "gf/item/req/update/item" :
        	break;
        case  "gf/item/req/search/item" :
        	jsonResponse = callSearchItem(jsonInput);
        	break;
        	
        }
        
        return jsonResponse;
		
	}
    
    
    private static String callViewItem(String jsonInput) {
		// TODO Auto-generated method stub
    	String jsonResponse = null;
    	
    	jsonResponse ="{\"method\":\"callViewItem\"}";
		return jsonResponse;
	}



	private static String callSearchItem(String jsonInput) {
		// TODO Auto-generated method stub
		return null;
	}



	private static String callCreateItem(String jsonInput) {
		// TODO Auto-generated method stub
    	String jsonResponse = null;
    	
    	jsonResponse ="{\"method\":\"callCreateItem\"}";
		return jsonResponse;	
	}



	private static String callDeleteItem(String jsonInput) {
		String jsonResponse = null;

    	try {
			
    		//extract Json
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
   
			System.out.println("callDeleteItem itemID="+jsonObject.getString("itemID"));
			
			//set VO;
	        
	        ItemDAO itemDAO = new ItemDAO();
	        ItemVO itemVOInput = new ItemVO();
	        ItemVO itemVOOutput;
	        
	        
	        itemVOInput.setItemID(jsonObject.getString("itemID"));
	        
	        int noOfRecordsDeleted = itemDAO.deleteItem(itemVOInput);
	        System.out.println("callDeleteItem noOfRecordsDeleted="+noOfRecordsDeleted);
	        
	        if (noOfRecordsDeleted==1) {
	        	
	        	//login successful
	        	jsonResponse ="{\"status\":\"success\", \"itemID\":\""+itemVOInput.getItemID()+ "\"}";
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

private static String callAddItem(String jsonInput) {
	
		System.out.println("callAddItem="+jsonInput);
    	
    	String jsonResponse = null;

    	try {
			
    		//extract Json
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
   
			System.out.println("itemName="+jsonObject.getString("itemName"));
			System.out.println("itemCategory="+jsonObject.getString("itemCategory"));
			System.out.println("itemBrand="+jsonObject.getString("itemBrand"));
			System.out.println("itemUnit="+jsonObject.getString("itemPackagingUnit"));
			
			//set VO
	    	//UserDAO userDAO = new UserDAO();
	        //UserVO userVOInput = new UserVO();
	        //UserVO userVOOutput = new UserVO();
	        
	        ItemDAO itemDAO = new ItemDAO();
	        ItemVO itemVOInput = new ItemVO();
	        ItemVO itemVOOutput = new ItemVO();
	        
	        itemVOInput.setItemID(jsonObject.getString("itemID")); 
	        itemVOInput.setItemName(jsonObject.getString("itemName"));
	        itemVOInput.setCategory(jsonObject.getString("itemCategory"));
	        itemVOInput.setBrand(jsonObject.getString("itemBrand"));
	        itemVOInput.setPackagingUnit(jsonObject.getString("itemPackagingUnit"));
	        
	        itemVOOutput = itemDAO.AddItem(itemVOInput);
	        
	        //System.out.println("userVOOutput="+userVOOutput);
	        
	        if (itemVOInput!=null && itemVOOutput.getItemName()!=null) {
	        	
	        	//login successful
	        	jsonResponse ="{\"status\":\"success\", \"itemName\":\""+itemVOOutput.getItemName()+ "\"}";
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

	private static String callViewItemList(String jsonInput) {
    	
    	String jsonResponse = null;

    	try {
			

			
			//set VO
	
	        ItemDAO itemDAO = new ItemDAO();
	       
	        ItemVO itemVOOutput = new ItemVO();
	        
	        ArrayList <ItemVO> arrItems = new ArrayList <ItemVO>();
	        
	        arrItems = itemDAO.getItemsList();
	        
	        //userVOInput.setUsername(jsonObject.getString("username"));
	        //userVOInput.setPassword(jsonObject.getString("password"));
	        
	        //userVOOutput = userDAO.loginUser(userVOInput);
	        //System.out.println("userVOInput="+userVOInput);
	        //System.out.println("userVOOutput="+userVOOutput);
	        
	        //jsonResponse = "{\"items\":[";
	        jsonResponse = "[";
	        	
	        	//jsonResponse ="{\"status\":\"success\", \"firstName\":\""+userVOOutput.getFirstName()+ "\"}";
	        	//within for loop
	        	
	        	
	        	for (int i=0; i<arrItems.size();i++) {
	        		
	        		itemVOOutput = arrItems.get(i);
	        		
	        		jsonResponse += "{\"itemID\": \""+itemVOOutput.getItemID()+ "\", \"itemName\":\""+itemVOOutput.getItemName()+"\", \"itemCategory\":\""+itemVOOutput.getCategory()+"\", \"itemBrand\":\""+itemVOOutput.getBrand()+"\", \"itemPackagingUnit\":\""+itemVOOutput.getPackagingUnit()+"\"},";
	        	}
	        	//outside for loop
	        	
	        	//substring by 1
	        	jsonResponse = jsonResponse.substring(0, jsonResponse.length()-1);
	        	//add ]}
	        	jsonResponse += "]";
	        			
	        

	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
    }
}