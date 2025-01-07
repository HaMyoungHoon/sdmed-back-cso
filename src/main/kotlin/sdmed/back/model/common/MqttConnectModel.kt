package sdmed.back.model.common

data class MqttConnectModel(
	var brokerUrl: MutableList<String> = arrayListOf(),
	var topic: MutableList<String> = arrayListOf(),
	var userName: String = "",
	var password: String = "",
) {
}