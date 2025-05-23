package sdmed.back.advice

import jakarta.servlet.AsyncContext
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
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
		responseService.getFailResult(getMessage("accessDeniedException.code").toInt(), getMessageMerge("accessDeniedException.msg", exception.message))

	@ExceptionHandler(AuthenticationEntryPointException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun authenticationEntryPointException(request : HttpServletRequest, exception : AuthenticationEntryPointException) =
		responseService.getFailResult(getMessage("authenticationEntryPointException.code").toInt(), getMessageMerge("authenticationEntryPointException.msg", exception.message))

	@ExceptionHandler(CommunicationException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun communicationException(request: HttpServletRequest, exception: CommunicationException) =
		responseService.getFailResult(getMessage("communicationException.code").toInt(), getMessageMerge("communicationException.msg", exception.message))

	@ExceptionHandler(ConfirmPasswordUnMatchException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun confirmPasswordUnMatchException(request: HttpServletRequest, exception: ConfirmPasswordUnMatchException) =
		responseService.getFailResult(getMessage("confirmPasswordUnMatchException.code").toInt(), getMessageMerge("confirmPasswordUnMatchException.msg", exception.message))

	@ExceptionHandler(FileDownloadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileDownloadException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("fileDownloadException.code").toInt(), getMessageMerge("fileDownloadException.msg", exception.message))

	@ExceptionHandler(FileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun fileUploadException(request: HttpServletRequest, exception: FileUploadException) =
		responseService.getFailResult(getMessage("fileUploadException.code").toInt(), getMessageMerge("fileUploadException.msg", exception.message))

	@ExceptionHandler(HosDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hosDataFileUploadException(request: HttpServletRequest, exception: HosDataFileUploadException) =
		responseService.getFailResult(getMessage("hosDataFileUploadException.code").toInt(), getMessageMerge("hosDataFileUploadException.msg", exception.message))

	@ExceptionHandler(HospitalNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hospitalNotFoundException(request: HttpServletRequest, exception: HospitalNotFoundException) =
		responseService.getFailResult(getMessage("hospitalNotFoundException.code").toInt(), getMessageMerge("hospitalNotFoundException.msg", exception.message))

	@ExceptionHandler(MedicineDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun medicineDataFileUploadException(request: HttpServletRequest, exception: MedicineDataFileUploadException) =
		responseService.getFailResult(getMessage("medicineDataFileUploadException.code").toInt(), getMessageMerge("medicineDataFileUploadException.msg", exception.message))

	@ExceptionHandler(MedicineNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun medicineNotFoundException(request: HttpServletRequest, exception: MedicineNotFoundException) =
		responseService.getFailResult(getMessage("medicineNotFoundException.code").toInt(), getMessageMerge("medicineNotFoundException.msg", exception.message))

	@ExceptionHandler(NotFoundDeptException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundDeptException(request: HttpServletRequest, exception: NotFoundDeptException) =
		responseService.getFailResult(getMessage("notFoundDeptException.code").toInt(), getMessageMerge("notFoundDeptException.msg", exception.message))

	@ExceptionHandler(NotFoundLanguageException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundLanguageException(request: HttpServletRequest, exception: FileDownloadException) =
		responseService.getFailResult(getMessage("notFoundLanguage.code").toInt(), getMessageMerge("notFoundLanguage.msg", exception.message))

	@ExceptionHandler(NotFoundRolesException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundRolesException(request: HttpServletRequest, exception: NotFoundRolesException) =
		responseService.getFailResult(getMessage("notFoundRolesException.code").toInt(), getMessageMerge("notFoundRolesException.msg", exception.message))

	@ExceptionHandler(NotFoundSystemException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notFoundSystemException(request: HttpServletRequest, exception: NotFoundSystemException) =
		responseService.getFailResult(getMessage("notFoundSystemException.code").toInt(), getMessageMerge("notFoundSystemException.msg", exception.message))

	@ExceptionHandler(NotOwnerException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notOwnerException(request : HttpServletRequest, exception : NotOwnerException) =
		responseService.getFailResult(getMessage("notOwnerException.code").toInt(), getMessageMerge("notOwnerException.msg", exception.message))

	@ExceptionHandler(NotValidOperationException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun notValidOperationException(request : HttpServletRequest, exception: NotValidOperationException) =
		responseService.getFailResult(getMessage("notValidOperationException.code").toInt(), getMessageMerge("notValidOperationException.msg", exception.message))

	@ExceptionHandler(PharmaDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmaDataFileUploadException(request : HttpServletRequest, exception: PharmaDataFileUploadException) =
		responseService.getFailResult(getMessage("pharmaDataFileUploadException.code").toInt(), getMessageMerge("pharmaDataFileUploadException.msg", exception.message))

	@ExceptionHandler(PharmaNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmaNotFoundException(request : HttpServletRequest, exception: PharmaNotFoundException) =
		responseService.getFailResult(getMessage("pharmaNotFoundException.code").toInt(), getMessageMerge("pharmaNotFoundException.msg", exception.message))

	@ExceptionHandler(ResourceAlreadyExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceAlreadyExistException(request: HttpServletRequest, exception: ResourceAlreadyExistException) =
		responseService.getFailResult(getMessage("resourceAlreadyExistException.code").toInt(), getMessageMerge("resourceAlreadyExistException.msg", exception.message))

	@ExceptionHandler(ResourceNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun resourceNotExistException(req: HttpServletRequest, exception: ResourceNotExistException) =
		responseService.getFailResult(getMessage("resourceNotExistException.code").toInt(), getMessageMerge("resourceNotExistException.msg", exception.message))

	@ExceptionHandler(SignInFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signInFailedException(request : HttpServletRequest, exception : SignInFailedException) =
		responseService.getFailResult(getMessage("signInFailedException.code").toInt(), getMessageMerge("signInFailedException.msg", exception.message))

	@ExceptionHandler(SignUpFailedException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpFailedException(request : HttpServletRequest, exception : SignUpFailedException) =
		responseService.getFailResult(getMessage("signUpFailedException.code").toInt(), getMessageMerge("signUpFailedException.msg", exception.message))

	@ExceptionHandler(SignUpIDConditionException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpIDConditionException(request : HttpServletRequest, exception : SignUpIDConditionException) =
		responseService.getFailResult(getMessage("signUpIDConditionException.code").toInt(), getMessageMerge("signUpIDConditionException.msg", exception.message))

	@ExceptionHandler(SignUpPWConditionException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpPWConditionException(request : HttpServletRequest, exception : SignUpPWConditionException) =
		responseService.getFailResult(getMessage("signUpPWConditionException.code").toInt(), getMessageMerge("signUpPWConditionException.msg", exception.message))

	@ExceptionHandler(UserDataFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userDataFileUploadException(request: HttpServletRequest, exception: UserDataFileUploadException) =
		responseService.getFailResult(getMessage("userDataFileUploadException.code").toInt(), getMessageMerge("userDataFileUploadException.msg", exception.message))

	@ExceptionHandler(UserNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userNotFoundException(request: HttpServletRequest, exception: UserNotFoundException) =
		responseService.getFailResult(getMessage("userNotFoundException.code").toInt(), getMessageMerge("userNotFoundException.msg", exception.message))

	@ExceptionHandler(MultipartException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun multipartException(request: HttpServletRequest, exception: MultipartException) =
		responseService.getFailResult(getMessage("multipartException.code").toInt(), getMessageMerge("multipartException.msg", exception.message))

	@ExceptionHandler(HospitalExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hospitalExistException(request: HttpServletRequest, exception: HospitalExistException) =
		responseService.getFailResult(getMessage("hospitalExistException.code").toInt(), getMessageMerge("hospitalExistException.msg", exception.message))

	@ExceptionHandler(PharmaExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmaExistException(request: HttpServletRequest, exception: PharmaExistException) =
		responseService.getFailResult(getMessage("pharmaExistException.code").toInt(), getMessageMerge("pharmaExistException.msg", exception.message))

	@ExceptionHandler(MedicineExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun medicineExistException(request: HttpServletRequest, exception: MedicineExistException) =
		responseService.getFailResult(getMessage("medicineExistException.code").toInt(), getMessageMerge("medicineExistException.msg", exception.message))

	@ExceptionHandler(CurrentPWNotMatchException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun currentPWNotMatchException(request: HttpServletRequest, exception: CurrentPWNotMatchException) =
		responseService.getFailResult(getMessage("currentPWNotMatchException.code").toInt(), getMessageMerge("currentPWNotMatchException.msg", exception.message))

	@ExceptionHandler(RequestModelNotFoundException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun currentPWNotMatchException(request: HttpServletRequest, exception: RequestModelNotFoundException) =
		responseService.getFailResult(getMessage("requestModelNotFoundException.code").toInt(), getMessageMerge("requestModelNotFoundException.msg", exception.message))

	@ExceptionHandler(EDIApplyDateExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediApplyDateExistException(request: HttpServletRequest, exception: EDIApplyDateExistException) =
		responseService.getFailResult(getMessage("ediApplyDateExistException.code").toInt(), getMessageMerge("ediApplyDateExistException.msg", exception.message))

	@ExceptionHandler(EDIApplyDateNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediApplyDateNotExistException(request: HttpServletRequest, exception: EDIApplyDateNotExistException) =
		responseService.getFailResult(getMessage("ediApplyDateNotExistException.code").toInt(), getMessageMerge("ediApplyDateNotExistException.msg", exception.message))

	@ExceptionHandler(EDIUploadNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediUploadNotExistException(request: HttpServletRequest, exception: EDIUploadNotExistException) =
		responseService.getFailResult(getMessage("ediUploadNotExistException.code").toInt(), getMessageMerge("ediUploadNotExistException.msg", exception.message))

	@ExceptionHandler(EDIPharmaDueDateExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediPharmaDueDateExistException(request: HttpServletRequest, exception: EDIPharmaDueDateExistException) =
		responseService.getFailResult(getMessage("ediPharmaDueDateExistException.code").toInt(), getMessageMerge("ediPharmaDueDateExistException.msg", exception.message))

	@ExceptionHandler(EDIUploadPharmaNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediUploadPharmaNotExistException(request: HttpServletRequest, exception: EDIUploadPharmaNotExistException) =
		responseService.getFailResult(getMessage("ediUploadPharmaNotExistException.code").toInt(), getMessageMerge("ediUploadPharmaNotExistException.msg", exception.message))

	@ExceptionHandler(EDIUploadPharmaMedicineNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediUploadPharmaMedicineNotExistException(request: HttpServletRequest, exception: EDIUploadPharmaMedicineNotExistException) =
		responseService.getFailResult(getMessage("ediUploadPharmaMedicineNotExistException.code").toInt(), getMessageMerge("ediUploadPharmaMedicineNotExistException.msg", exception.message))

	@ExceptionHandler(QnAHeaderNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun qnaHeaderNotExistException(request: HttpServletRequest, exception: QnAHeaderNotExistException) =
		responseService.getFailResult(getMessage("qnaHeaderNotExistException.code").toInt(), getMessageMerge("qnaHeaderNotExistException.msg", exception.message))

	@ExceptionHandler(QnAContentNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun qnaContentNotExistException(request: HttpServletRequest, exception: QnAContentNotExistException) =
		responseService.getFailResult(getMessage("qnaContentNotExistException.code").toInt(), getMessageMerge("qnaContentNotExistException.msg", exception.message))

	@ExceptionHandler(EDIPharmaDueDateNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediPharmaDueDateNotExistException(request: HttpServletRequest, exception: EDIPharmaDueDateNotExistException) =
		responseService.getFailResult(getMessage("ediPharmaDueDateNotExistException.code").toInt(), getMessageMerge("ediPharmaDueDateNotExistException.msg", exception.message))

	@ExceptionHandler(EDIUploadPharmaExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediUploadPharmaExistException(request: HttpServletRequest, exception: EDIUploadPharmaExistException) =
		responseService.getFailResult(getMessage("ediUploadPharmaExistException.code").toInt(), getMessageMerge("ediUploadPharmaExistException.msg", exception.message))

	@ExceptionHandler(EDIFileNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediFileNotExistException(request: HttpServletRequest, exception: EDIFileNotExistException) =
		responseService.getFailResult(getMessage("ediFileNotExistException.code").toInt(), getMessageMerge("ediFileNotExistException.msg", exception.message))

	@ExceptionHandler(SignUpIDExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun signUpIDExistException(request: HttpServletRequest, exception: SignUpIDExistException) =
		responseService.getFailResult(getMessage("signUpIDExistException.code").toInt(), getMessageMerge("signUpIDExistException.msg", exception.message))

	@ExceptionHandler(VersionCheckNotExistException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun versionCheckNotExistException(request: HttpServletRequest, exception: VersionCheckNotExistException) =
		responseService.getFailResult(getMessage("versionCheckNotExistException.code").toInt(), getMessageMerge("versionCheckNotExistException.msg", exception.message))

	@ExceptionHandler(AuthNumberCheckException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun authNumberCheckException(request: HttpServletRequest, exception: AuthNumberCheckException) =
		responseService.getFailResult(getMessage("authNumberCheckException.code").toInt(), getMessageMerge("authNumberCheckException.msg", exception.message))

	@ExceptionHandler(PhoneNumberFormatException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun phoneNumberFormatException(request: HttpServletRequest, exception: PhoneNumberFormatException) =
		responseService.getFailResult(getMessage("phoneNumberFormatException.code").toInt(), getMessageMerge("phoneNumberFormatException.msg", exception.message))

	@ExceptionHandler(UserQuarantineException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userQuarantineException(request: HttpServletRequest, exception: UserQuarantineException) =
		responseService.getFailResult(getMessage("userQuarantineException.code").toInt(), getMessageMerge("userQuarantineException.msg", exception.message))

	@ExceptionHandler(EDIDueDateFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun ediDueDateFileUploadException(request: HttpServletRequest, exception: EDIDueDateFileUploadException) =
		responseService.getFailResult(getMessage("ediDueDateFileUploadException.code").toInt(), getMessageMerge("ediDueDateFileUploadException.msg", exception.message))

	@ExceptionHandler(UserMappingFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userMappingFileUploadException(request: HttpServletRequest, exception: UserMappingFileUploadException) =
		responseService.getFailResult(getMessage("userMappingFileUploadException.code").toInt(), getMessageMerge("userMappingFileUploadException.msg", exception.message))

	@ExceptionHandler(HospitalTempFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun hospitalTempFileUploadException(request: HttpServletRequest, exception: HospitalTempFileUploadException) =
		responseService.getFailResult(getMessage("hospitalTempFileUploadException.code").toInt(), getMessageMerge("hospitalTempFileUploadException.msg", exception.message))

	@ExceptionHandler(PharmacyTempFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun pharmacyTempFileUploadException(request: HttpServletRequest, exception: PharmacyTempFileUploadException) =
		responseService.getFailResult(getMessage("pharmacyTempFileUploadException.code").toInt(), getMessageMerge("pharmacyTempFileUploadException.msg", exception.message))

	@ExceptionHandler(UserTrainingFileUploadException::class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	protected fun userTrainingFileUploadException(request: HttpServletRequest, exception: UserTrainingFileUploadException) =
		responseService.getFailResult(getMessage("userTrainingFileUploadException.code").toInt(), getMessageMerge("userTrainingFileUploadException.msg", exception.message))

	@ExceptionHandler(AsyncRequestTimeoutException::class)
	@ResponseStatus(HttpStatus.OK)
	protected fun asyncRequestTimeoutException(request: HttpServletRequest, exception: AsyncRequestTimeoutException): AsyncContext =
		request.startAsync()

	protected fun getMessageMerge(code: String, extraMessage: String?) = if (extraMessage.isNullOrBlank()) {
		getMessage(code, null)
	} else {
		"${getMessage(code, null)} : $extraMessage"
	}

	protected fun getMessage(code: String) = getMessage(code, null)
	protected fun getMessage(code: String, args: Array<Any>?) = messageSource.getMessage(code, args, LocaleContextHolder.getLocale())
}