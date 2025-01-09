package sdmed.back.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.integration.mqtt.core.MqttPahoClientFactory
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.MqttConnectModel
import sdmed.back.model.common.MqttContentModel
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles

class MqttService: FServiceBase() {
	@Value(value = "\${mqtt.brokerUrl1}") lateinit var brokerUrl1: String
	@Value(value = "\${mqtt.brokerUrl2}") lateinit var brokerUrl2: String
	@Value(value = "\${mqtt.clientId}") lateinit var clientId: String
	@Value(value = "\${mqtt.username}") lateinit var userName: String
	@Value(value = "\${mqtt.password}") lateinit var password: String
	@Autowired lateinit var mqttOutputChannel: MessageChannel
	@Autowired lateinit var mqttClientFactory: MqttPahoClientFactory
	@Autowired lateinit var objectMapper: ObjectMapper

	fun getMqttConnectData(token: String): MqttConnectModel = MqttConnectModel().apply {
		this.brokerUrl = getBrokerUrl()
		this.userName = this@MqttService.userName
		this.password = this@MqttService.password
		val tokenUser = getUserDataByToken(token)
		if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			this.topic = getEmployeeTopic(tokenUser.thisPK)
		} else if (haveRole(tokenUser, UserRole.BusinessMan.toS())){
			this.topic = getBusinessman(tokenUser.thisPK)
		} else {
			this.topic = getDefaultTopic()
		}
	}
	fun sendMessage(token: String, topic: String, payload: MqttContentModel) {
		val tokenUser = getUserDataByToken(token)
		payload.senderPK = tokenUser.thisPK
		payload.senderName = tokenUser.name
		val msg = if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			MessageBuilder.withPayload(objectMapper.writeValueAsBytes(payload))
				.setHeader("mqtt_topic", topic)
				.build()
		} else if (haveRole(tokenUser, UserRole.BusinessMan.toS())) {
			MessageBuilder.withPayload(objectMapper.writeValueAsBytes(payload))
				.setHeader("mqtt_topic", "private/${tokenUser.thisPK}")
				.build()
		} else {
			throw NotValidOperationException()
		}
		mqttOutputChannel.send(msg)
	}

	fun getMqttClient(): IMqttClient = mqttClientFactory.getClientInstance(brokerUrl1, clientId)
	fun getMqttClient(brokerUrl: String? = null, clientId: String? = null): IMqttClient = mqttClientFactory.getClientInstance(brokerUrl ?: this.brokerUrl1, clientId ?: this.clientId)
	fun disconnectMqtt() {
		try {
			getMqttClient().let {
				if (it.isConnected) {
					it.disconnect()
				}
			}
		} catch (_: Exception) {
		}
	}
	fun disconnectMqtt(brokerUrl: String? = null, clientId: String? = null) {
		try {
			getMqttClient(brokerUrl, clientId).let {
				if (it.isConnected) {
					it.disconnect()
				}
			}
		} catch (_: Exception) {
		}
	}

	fun getBrokerUrl(): MutableList<String> {
		val ret = mutableListOf<String>()
		ret.add(brokerUrl1)
		ret.add(brokerUrl2)
		return ret
	}
	fun getEmployeeTopic(thisPK: String): MutableList<String> {
		val ret = mutableListOf<String>()
		ret.add("notice")
		ret.add("private/${thisPK}")
		userChildPKRepository.selectAllByMotherPK(thisPK).forEach { x ->
			ret.add("private/${x}")
		}
		return ret
	}
	fun getBusinessman(thisPK: String): MutableList<String> {
		val ret = mutableListOf<String>()
		ret.add("notice")
		ret.add("private/${thisPK}")
		return ret
	}
	fun getDefaultTopic(): MutableList<String> {
		val ret = mutableListOf<String>()
		ret.add("notice")
		return ret
	}
}