package sdmed.back.controller.common

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.AccessDeniedException
import sdmed.back.config.FConstants
import sdmed.back.model.common.IRestResult

@Hidden
@Tag(name = "ExceptionController")
@RestController
@RequestMapping(value = ["/v1/exception"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class ExceptionController {
	@GetMapping(value = ["/entryPoint"])
	fun entrypointException(): IRestResult {
		throw AuthenticationEntryPointException()
	}

	@GetMapping(value = ["/accessDenied"])
	fun accessDeniedException(): IRestResult {
		throw AccessDeniedException()
	}
}