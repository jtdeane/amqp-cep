package ws.cogito.magic;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.amqp.core.Message;

public interface MessageLogging {
	
	static final String trackingId = "trackingId";
	
	default public String logMessage (Message message, Logger logger) {
		
		String tid = null;
		
		//log tracking id if it exists
		if (message.getMessageProperties().getHeaders().containsKey(trackingId)) {
			
			tid = (String)message.getMessageProperties().getHeaders().get(trackingId);
			
			logger.info("Existing Tracking ID: " + trackingId + "=" + tid + " ");
		
		} else {
			
			//create new tracking id
			tid = UUID.randomUUID().toString();
			
			logger.info("Generated Tracking ID: " + trackingId + "=" + tid + " ");
		}
		
		//log the correlation ID if it exists
		if (message.getMessageProperties().getCorrelationId() != null) {
			
			try {
			
				String correlationId = new String(message.getMessageProperties().
						getCorrelationId(), "UTF-8");
				
				logger.info("Message Correlation ID: " + correlationId);
			
			} catch (UnsupportedEncodingException e) {
				logger.error("Unable to process correlation identifer \n" + e.getMessage());
			}
		}
		
		//log the message body
		String body = null;
		
		try {
		
			body = new String(message.getBody(), StandardCharsets.UTF_8.name());
		
		} catch (UnsupportedEncodingException e) {
			
			logger.error("Unable to parse message body /n" + e.getMessage());
		}
		
		logger.info("Received Message: \n" + body);
		
		return tid;
	}
}