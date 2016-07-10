package ws.cogito.magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ws.cogito.magic.messaging.EventsMessageListener;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties(AmqpRouterProperties.class)
public class AmqpDeclarationsConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(AmqpDeclarationsConfiguration.class);
	
	@Autowired
	@Qualifier("amqpRouterProperties")
	AmqpRouterProperties properties;
	
    @Bean
    public ConnectionFactory connectionFactory() {
    	
    	CachingConnectionFactory connectionFactory = null;
    	
    	logger.info("Configuring Connection Factory");
    	
        connectionFactory = new CachingConnectionFactory(properties.getHost());
        connectionFactory.setUsername(properties.getUserName());
        connectionFactory.setPassword(properties.getUserPassword());
        connectionFactory.setVirtualHost(properties.getvHost());
    	
    	return connectionFactory;
    }
    
	/**
	 * In order for all the other configurations to take effect there must
	 * be at least one decleration by the rabbitAdmin
	 * @param connectionFactory
	 * @return RabbitAdmin
	 */
    @Bean
    public RabbitAdmin admin(ConnectionFactory connectionFactory) {
    	
    	logger.info("Kicking off Declarations");
    	
    	RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
    	
    	/* 
    	 * *********************IMPORTANT********************************
    	 * 
    	 * None of the other declarations take effect unless at least one 
    	 * declaration is executed by RabbitAdmin.
    	 * 
    	 * *********************IMPORTANT******************************** 
    	 */
    	rabbitAdmin.declareExchange(topicExchange());
    	
        return new RabbitAdmin(connectionFactory);
    }
    
    @Bean
    RabbitTemplate rabbitTemplate() {
    	
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
    	
    	return rabbitTemplate;
    }    

    /* ALERT CONFIGURATION */
	@Bean
	Queue alertsQueue() {
		return new Queue(properties.getEventAlertsQueue(), true);
	}
	
	@Bean
	Binding alertsBinding(Queue alertsQueue, TopicExchange topicExchange) {
		return BindingBuilder.bind(alertsQueue).to(topicExchange).
				with(properties.getEventAlertsQueueBinding());
	}    
	
    /* EVALUTATIONS CONFIGURATION */
	@Bean
	Queue evaluationsQueue() {
		return new Queue(properties.getEvaluationsQueue(), true);
	}

	@Bean
	TopicExchange topicExchange() {
		return new TopicExchange(properties.getTopicExchange());
	}

	@Bean
	Binding evaluationsBinding(Queue evaluationsQueue, TopicExchange topicExchange) {
		
		//binding to magic.events.#		
		return BindingBuilder.bind(evaluationsQueue).to(topicExchange).
				with(properties.getEvaluationsQueueBinding());
	}
 
    @Bean
    MessageListenerAdapter eventsMessageListener() throws Exception {
    	
    	EventsMessageListener eventsMessageListener = new EventsMessageListener();
    	
    	eventsMessageListener.setRabbitTemplate(rabbitTemplate());
    	eventsMessageListener.setTopicExchange(properties.getTopicExchange());
    	eventsMessageListener.setEventAlertsRoutingKey(properties.getEventAlertsRoutingKey());    	
    	
        return new MessageListenerAdapter(eventsMessageListener) {{
            setDefaultListenerMethod("onMessage");
        }};
    }
    
	@Bean
	SimpleMessageListenerContainer eventsContainer() {
		
		SimpleMessageListenerContainer eventsContainer = 
				new SimpleMessageListenerContainer();
		
		eventsContainer.setConnectionFactory(connectionFactory());
		eventsContainer.setQueueNames(properties.getEvaluationsQueue());
		
		return eventsContainer;
	}   
    
    @Bean
	SimpleMessageListenerContainer eventsContainer(ConnectionFactory connectionFactory, 
			@Qualifier("eventsMessageListener") MessageListenerAdapter eventsMessageListener) {
		
		SimpleMessageListenerContainer eventsContainer = 
				new SimpleMessageListenerContainer();
		
		eventsContainer.setConnectionFactory(connectionFactory);
		eventsContainer.setQueueNames(properties.getEvaluationsQueue());
		eventsContainer.setMessageListener(eventsMessageListener);
		
		return eventsContainer;
	} 	
}