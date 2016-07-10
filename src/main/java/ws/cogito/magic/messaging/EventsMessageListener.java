package ws.cogito.magic.messaging;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;

import com.jayway.jsonpath.JsonPath;

import ws.cogito.magic.MessageLogging;

public class EventsMessageListener implements MessageListener, MessageLogging {
	
	private static final Logger logger = LoggerFactory.getLogger(EventsMessageListener.class);
	
	static final List<String> cities = Arrays.asList ("Las Vegas","New York", "Los Angeles");
	
	private RabbitTemplate rabbitTemplate;
	private String eventAlertsRoutingKey;
	private String topicExchange;

	@Override
	public void onMessage(Message message) {
		
		String tid = logMessage(message, logger);
		
		//an alert is required...
		if (evaluateMessage(message)) {
			
			raiseAlert(message, tid);
		}
	}
	
	private void raiseAlert (Message message, String tid) {
		
		//Response Properties
		MessageProperties properties = new MessageProperties();
		properties.setContentType(MediaType.APPLICATION_JSON.toString());
		properties.setContentEncoding(StandardCharsets.UTF_8.name());
		
		//forward on tracking id
		properties.getHeaders().put(trackingId, tid);
		
		//add another custom header
		properties.getHeaders().put("eventAlert", true);
		
		Message responseMessage = new Message(message.getBody(), properties);
		
		rabbitTemplate.send(topicExchange, eventAlertsRoutingKey, responseMessage);
		
		logger.info("Published Event Alert Message");
	}
	
	private boolean evaluateMessage (Message message) {
		
		boolean alert = false;
		
		try {
			
			String json = new String(message.getBody(), StandardCharsets.UTF_8.name());
			
			String location = JsonPath.read(json,"$.location");
			
			if (cities.contains(location)) {   				
				alert = true;
			} else {
				logger.debug("Message does not require an alert...off into the ether");
			}
		
		} catch (UnsupportedEncodingException e) {
			
			logger.error("Unable to parse message body /n" + e.getMessage());
		}
		
		return alert;
	}
	
	public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	public void setEventAlertsRoutingKey(String eventAlertsRoutingKey) {
		this.eventAlertsRoutingKey = eventAlertsRoutingKey;
	}
	
	public void setTopicExchange(String topicExchange) {
		this.topicExchange = topicExchange;
	}

}