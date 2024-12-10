package sdmed.back.controller.intra

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
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.HospitalModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.HospitalService
import sdmed.back.service.ResponseService

@Tag(name = "병원목록")
@RestController
@RequestMapping(value = ["/intra/hospitalList"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class HospitalListController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var hospitalService: HospitalService
	@Autowired lateinit var azureBlobService: AzureBlobService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(hospitalService.getAllHospital(token))

	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(hospitalService.getHospitalData(token, thisPK))

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
								@RequestParam file: MultipartFile) =
		responseService.getResult(hospitalService.hospitalUpload(token, file))

	@Operation(summary = "병원 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody hospitalModel: HospitalModel) =
		responseService.getResult(hospitalService.addHospitalData(token, hospitalModel))

	@Operation(summary = "엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.HOSPITAL).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
				.body(x)
		}

	@Operation(summary = "병원 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
							@RequestBody hospitalModel: HospitalModel) =
		responseService.getResult(hospitalService.hospitalDataModify(token, hospitalModel))

	@Operation(summary = "병원 사업자 등록증 업로드")
	@PostMapping(value = ["/file/{thisPK}/image"])
	fun postImage(@RequestHeader token: String,
	              @PathVariable thisPK: String,
								@RequestParam file: MultipartFile): IRestResult {
		hospitalService.isValid(token)
		val tokenUser = hospitalService.getUserDataByToken(token)
		if (!hospitalService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val hospitalData = hospitalService.getHospitalData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, "hospital/$today", tokenUser.thisPK)
		hospitalData.imageUrl = blobUrl
		return responseService.getResult(hospitalService.hospitalDataModify(token, hospitalData))
	}
	@Operation(summary = "병원 사업자 등록증 업로드")
	@PutMapping(value = ["/file/{thisPK}/image"])
	fun putImage(@RequestHeader token: String,
	             @PathVariable thisPK: String,
	             @RequestBody blobModel: BlobUploadModel): IRestResult {
		hospitalService.isValid(token)
		val tokenUser = hospitalService.getUserDataByToken(token)
		if (!hospitalService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val hospitalData = hospitalService.getHospitalData(token, thisPK)
		azureBlobService.blobUploadSave(blobModel.newSave())
		hospitalData.imageUrl = blobModel.blobUrl
		return responseService.getResult(hospitalService.hospitalDataModify(token, hospitalData))
	}
}