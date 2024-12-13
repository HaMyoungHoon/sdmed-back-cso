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
import sdmed.back.config.*
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.service.HospitalListService

@Tag(name = "병원목록")
@RestController
@RequestMapping(value = ["/intra/hospitalList"])
class HospitalListController: FControllerBase() {
	@Autowired lateinit var hospitalListService: HospitalListService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(hospitalListService.getList(token))
	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(hospitalListService.getData(token, thisPK))
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

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
								@RequestParam file: MultipartFile) =
		responseService.getResult(hospitalListService.hospitalUpload(token, file))
	@Operation(summary = "병원 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody hospitalModel: HospitalModel) =
		responseService.getResult(hospitalListService.addHospitalData(token, hospitalModel))

	@Operation(summary = "병원 사업자 등록증 업로드")
	@PostMapping(value = ["/file/{thisPK}/image"])
	fun postImage(@RequestHeader token: String,
	              @PathVariable thisPK: String,
	              @RequestParam file: MultipartFile): IRestResult {
		hospitalListService.isValid(token)
		val tokenUser = hospitalListService.getUserDataByToken(token)
		if (!hospitalListService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val hospitalData = hospitalListService.getData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, "hospital/$today", tokenUser.thisPK)
		hospitalData.imageUrl = blobUrl
		return responseService.getResult(hospitalListService.hospitalDataModify(token, hospitalData))
	}

	@Operation(summary = "병원 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
							@RequestBody hospitalModel: HospitalModel) =
		responseService.getResult(hospitalListService.hospitalDataModify(token, hospitalModel))
	@Operation(summary = "병원 사업자 등록증 업로드")
	@PutMapping(value = ["/file/{thisPK}/image"])
	fun putImage(@RequestHeader token: String,
	             @PathVariable thisPK: String,
	             @RequestBody blobModel: BlobUploadModel): IRestResult {
		hospitalListService.isValid(token)
		val tokenUser = hospitalListService.getUserDataByToken(token)
		if (!hospitalListService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val hospitalData = hospitalListService.getData(token, thisPK)
		azureBlobService.blobUploadSave(blobModel.newSave())
		hospitalData.imageUrl = blobModel.blobUrl
		return responseService.getResult(hospitalListService.hospitalDataModify(token, hospitalData))
	}
}