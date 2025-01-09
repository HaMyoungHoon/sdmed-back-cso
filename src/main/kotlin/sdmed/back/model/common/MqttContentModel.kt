package sdmed.back.model.common

data class MqttContentModel(
	var senderPK: String = "",
	var senderName: String = "",
	var content: String = "",
	var contentType: MqttContentType = MqttContentType.None,
	var targetItemPK: String = "",
) {
}