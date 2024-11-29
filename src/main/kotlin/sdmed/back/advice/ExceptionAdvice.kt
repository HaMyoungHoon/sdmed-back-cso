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
import org.springframework.web.multipart.MultipartException
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

	@ExceptionHandler(AccessDeniedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun accessDeniedException(request : HttpServletRequest, exception : AccessDeniedException) =
		responseService.getFailResult(getMessage("accessDeniedException.code").toInt(), "${getMessage("accessDeniedException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(AuthenticationEntryPointException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun authenticationEntryPointException(request : HttpServletRequest, exception : AuthenticationEntryPointException) =
		responseService.getFailResult(getMessage("authenticationEntryPointException.code").toInt(), "${getMessage("authenticationEntryPointException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(CommunicationException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun communicationException(request: HttpServletRequest, exception: CommunicationException) =
		responseService.getFailResult(getMessage("communicationException.code").toInt(), "${getMessage("communicationException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(ConfirmPasswordUnMatchException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun confirmPasswordUnMatchException(request: HttpServletRequest, exception: ConfirmPasswordUnMatchException) =
		responseService.getFailResult(getMessage("confirmPasswordUnMatchException.code").toInt(), "${getMessage("confirmPasswordUnMatchException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(FileDownloadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileDownloadException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("fileDownloadException.code").toInt(), "${getMessage("fileDownloadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(FileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileUploadException(request: HttpServletRequest, exception: FileUploadException) =
		responseService.getFailResult(getMessage("fileUploadException.code").toInt(), "${getMessage("fileUploadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(HosDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hosDataFileUploadException(request: HttpServletRequest, exception: HosDataFileUploadException) =
		responseService.getFailResult(getMessage("hosDataFileUploadException.code").toInt(), "${getMessage("hosDataFileUploadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(HospitalNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hospitalNotFoundException(request: HttpServletRequest, exception: HospitalNotFoundException) =
		responseService.getFailResult(getMessage("hospitalNotFoundException.code").toInt(), "${getMessage("hospitalNotFoundException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(MedicineDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun medicineDataFileUploadException(request: HttpServletRequest, exception: MedicineDataFileUploadException) =
		responseService.getFailResult(getMessage("medicineDataFileUploadException.code").toInt(), "${getMessage("medicineDataFileUploadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(MedicineNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun medicineNotFoundException(request: HttpServletRequest, exception: MedicineNotFoundException) =
		responseService.getFailResult(getMessage("medicineNotFoundException.code").toInt(), "${getMessage("medicineNotFoundException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotFoundDeptException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundDeptException(request: HttpServletRequest, exception: NotFoundDeptException) =
		responseService.getFailResult(getMessage("notFoundDeptException.code").toInt(), "${getMessage("notFoundDeptException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotFoundLanguageException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundLanguageException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("notFoundLanguage.code").toInt(), "${getMessage("notFoundLanguage.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotFoundRolesException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundRolesException(request: HttpServletRequest, exception: NotFoundRolesException) =
		responseService.getFailResult(getMessage("notFoundRolesException.code").toInt(), "${getMessage("notFoundRolesException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotFoundSystemException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundSystemException(request: HttpServletRequest, exception: NotFoundSystemException) =
		responseService.getFailResult(getMessage("notFoundSystemException.code").toInt(), "${getMessage("notFoundSystemException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotOwnerException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notOwnerException(request : HttpServletRequest, exception : NotOwnerException) =
		responseService.getFailResult(getMessage("notOwnerException.code").toInt(), "${getMessage("notOwnerException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(NotValidOperationException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notValidOperationException(request : HttpServletRequest, exception: NotValidOperationException) =
		responseService.getFailResult(getMessage("notValidOperationException.code").toInt(), "${getMessage("notValidOperationException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(PharmaDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmaDataFileUploadException(request : HttpServletRequest, exception: PharmaDataFileUploadException) =
		responseService.getFailResult(getMessage("pharmaDataFileUploadException.code").toInt(), "${getMessage("pharmaDataFileUploadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(PharmaNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmaNotFoundException(request : HttpServletRequest, exception: PharmaNotFoundException) =
		responseService.getFailResult(getMessage("pharmaNotFoundException.code").toInt(), "${getMessage("pharmaNotFoundException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(ResourceAlreadyExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceAlreadyExistException(request: HttpServletRequest, exception: ResourceAlreadyExistException) =
		responseService.getFailResult(getMessage("resourceAlreadyExistException.code").toInt(), "${getMessage("resourceAlreadyExistException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(ResourceNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceNotExistException(req: HttpServletRequest, exception: ResourceNotExistException) =
		responseService.getFailResult(getMessage("resourceNotExistException.code").toInt(), "${getMessage("resourceNotExistException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(SignInFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signInFailedException(request : HttpServletRequest, exception : SignInFailedException) =
		responseService.getFailResult(getMessage("signInFailedException.code").toInt(), "${getMessage("signInFailedException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(SignUpFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpFailedException(request : HttpServletRequest, exception : SignUpFailedException) =
		responseService.getFailResult(getMessage("signUpFailedException.code").toInt(), "${getMessage("signUpFailedException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(SignUpIDConditionException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpIDConditionException(request : HttpServletRequest, exception : SignUpIDConditionException) =
		responseService.getFailResult(getMessage("signUpIDConditionException.code").toInt(), "${getMessage("signUpIDConditionException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(SignUpPWConditionException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpPWConditionException(request : HttpServletRequest, exception : SignUpPWConditionException) =
		responseService.getFailResult(getMessage("signUpPWConditionException.code").toInt(), "${getMessage("signUpPWConditionException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(UserDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userDataFileUploadException(request: HttpServletRequest, exception: UserDataFileUploadException) =
		responseService.getFailResult(getMessage("userDataFileUploadException.code").toInt(), "${getMessage("userDataFileUploadException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(UserNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userNotFoundException(request: HttpServletRequest, exception: UserNotFoundException) =
		responseService.getFailResult(getMessage("userNotFoundException.code").toInt(), "${getMessage("userNotFoundException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(MultipartException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun multipartException(request: HttpServletRequest, exception: MultipartException) =
		responseService.getFailResult(getMessage("multipartException.code").toInt(), "${getMessage("multipartException.msg")} : ${exception.message.toString()}")

	@ExceptionHandler(AsyncRequestTimeoutException::class)
	@ResponseStatus(HttpStatus.OK)
	protected fun asyncRequestTimeoutException(request: HttpServletRequest, exception: AsyncRequestTimeoutException): AsyncContext =
		request.startAsync()

	protected fun getMessage(code: String) = getMessage(code, null)
	protected fun getMessage(code: String, args: Array<Any>?) = messageSource.getMessage(code, args, LocaleContextHolder.getLocale())
}