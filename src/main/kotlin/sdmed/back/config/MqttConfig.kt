package sdmed.back.config

import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler

@Configuration
open class MqttConfig {
	@Value(value = "\${mqtt.brokerUrl1}") lateinit var brokerUrl1: String
	@Value(value = "\${mqtt.brokerUrl2}") lateinit var brokerUrl2: String
	@Value(value = "\${mqtt.clientId}") lateinit var clientId: String
	@Value(value = "\${mqtt.username}") lateinit var userName: String
	@Value(value = "\${mqtt.password}") lateinit var password: String

	@Bean open fun mqttInputChannel() = DirectChannel()
	@Bean open fun mqttOutputChannel() = DirectChannel()

	@Bean
	open fun mqttConnectOptions() = MqttConnectOptions().apply {
		this.userName = this@MqttConfig.userName
		this.password = this@MqttConfig.password.toCharArray()
	}
	@Bean
	open fun mqttClientFactory() = DefaultMqttPahoClientFactory().apply {
		connectionOptions = mqttConnectOptions()
	}
	//	@Bean
//	fun mqttInbound() = MqttPahoMessageDrivenChannelAdapter(brokerUrl, clientId, mqttClientFactory()).apply {
//		outputChannel = mqttInputChannel()
//	}
//	@Bean
//	@ServiceActivator(inputChannel = "mqttInputChannel")
//	fun handler(): MessageHandler = MessageHandler { x ->
//		println(x.payload)
//	}
	@Bean
	@ServiceActivator(inputChannel = "mqttOutputChannel")
	open fun mqttOutbound() = MqttPahoMessageHandler(brokerUrl1, clientId, mqttClientFactory()).apply {
		setAsync(true)
	}
	@Bean
	open fun sendMessageFlow() = IntegrationFlow.from(mqttOutputChannel())
		.handle(mqttOutbound())
		.get()
}