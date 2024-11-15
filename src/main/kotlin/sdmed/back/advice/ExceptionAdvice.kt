package sdmed.back.advice

import jakarta.servlet.AsyncContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.async.AsyncRequestTimeoutException
import sdmed.back.service.ResponseService
import sdmed.back.advice.exception.*

@RestControllerAdvice
class ExceptionAdvice {
	@Autowired
	lateinit var responseService: ResponseService
	@Autowired
	lateinit var messageSource: MessageSource
	@ExceptionHandler(Exception::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun defaultException(req: HttpServletRequest, exception: Exception) =
		responseService.getFailResult(getMessage("unKnown.code").toInt(), exception.message.toString())
	@ExceptionHandler(UserNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userNotFoundException(request: HttpServletRequest, exception: UserNotFoundException) =
		responseService.getFailResult(getMessage("userNotFound.code").toInt(), getMessage("userNotFound.msg"))
	@ExceptionHandler(SignInFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signInFailedException(request : HttpServletRequest, exception : SignInFailedException) =
		responseService.getFailResult(getMessage("signInFailed.code").toInt(), getMessage("signInFailed.msg"))
	@ExceptionHandler(SignUpFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpFailedException(request : HttpServletRequest, exception : SignUpFailedException) =
		responseService.getFailResult(getMessage("signUpFailed.code").toInt(), getMessage("signUpFailed.msg"))
	@ExceptionHandler(AuthenticationEntryPointException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun authenticationEntryPointException(request : HttpServletRequest, exception : AuthenticationEntryPointException) =
		responseService.getFailResult(getMessage("entryPoint.code").toInt(), getMessage("entryPoint.msg"))
	@ExceptionHandler(AccessDeniedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun accessDeniedException(request : HttpServletRequest, exception : AccessDeniedException) =
		responseService.getFailResult(getMessage("accessDenied.code").toInt(), getMessage("accessDenied.msg"))
	@ExceptionHandler(ResourceNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceNotExistException(req: HttpServletRequest, exception: ResourceNotExistException) =
		responseService.getFailResult(getMessage("resourceNotExist.code").toInt(), getMessage("resourceNotExist.msg"))
	@ExceptionHandler(NotOwnerException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notOwnerException(request : HttpServletRequest, exception : NotOwnerException) =
		responseService.getFailResult(getMessage("notOwner.code").toInt(), getMessage("notOwner.msg"))
	@ExceptionHandler(FileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileUploadException(request: HttpServletRequest, exception: FileUploadException) =
		responseService.getFailResult(getMessage("fileUpload.code").toInt(), "${getMessage("fileUpload.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(FileDownloadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileDownloadException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("fileDownload.code").toInt(), "${getMessage("fileDownload.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(NotFoundLanguageException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundLanguageException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("notFoundLanguage.code").toInt(), "${getMessage("notFoundLanguage.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(NotFoundRolesException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundRolesException(request: HttpServletRequest, exception: NotFoundRolesException) =
		responseService.getFailResult(getMessage("notFoundRoles.code").toInt(), "${getMessage("notFoundRoles.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(NotFoundDeptException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundDeptException(request: HttpServletRequest, exception: NotFoundDeptException) =
		responseService.getFailResult(getMessage("notFoundDept.code").toInt(), "${getMessage("notFoundDept.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(NotFoundSystemException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundSystemException(request: HttpServletRequest, exception: NotFoundSystemException) =
		responseService.getFailResult(getMessage("notFoundSystem.code").toInt(), "${getMessage("notFoundSystem.msg")} : ${exception.message.toString()}")
	@ExceptionHandler(NotValidOperationException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notValidOperationException(request : HttpServletRequest, exception: NotValidOperationException) =
		responseService.getFailResult(getMessage("notValidOperation.code").toInt(), getMessage("notValidOperation.msg"))
	@ExceptionHandler(ResourceAlreadyExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceAlreadyExistException(request: HttpServletRequest, exception: ResourceAlreadyExistException) =
		responseService.getFailResult(getMessage("resourceAlreadyExistException.code").toInt(), getMessage("resourceAlreadyExistException.msg"))
	@ExceptionHandler(AsyncRequestTimeoutException::class)
	@ResponseStatus(HttpStatus.OK)
	protected fun asyncRequestTimeoutException(request: HttpServletRequest, exception: AsyncRequestTimeoutException): AsyncContext =
		request.startAsync()

	protected fun getMessage(code: String) = getMessage(code, null)
	protected fun getMessage(code: String, args: Array<Any>?) = messageSource.getMessage(code, args, LocaleContextHolder.getLocale())
}