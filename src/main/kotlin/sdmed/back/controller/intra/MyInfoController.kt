package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FConstants
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "내정보")
@RestController
@RequestMapping(value = ["/intra/myInfo"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class MyInfoController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService

	@Operation(summary = "유저 정보")
	@GetMapping(value = ["/data"])
	fun getData(@RequestHeader token: String,
							@RequestParam(required = false) childView: Boolean = false,
							@RequestParam(required = false) relationView: Boolean = false,
							@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userService.getUserData(token, "", childView, relationView, pharmaOwnMedicineView))

	@Operation(summary = "패스워드 변경")
	@PutMapping(value = ["/passwordChange"])
	fun putPasswordChange(@RequestHeader token: String,
												@RequestParam currentPW: String,
												@RequestParam afterPW: String,
												@RequestParam confirmPW: String) =
		responseService.getResult(userService.passwordChange(token, currentPW, afterPW, confirmPW))
}