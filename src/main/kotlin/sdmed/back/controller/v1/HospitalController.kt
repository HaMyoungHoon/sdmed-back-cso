package sdmed.back.controller.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRoles
import sdmed.back.model.sqlCSO.HospitalModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.HospitalService
import sdmed.back.service.ResponseService

@Tag(name = "HospitalController")
@RestController
@RequestMapping(value = ["/v1/hospital"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class HospitalController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var hospitalService: HospitalService
	@Autowired lateinit var azureBlobService: AzureBlobService

	@Operation(summary = "병원 정보 조회")
	@GetMapping(value = ["/all"])
	fun getHospitalAll(@RequestHeader token: String) =
		responseService.getResult(hospitalService.getAllHospital(token))
	@Operation(summary = "병원 조회 like code, innerName, orgName")
	@GetMapping(value = ["/all/search"])
	fun getHospitalAllSearch(@RequestHeader token: String,
													 @RequestParam searchString: String,
													 @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(hospitalService.getHospitalAllSearch(token, searchString, isSearchTypeCode))
	@Operation(summary = "병원 조회")
	@GetMapping(value = ["/{thisPK}"])
	fun getHospitalData(@RequestHeader token: String,
											@PathVariable thisPK: String) =
		responseService.getResult(hospitalService.getHospitalData(token, thisPK))

	@Operation(summary = "병원 정보 추가")
	@PostMapping(value = ["/add"])
	fun postHospitalData(@RequestHeader token: String,
											 @RequestBody hospitalData: HospitalModel) =
		responseService.getResult(hospitalService.addHospitalData(token, hospitalData))
	@Operation(summary = "병원 데이터 엑셀 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader token: String,
	                        @RequestParam file: MultipartFile) =
		responseService.getResult(hospitalService.hospitalUpload(token, file))
	@Operation(summary = "병원 사업자등록증 업로드")
	@PostMapping(value = ["/imageUpload"], consumes = ["multipart/form-data"])
	fun postImageUpload(@RequestHeader token: String,
	                    @RequestParam thisPK: String,
	                    @RequestParam file: MultipartFile): IRestResult {
		hospitalService.isValid(token)
		val tokenUser = hospitalService.getUserDataByToken(token)
		if (!hospitalService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val hospitalData = hospitalService.getHospitalData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, today, tokenUser.thisPK)
		hospitalData.imageUrl = blobUrl
		return responseService.getResult(hospitalService.hospitalDataModify(token, hospitalData))
	}

	@Operation(summary = "병원 데이터 엑셀 샘플 다운로드")
	@GetMapping(value = ["/sampleDownloadExcel"])
	fun getSampleDownloadExcel(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.HOSPITAL)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}
	@Operation(summary = "병원 수정")
	@PutMapping(value = ["/modify"])
	fun putHospitalDataModify(@RequestHeader token: String,
														@RequestBody hospitalData: HospitalModel) =
		responseService.getResult(hospitalService.hospitalDataModify(token, hospitalData))
}