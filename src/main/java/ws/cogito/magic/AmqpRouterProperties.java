package ws.cogito.magic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="amqp")
public final class AmqpRouterProperties {

	//connections
	@Value("${amqp.hostname:localhost}")
	private String host;
	
	private String vHost;
	private String userName;
	private String userPassword;
	
	//exchanges
	private String topicExchange;	
	
	//queues
	private String evaluationsQueue;
	private String eventAlertsQueue;
	
	//bindings
	private String evaluationsQueueBinding;
	private String eventAlertsQueueBinding;
	
	//routing
	private String eventAlertsRoutingKey;
	
	public String getEventAlertsQueue() {
		return eventAlertsQueue;
	}
	public void setEventAlertsQueue(String eventAlertsQueue) {
		this.eventAlertsQueue = eventAlertsQueue;
	}
	public String getEventAlertsQueueBinding() {
		return eventAlertsQueueBinding;
	}
	public void setEventAlertsQueueBinding(String eventAlertsQueueBinding) {
		this.eventAlertsQueueBinding = eventAlertsQueueBinding;
	}
	public String getEventAlertsRoutingKey() {
		return eventAlertsRoutingKey;
	}
	public void setEventAlertsRoutingKey(String eventAlertsRoutingKey) {
		this.eventAlertsRoutingKey = eventAlertsRoutingKey;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getvHost() {
		return vHost;
	}
	public void setvHost(String vHost) {
		this.vHost = vHost;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getTopicExchange() {
		return topicExchange;
	}
	public void setTopicExchange(String topicExchange) {
		this.topicExchange = topicExchange;
	}
	public String getEvaluationsQueue() {
		return evaluationsQueue;
	}
	public void setEvaluationsQueue(String evaluationsQueue) {
		this.evaluationsQueue = evaluationsQueue;
	}
	public String getEvaluationsQueueBinding() {
		return evaluationsQueueBinding;
	}
	public void setEvaluationsQueueBinding(String evaluationsQueueBinding) {
		this.evaluationsQueueBinding = evaluationsQueueBinding;
	}
}