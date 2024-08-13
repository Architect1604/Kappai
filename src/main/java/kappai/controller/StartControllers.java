package kappai.controller;

import java.util.Properties;

import com.solace.messaging.MessagingService;
import com.solace.messaging.config.SolaceProperties.AuthenticationProperties;
import com.solace.messaging.config.SolaceProperties.ServiceProperties;
import com.solace.messaging.config.SolaceProperties.TransportLayerProperties;
import com.solace.messaging.config.profile.ConfigurationProfile;
import com.solace.messaging.resources.Topic;
import com.solace.messaging.resources.TopicSubscription;

public class StartControllers {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LoginController lc = new LoginController();
		LoginControllerReq lcr = new LoginControllerReq();
		
		  //final MessagingService serviceForRequestingParty,
	      //final MessagingService serviceForRespondingParty,
	      //final Topic destinationForRequester,
	      //final TopicSubscription sourceForResponder) 
		
		
        final Properties properties = new Properties();
        properties.setProperty(TransportLayerProperties.HOST, args[0]);          // host:port
        properties.setProperty(ServiceProperties.VPN_NAME,  args[1]);     // message-vpn
        properties.setProperty(AuthenticationProperties.SCHEME_BASIC_USER_NAME, args[2]);      // client-username
        if (args.length > 3) {
            properties.setProperty(AuthenticationProperties.SCHEME_BASIC_PASSWORD, args[3]);  // client-password
        }
        //properties.setProperty(JCSMPProperties.GENERATE_SEQUENCE_NUMBERS, true);  // not required, but interesting
        properties.setProperty(ServiceProperties.RECEIVER_DIRECT_SUBSCRIPTION_REAPPLY, "true");  // subscribe Direct subs after reconnect
        properties.setProperty(TransportLayerProperties.RECONNECTION_ATTEMPTS, "20");  // recommended settings
        properties.setProperty(TransportLayerProperties.CONNECTION_RETRIES_PER_HOST, "5");
        // https://docs.solace.com/Solace-PubSub-Messaging-APIs/API-Developer-Guide/Configuring-Connection-T.htm
        
        
        //Responder start


        final MessagingService serviceForRespondingParty = MessagingService.builder(ConfigurationProfile.V1)
                .fromProperties(properties)
                .build();
        serviceForRespondingParty.connect();  // blocking connect
        serviceForRespondingParty.addServiceInterruptionListener(serviceEvent -> {
            System.out.println("### SERVICE INTERRUPTION: "+serviceEvent.getCause());
            //isShutdown = true;
        });
        serviceForRespondingParty.addReconnectionAttemptListener(serviceEvent -> {
            System.out.println("### RECONNECTING ATTEMPT: "+serviceEvent);
        });
        serviceForRespondingParty.addReconnectionListener(serviceEvent -> {
            System.out.println("### RECONNECTED: "+serviceEvent);
        });		
        
        TopicSubscription sourceForResponder = TopicSubscription.of("abc");      

        LoginController.blockingResponder(serviceForRespondingParty, sourceForResponder);
		
		//Requester start
		
/*
		  public static void blockingRequestor(
		      final MessagingService serviceForRequestingParty,
		      final Topic destinationForRequester) { */
	       

        final MessagingService serviceForRequestingParty = MessagingService.builder(ConfigurationProfile.V1)
                .fromProperties(properties)
                .build();
        serviceForRequestingParty.connect();  // blocking connect
        serviceForRequestingParty.addServiceInterruptionListener(serviceEvent -> {
            System.out.println("### SERVICE INTERRUPTION: "+serviceEvent.getCause());
            //isShutdown = true;
        });
        serviceForRequestingParty.addReconnectionAttemptListener(serviceEvent -> {
            System.out.println("### RECONNECTING ATTEMPT: "+serviceEvent);
        });
        serviceForRequestingParty.addReconnectionListener(serviceEvent -> {
            System.out.println("### RECONNECTED: "+serviceEvent);
        });
        
        Topic destinationForRequester = Topic.of("abc");

		
        LoginControllerReq.blockingRequestor(serviceForRequestingParty, destinationForRequester);

	}

}
