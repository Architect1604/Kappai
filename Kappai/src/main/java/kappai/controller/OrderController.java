package kappai.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.publisher.OutboundMessage;
import com.solace.messaging.publisher.OutboundMessageBuilder;
import com.solace.messaging.receiver.RequestReplyMessageReceiver;
import com.solace.messaging.resources.TopicSubscription;

import kappai.dao.OrderDAO;
import kappai.model.OrderItemVO;
import kappai.model.OrderVO;

/**
 * This class demonstrates the usage of the Solace Java API to create a Replier class.
 * This implementation focuses on the blocking behaviour of the API.
 * The mechanism of the Request-Reply pattern is defined in more detail over here : <a href="https://tutorials.solace.dev/jcsmp/request-reply/">Solace Request/Reply pattern</a>
 * <p>
 * Refer to the DirectRequestorBlocking class for the request component of the flow.
 * 
 * 
 * Supported topics in variable requestTopic: 
 * gf/order/req/create/order
 * 	Input : no input needed, default oder created with status PENDING and order date as current date
 *	Output jsonResponse = {"orderid" : "1234", "orderstatus" : "PENDING", "orderdate": "dd/MM/yyyy" }
 *
 * gf/order/req/delete/order (//to be implemented later)
 * 
 * gf/order/req/update/order
 * 	Input jsonInput = {"orderid" : "1234", "orderstatus" : "APPROVED | ORDERED | DELIVERED", "deliverydate" : "dd/mm/yyyy"} (delivery date optional)
 * 	Output jsonResponse = {"orderid" : "1234", "orderstatus" : "success", "status" : "APPROVED | ORDERED | DELIVERED" }
 * 
 * gf/order/req/get/order/pending
 * 	Input: No input needed
 *	Output jsonResponse = {"orderid" : "1234", "orderstatus" : "PENDING" , "orderdate": "dd/MM/yyyy" }
 *
 * gf/order/req/get/order/ordered
 * 	 Input: No input needed
 *	 Output jsonResponse = {"orderid" : "1234", "orderstatus" : "ORDERED" , "orderdate": "dd/MM/yyyy" }
 *
 * gf/order/req/get/items
 * 	Input jsonInput = {"orderid" : "1234"}
 *	Output jsonResponse = {"orderid" : "1234", "items" : [
 * 		{  "itemid" : "123", "name" : "Milk", "category": "Dairy", "brand" :"Farmhouse", "packagingunit" : "2L", "quantity" : "2"},
 * 		{  "itemid" : "123", "name" : "Milk", "category": "Dairy", "brand" :"Farmhouse", "packagingunit" : "2L", "quantity" : "2"}
 * 	]
 *
 *  gf/order/req/add/item
 *        {
 *        	"orderid" : "8",
            "itemid": "9",
            "quantity": "1",
            "status": "ORDERED"
        }
 *
 * gf/order/req/add/items
{
    "orderid": "8",
    "items": [
        {
            "itemid": "9",
            "quantity": "1",
            "status": "ORDERED"
        },
        {
            "itemid": "11",
            "quantity": "2",
            "status": "ORDERED"
        },
        {
            "itemid": "12",
            "quantity": "1",
            "status": "ORDERED"
        },
        {
            "itemid": "14",
            "quantity": "3",
            "status": "ORDERED"
        }
    ]
}
 * Output jsonInput = {"orderid" : "1234", "numberOfItemsAdded" : "3"}
 * 
 * gf/order/req/delete/item
 * 	Input jsonInput = {"orderitemid" : "123"}
 *	Output jsonInput = {"orderitemid" : "1234", "status" : "deleted"}
 *
 * gf/order/req/update/item  //to be called when item is delivered
 * gf/order/req/search/item  //to be called when item is delivered
 * 
 */
public class OrderController {

    private static final String SAMPLE_NAME = OrderController.class.getSimpleName();
    private static final String TOPIC = "gf/order/req/>";

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
     * topic represents and controls which DAO method needs to be invoked
     */
    
    
    /*
   
    	*/
    
 // The requestTopic represents an API command event using a URI that follows a 
	//convention of nouns and verbs (e.g., "order/create").
    // These URIs act as Publish-Subscribe (PubSub) topics in the Solace messaging system.
    // The controller tier subscribes to these topics using the Solace Java Messaging API.
    // Each event carries a JSON nested structure as its payload.
	
    
    
    //callDAO is the generic method to call appropriate DAO methods based on topic
    //topic represents and controls which DAO method needs to be invoked
    private static String callDAO(String requestTopic, String jsonInput) {
    	// Declare a variable to hold the JSON response
    	String jsonResponse = null;
    	// Print the request topic for debugging purposes
    	System.out.println("requestTopic="+requestTopic);

    	//RequestTopic is an API command using a noun-verb URI (e.g., "order/create"), handled via Solace PubSub
    	
    	// Use a switch statement to determine the operation based on the requestTopic
        switch (requestTopic) {
       
        // Each case corresponds to a specific command event represented by a URI
        // The methods called handle the business logic for each event
       
        case "gf/order/req/create/order" :
        	jsonResponse = callOrderCreate();
        	break;
        case "gf/order/req/delete/order" :
        	jsonResponse = callOrderDelete(jsonInput);
        	break;
        case "gf/order/req/update/order" : 
        	jsonResponse = callOrderUpdate(jsonInput);
        	break;
        case "gf/order/req/get/order/pending" :
        	jsonResponse = callGetOrderByStatusPending("{\"orderstatus\" : \"PENDING\"}");
        	break;
        case "gf/order/req/get/orders" :
        	jsonResponse = callGetOrderByStatus(jsonInput);
        	break;
        case "gf/order/req/get/order/approved" :
        	jsonResponse = callGetOrderByStatus("{\"orderstatus\" : \"APPROVED\"}");
        	break;
        case "gf/order/req/get/order/delivered" :
        	jsonResponse = callGetOrderByStatus("{\"orderstatus\" : \"DELIVERED\"}");
        	break;
        case  "gf/order/req/get/items" :
        	jsonResponse = callGetOrderItems(jsonInput);
        	break;
        case  "gf/order/req/add/items" :
        	jsonResponse = callAddOrderItems(jsonInput);
        	break;
        case  "gf/order/req/add/item" :
        	jsonResponse = callAddOrderItem(jsonInput);
        	break;
        case  "gf/order/req/delete/item" :
        	jsonResponse = callDeleteOrderItem(jsonInput);
        	break;
        case  "gf/order/req/update/item" :
        	jsonResponse = callUpdateOrderItem(jsonInput); //to be called when item is delivered
        	break;
        case  "gf/order/req/search/item" :
        	jsonResponse = callSearchOrderItem(jsonInput); //to be implemented later
        	break;
        case  "gf/order/req/update/orderitem/status" :
        	jsonResponse = callUpdateOrderItemStatus(jsonInput);
        	break;
        case "gf/order/req/update/order/status/delivered" :
        	jsonResponse = callOrderUpdateToDelivered(jsonInput);
        	break;
        }
        
        // Return the JSON response generated by the appropriate method call
        return jsonResponse;
	}
    
	/* Method to get the PENDING order. There should be only one PENDING order at any time. 
	 * Input: {"orderstatus" : "PENDING | APPROVED | DELIVERED"}
	 * Output jsonResponse = {"orderid" : "1234", "orderstatus" : "PENDING | APPROVED | DELIVERED" , "orderdate": "dd/MM/yyyy" }
	 * Delivery date could be absent as well
	 */
    private static String callGetOrderByStatusPending(String jsonInput) {

		System.out.println("callGetPendingOrder jsonInput=" +jsonInput);

    	String jsonResponse = null;
    	try {
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			
			
			OrderDAO orderDAO = new OrderDAO();
			OrderVO orderOutputVO = null;
			
			orderOutputVO = orderDAO.getOrderByStatusPending(jsonObject.getString("orderstatus"));
			
			//convert to response json
			
			if (orderOutputVO == null) {
				jsonResponse ="{\"orderid\":\"-1\", \"orderStatus\":\"Not Found\"}";	
			}
			else {
				jsonResponse ="{\"orderid\":\"" +orderOutputVO.getOrderID()+ "\", \"orderStatus\":\""+orderOutputVO.getStatus()+  "\", \"orderdate\":\""+orderOutputVO.getOrderDate()+ "\"}";	
			}

	        
	       
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	

	}
    
    
    
    
    
    /* Method to return orders - orderid, orderstatus, orderdate, deliverydate
     * Input: OrderStatus (won't expect pending, so ORDERED | DELIVERED)
     * Output jsonResponse = {"orderid" : "1234", "orderstatus" : "ORDERED | DELIVERED" , "orderdate": "dd/MM/yyyy" }
	 * Delivery date could be absent as well
     */

    
    private static String callGetOrderByStatus(String jsonInput) {

		System.out.println("callGetPendingOrder jsonInput=" +jsonInput);
		ArrayList<OrderVO> arrOrders;
    	String jsonResponse = null;
    	try {
    		//extract Json and map to Order VO
	        
			/*InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			*/
			
			OrderDAO orderDAO = new OrderDAO();
			OrderVO orderOutputVO = null;
			arrOrders = orderDAO.getOrderByStatus(jsonInput);
			
			
			
			//convert to response json
			
			/*ArrayList to store orders, receiving orderVO
			 * 
			 * ArrayList <OrderVO> orders = new ArrayList <OrderVO>();
			 * for (int i=0
			 */
			
			
			
        	
        	//jsonResponse ="{\"status\":\"success\", \"firstName\":\""+userVOOutput.getFirstName()+ "\"}";
        	//within for loop
        	
			if (arrOrders == null || arrOrders.size()<1) {
				jsonResponse ="{\"orderid\":\"-1\", \"orderStatus\":\"Not Found\"}";	
			}
			else {		
				jsonResponse = "[";
	        	for (int i=0; i<arrOrders.size();i++) {
	        		
	        		orderOutputVO = arrOrders.get(i);
	        		
	        		jsonResponse += "{\"orderID\": \""+orderOutputVO.getOrderID()+ "\", \"orderStatus\":\""+orderOutputVO.getStatus()+"\", \"orderDate\":\""+orderOutputVO.getOrderDate()+"\", \"deliveryDate\":\""+orderOutputVO.getDeliveryDate()+"\"},";
	        	}
	        	//outside for loop
	        	
	        	//substring by 1
	        	jsonResponse = jsonResponse.substring(0, jsonResponse.length()-1);
	        	//add ]}
	        	jsonResponse += "]";
			}
			


	        
	       
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	

	}
    
    
    
    

	/* Method to create new order
	 * Input : no input needed, default oder created with status PENDING and order date as current date
	 * Output jsonResponse = {"orderid" : "1234", "orderstatus" : "APPROVED | ORDERED | DELIVERED", "orderdate": "dd/MM/yyyy" }
	 * Delivery date could be absent as well
	 */
	private static String callOrderCreate() {
		System.out.println("callOrderCreate");

    	String jsonResponse = null;
    	try {
			
    					
			OrderDAO orderDAO = new OrderDAO();
			OrderVO orderOutputVO = orderDAO.createOrder();

	        jsonResponse ="{\"orderid\":\"" +orderOutputVO.getOrderID()+ "\", \"orderstatus\":\""+orderOutputVO.getStatus()+  "\", \"orderdate\":\""+orderOutputVO.getOrderDate()+ "\"}";
	       
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
	}



	private static String callOrderDelete(String jsonInput) {
		// TODO Auto-generated method stub
		return null;
	}


	/* Method to update order status and delivery date
	 * Input jsonInput = {"orderid" : "1234", "orderstatus" : "APPROVED | ORDERED | DELIVERED", "deliverydate" : "dd/mm/yyyy"}
	 * Output jsonResponse = {"orderid" : "1234", "orderstatus" : "success", "status" : "APPROVED | ORDERED | DELIVERED" }
	 * Delivery date could be absent as well
	 */
	private static String callOrderUpdate(String jsonInput) {

		System.out.println("callOrderUpdate Order="+jsonInput);

    	String jsonResponse = null;
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			
			OrderVO orderVOInput = new OrderVO();
			
			System.out.println("orderid="+jsonObject.getString("orderid"));
			System.out.println("orderstatus="+jsonObject.getString("orderstatus"));
			System.out.println("deliverydate="+jsonObject.containsKey("deliverydate"));
			
			orderVOInput.setOrderID(Integer.parseInt(jsonObject.getString("orderid")));
			orderVOInput.setStatus(jsonObject.getString("orderstatus"));
			
			if (jsonObject.containsKey("deliverydate") && !jsonObject.isNull("deliverydate") && jsonObject.getString("deliverydate")!=null && !jsonObject.getString("deliverydate").equals("")) {
				//convert from String to sql date
				java.sql.Date deliveryDate = convertToSqlDate(jsonObject.getString("deliverydate"));
				orderVOInput.setDeliveryDate(deliveryDate);
			}
			
			int noOfRecordsUpdated = orderDAO.updateOrderStatus(orderVOInput);
	        System.out.println("callOrderUpdate noOfRecordsUpdated="+noOfRecordsUpdated);
	        
	        // status of the order update method
	        String status = "error";
	        if(noOfRecordsUpdated == 1) status="success";
	        
	        jsonResponse ="{\"orderid\":\"" +orderVOInput.getOrderID()+ "\", \"status\":\""+status+  "\", \"orderstatus\":\""+orderVOInput.getStatus()+ "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
	}
	
	private static String callOrderUpdateToDelivered(String jsonInput) {

		System.out.println("callOrderUpdate Order="+jsonInput);

    	String jsonResponse = null;
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			
			OrderVO orderVOInput = new OrderVO();
			
			System.out.println("orderid="+jsonObject.getString("orderid"));
			System.out.println("orderstatus="+jsonObject.getString("orderstatus"));
			System.out.println("deliverydate="+jsonObject.containsKey("deliverydate"));
			
			orderVOInput.setOrderID(Integer.parseInt(jsonObject.getString("orderid")));
			orderVOInput.setStatus(jsonObject.getString("orderstatus"));
			
			System.out.println("OrderController: Delivery date is " + jsonObject);
			//if (jsonObject.containsKey("deliverydate") && !jsonObject.isNull("deliverydate") && jsonObject.getString("deliverydate")!=null) {
			if (jsonObject.containsKey("deliverydate")) {
				//convert from String to sql date
				//java.sql.Date deliveryDate = convertToSqlDate(jsonObject.getString("deliverydate"));
				java.sql.Date deliveryDate = new java.sql.Date(System.currentTimeMillis());
				System.out.println("Delivery date is " + deliveryDate);
				orderVOInput.setDeliveryDate(deliveryDate);
				
			}
			
			int noOfRecordsUpdated = orderDAO.updateOrderStatus(orderVOInput);
	        System.out.println("callOrderUpdate noOfRecordsUpdated="+noOfRecordsUpdated);
	        
	        // status of the order update method
	        String status = "error";
	        if(noOfRecordsUpdated == 1) status="success";
	        
	        jsonResponse ="{\"orderid\":\"" +orderVOInput.getOrderID()+ "\", \"status\":\""+status+  "\", \"orderstatus\":\""+orderVOInput.getStatus()+ "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
	}


	/* Method to get order list
	 * Input jsonInput = {"orderid" : "1234", "orderstatus" : "APPROVED | ORDERED | DELIVERED", "deliverydate" : "dd/mm/yyyy"}
	 * Output jsonResponse = {"orderid" : "1234", "orderstatus" : "success", "status" : "APPROVED | ORDERED | DELIVERED" }
	 * Delivery date could be absent as well
	 */
	

	/* Method to get order items list
	 * Input jsonInput = {"orderid" : "1234"}
	 * Output jsonResponse = {"orderid" : "1234", "items" : [
	 * {  "itemid" : "123", "name" : "Milk", "category": "Dairy", "brand" :"Farmhouse", "packagingunit" : "2L", "quantity" : "2"}
	 * {  "itemid" : "123", "name" : "Milk", "category": "Dairy", "brand" :"Farmhouse", "packagingunit" : "2L", "quantity" : "2"}
	 */
	
	
	// This method is responsible for retrieving the order items for a given order ID received from the Flutterflow frontend. 
	//It calls the DAO to interact with the database and returns the result as a JSON array, which is passed back to the frontend.
	private static String callGetOrderItems(String jsonInput) {
	    // Initialize a variable to store the final JSON response
		String jsonResponse = null;

    	try {
    		// Step 1: Extract the input JSON (containing orderID) received from the frontend

            // Convert the input string into a stream of bytes for JSON parsing
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
	        // The JsonReader allows us to read the incoming JSON payload from the frontend
			JsonReader jsonReader = Json.createReader(jis);
	        // Parse the JSON object from the input stream
			JsonObject jsonObject = jsonReader.readObject();
	        // Close resources (good practice to avoid memory leaks)
			jsonReader.close();
			jis.close();
   
	        // Log message for debugging to confirm we are processing the order list
			System.out.println("callOrderList");
			
			// Step 2: Set up the DAO (Data Access Object) and pass the extracted Order ID
	        
	        // Create an instance of the OrderDAO to interact with the database.
			OrderDAO orderDAO = new OrderDAO();
	        // Create an empty OrderVO (Value Object) to hold the order data for interaction with the DAO
			OrderVO orderVOInput = new OrderVO();
	        // Retrieve the orderID from the input JSON and set it in the OrderVO
			orderVOInput.setOrderID(Integer.parseInt(jsonObject.getString("orderid")));
	        // Call the DAO to fetch the order and its items from the database
			OrderVO orderVOOutput = orderDAO.getOrderAndItems(orderVOInput);

			 // Step 3: Convert the OrderVO's items into a JSON response

	        // Initialize the response as an empty JSON array
			jsonResponse = "[";
	        // Iterate over the list of order items retrieved from the database
			for (int i=0; i< orderVOOutput.getOrderItems().size(); i++) {
	            // Get each OrderItemVO (representing a specific item in the order)
				OrderItemVO orderItemVO = orderVOOutput.getOrderItems().get(i);
	            // Append each item as a JSON object with various attributes (orderitemid, itemid, etc)
				jsonResponse += "{\"orderitemid\": \"" + orderItemVO.getOrderItemID() + "\",";
				jsonResponse += "\"itemid\": \"" + orderItemVO.getItemID() + "\",";
				jsonResponse += "\"name\": \"" + orderItemVO.getItemName() + "\",";
				jsonResponse += "\"category\": \"" + orderItemVO.getCategory()+ "\",";
				jsonResponse += "\"brand\": \"" + orderItemVO.getBrand() + "\",";
				jsonResponse += "\"packagingunit\": \"" + orderItemVO.getPackagingUnit()+ "\",";
				jsonResponse += "\"quantity\": \"" + orderItemVO.getQuantity() + "\"},";
			
			}
	        // Remove the trailing comma from the last item and close the JSON array
			jsonResponse = jsonResponse.substring(0, jsonResponse.length()-1);
			jsonResponse += "]";
	        

		} catch (IOException e) {
	        // Log any errors encountered during the execution of the method
			e.printStackTrace();
		}
        // Return the fully constructed JSON response back to the frontend
    	return jsonResponse;
  	
    }
	
	

	/* Method to add  multiple items to an order 
	INPUT JSON=
	{
    "orderid": "8",
    "items": [
        {
            "itemid": "9",
            "quantity": "1",
            "status": "ORDERED"
        },
        {
            "itemid": "11",
            "quantity": "2",
            "status": "ORDERED"
        },
        {
            "itemid": "12",
            "quantity": "1",
            "status": "ORDERED"
        },
        {
            "itemid": "14",
            "quantity": "3",
            "status": "ORDERED"
        }
    ]
}
 	 * Output jsonInput = {"orderid" : "1234", "numberOfItemsAdded" : "3"}
	 */
	private static String callAddOrderItems(String jsonInput) {


		System.out.println("callAddOrderItem Order="+jsonInput);

    	String jsonResponse = null;
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			
			OrderVO orderVOInput = new OrderVO();
			
			orderVOInput.setOrderID(Integer.parseInt(jsonObject.getString("orderid")));
			
			JsonArray arrItemsJson = jsonObject.getJsonArray("items");
			
			System.out.println("arrItemsJson="+ arrItemsJson.size());
			
			for (int i=0; i< arrItemsJson.size(); i++) {
				
				OrderItemVO orderItemVO = new OrderItemVO();
				orderItemVO.setItemID(arrItemsJson.getJsonObject(i).getString("itemid"));
				//orderItemVO.setItemName(arrItemsJson.getJsonObject(i).getString("name"));
				//orderItemVO.setCategory(arrItemsJson.getJsonObject(i).getString("category"));
				//orderItemVO.setBrand(arrItemsJson.getJsonObject(i).getString("brand"));
				//orderItemVO.setPackagingUnit(arrItemsJson.getJsonObject(i).getString("packagingunit"));
				orderItemVO.setQuantity(Integer.parseInt(arrItemsJson.getJsonObject(i).getString("quantity")));
				
				orderVOInput.addOrderItem(orderItemVO);
				
					
			}
			
			int numberOfItemsAdded = orderDAO.addItemToOrder(orderVOInput);
	        
	        jsonResponse ="{\"orderid\":\"" +orderVOInput.getOrderID()+ "\", \"numberOfItemsAdded\":\""+numberOfItemsAdded+ "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;	
	}
	/* Adds a single item to order 
	 * Input json
	 * 	{
        	"orderid" : "8",
            "itemid": "9",
            "quantity": "1",
            "status": "ORDERED"
        }
	 *  
	 *  Output jsonInput = {"orderid" : "1234", "numberOfItemsAdded" : "1"}
	 */

	

	private static String callAddOrderItem(String jsonInput) {


		System.out.println("callAddOrderItem Order="+jsonInput);

    	String jsonResponse = null;
    	
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			
			OrderVO orderVOInput = new OrderVO();
			
			orderVOInput.setOrderID(Integer.parseInt(jsonObject.getString("orderid")));
			
				
			OrderItemVO orderItemVO = new OrderItemVO();
			orderItemVO.setItemID(jsonObject.getString("itemid"));
			orderItemVO.setQuantity(Integer.parseInt(jsonObject.getString("quantity")));
				
			orderVOInput.addOrderItem(orderItemVO);
				
			
			int numberOfItemsAdded = orderDAO.addItemToOrder(orderVOInput);
	        
	        jsonResponse ="{\"orderid\":\"" +orderVOInput.getOrderID()+ "\", \"numberOfItemsAdded\":\""+numberOfItemsAdded+ "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;	
	}

	/* Method to delete an item from an order
	 * Input jsonInput = {"orderitemid" : "123"}
 	 * 
 	 * Output jsonInput = {"orderitemid" : "1234", "status" : "deleted"}
	 */
	
	// This method is part of the OrderController class and is responsible for deleting an item 
	// from an order. It relies on OrderDAO to interact with the database and perform the deletion operation.
	private static String callDeleteOrderItem(String jsonInput) {

	    // Log the incoming JSON for debugging purposes
		System.out.println("callDeleteOrderItem Order="+jsonInput);

	    // Initialize the variable to store the JSON response
    	String jsonResponse = null;
    	try {
    		// Step 1: JSON Deserialization - Convert the incoming JSON string to a JsonObject
            // This JSON contains the order item ID that needs to be deleted
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			// Step 2: Create an instance of OrderDAO to interact with the database
	        // This demonstrates dependency: The Controller depends on the DAO to perform
	        // the business logic (i.e., deleting the order item)
			OrderDAO orderDAO = new OrderDAO();
			
			// Step 3: Use the DAO method to delete the order item based on the orderitemid 
			// extracted from the JSON object. This interaction is essential to the 
			// controller's functionality, as it offloads the database logic to the DAO.
			boolean deletestatus = orderDAO.deleteOrderItem(Integer.parseInt(jsonObject.getString("orderitemid")));
	        System.out.println("callDeleteOrderItem deletestatus="+deletestatus);
	        
	        // Step 4: Build the JSON response based on the result of the DAO operation
	        // If the deletion was successful, the status will be "success", otherwise "error"
	        String status = "error";
	        if(deletestatus) status="success";
	        // Step 5: JSON Serialization - Convert the result into a JSON response
	        jsonResponse ="{\"orderitemid\":\"" +jsonObject.getString("orderitemid")+ "\", \"status\":\""+status + "\"}";
	       
	        
		} catch (IOException e) {
			// Handle any IO exceptions that may occur during JSON processing
			e.printStackTrace();
		}
    	
        // Return the JSON response to Flutterflow
    	return jsonResponse;
    	
	}
	
	
	private static String callUpdateOrderItemStatus(String jsonInput) {

		System.out.println("callupdatePastOrderStatus ="+jsonInput);

    	String jsonResponse = null;
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			
			int numOfRecords = orderDAO.updateOrderItemStatus(Integer.parseInt(jsonObject.getString("orderitemid")));
			System.out.println("callUpdateOrderItemStatus records updated ="+numOfRecords);
			
	        // status of the order update method
	        String status = "error";
	        if(numOfRecords>0) status="success";
	        
	        jsonResponse ="{\"orderitemid\":\"" +jsonObject.getString("orderitemid")+ "\", \"status\":\""+status + "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
    	
	}
	
	
	/* Method to update the status of an item to DELIVERED
	 * Input jsonInput = {"orderitemid" : "123"}
 	 * 
 	 * Output jsonInput = {"orderitemid" : "1234", "status" : "delivered"}	 * 
	 */
	
	private static String callUpdateOrderItem(String jsonInput) {
		
		System.out.println("callUpdateOrderItem Order="+jsonInput);

    	String jsonResponse = null;
    	try {
			
    		//extract Json and map to Order VO
	        
			InputStream jis = new ByteArrayInputStream(jsonInput.getBytes());
			JsonReader jsonReader = Json.createReader(jis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			jis.close();
			
			OrderDAO orderDAO = new OrderDAO();
			OrderVO orderVOInput = new OrderVO();
							
			OrderItemVO orderItemVO = new OrderItemVO();
			orderItemVO.setOrderItemID(jsonObject.getString("orderitemid"));
			orderItemVO.setItemStatus("DELIVERED");
				
			orderVOInput.addOrderItem(orderItemVO);
				
			
			int numberOfItemsAdded = orderDAO.addItemToOrder(orderVOInput);
			
			
	        System.out.println("callUpdateOrderItem numberOfItemsAdded="+numberOfItemsAdded);
	        
	        // status of the order update method
	        String status = "error";
	        if(numberOfItemsAdded == 1) status="DELIVERED";
	        
	        jsonResponse ="{\"orderitemid\":\"" +jsonObject.getString("orderitemid")+ "\", \"itemstatus\":\""+status + "\"}";
	       
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	return jsonResponse;
	
	}
	
	private static String callSearchOrderItem(String jsonInput) {
		// TODO Auto-generated method stub
    	String jsonResponse = null;
    	
    	jsonResponse ="{\"method\":\"callCreateItem\"}";
		return jsonResponse;	
	}	
	
	/* Converts a String data in the format dd/MM/yyyy to java.sql.Date
	 * 
	 */
	
	private static java.sql.Date convertToSqlDate(String dateString) {
		
		java.sql.Date sqlDate = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
			java.util.Date utilDate = sdf.parse(dateString);

			// Convert java.util.Date to java.sql.Date
			sqlDate = new java.sql.Date(utilDate.getTime());
            // Print the result
            System.out.println("SQL Date: " + sqlDate);

        } catch (ParseException e) {
            // Handle the exception if the date string is not in the correct format
            e.printStackTrace();
        }
		
		return sqlDate;
	}
}
