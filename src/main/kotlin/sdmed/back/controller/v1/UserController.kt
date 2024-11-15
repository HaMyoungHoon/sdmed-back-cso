package sdmed.back.controller.v1

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.FConstants
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "UserController")
@RestController
@RequestMapping(value = ["/v1/user"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class UserController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService
	@GetMapping(value = ["/signIn"])
	fun signIn(@RequestParam(required = true) id: String, @RequestParam(required = true) pw: String): IRestResult =
		responseService.getResult(userService.signIn(id, pw))
	@PostMapping(value = ["/signUp"])
	fun signUp(@RequestParam(required = true) confirmPW: String,
	           @RequestBody data: UserDataModel): IRestResult {
		return responseService.getResult(userService.signUp(confirmPW, data))
	}
	@PutMapping(value = ["/passwordChange"])
	fun passwordChange(@RequestHeader(required = true) token: String,
	                   @RequestParam(required = true) id: String,
	                   @RequestParam(required = true) changePW: String): IRestResult {
		return responseService.getResult(userService.passwordChange(token, id, changePW))
	}
	@PostMapping(value = ["/tokenRefresh"])
	fun tokenRefresh(@RequestHeader(required = true) token: String): IRestResult {
		return responseService.getResult(userService.tokenRefresh(token))
	}
	@GetMapping(value = ["/userData"])
	fun getUserData(@RequestHeader(required = true) token: String, @RequestParam(required = false) id: String?): IRestResult {
		if (id == null) {
			return responseService.getResult(userService.getUserDataByToken(token))
		}

		if (userService.haveRole(token, UserRole.Admin.toS())) {
			return responseService.getResult(userService.getUserData(id))
		}
		return responseService.getResult(userService.getUserDataByToken(token))
	}
	@PutMapping(value = ["/userStatusModify"])
	fun putUserStatusModify(@RequestHeader(required = true) token: String,
	                        @RequestParam(required = true) id: String,
	                        @RequestParam(required = true) status: UserStatus): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userStatusModify(token, id, status))
	}

	@GetMapping(value = ["/statusList"])
	fun getStatusList() = responseService.getResult(userService.getUserStatusList())
	@GetMapping(value = ["/roleList"])
	fun getRoleList(): IRestResult = responseService.getResult(userService.getUserRoleList())
	@GetMapping(value = ["/deptList"])
	fun getDeptList(): IRestResult = responseService.getResult(userService.getUserDeptList())
}