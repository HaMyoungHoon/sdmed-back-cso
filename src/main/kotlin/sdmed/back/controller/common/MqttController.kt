package sdmed.back.controller.common

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.common.MqttContentModel
import sdmed.back.service.MqttService

@Tag(name = "Mqtt")
@RestController
@RequestMapping(value = ["/mqtt"])
class MqttController: FControllerBase() {
	@Autowired lateinit var mqttService: MqttService

	@Hidden
	@GetMapping(value = ["/subscribe"])
	fun getSubscribe(@RequestHeader token: String) =
		responseService.getResult(mqttService.getMqttConnectData(token))
	@Hidden
	@PostMapping(value = ["/publish"])
	fun postPublish(@RequestHeader token: String,
									@RequestParam topic: String,
									@RequestBody mqttContentModel: MqttContentModel) =
		responseService.getResult(mqttService.sendMessage(token, topic, mqttContentModel))

}