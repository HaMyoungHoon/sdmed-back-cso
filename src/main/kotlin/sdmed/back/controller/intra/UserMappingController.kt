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
import sdmed.back.config.ContentsType
import sdmed.back.config.FControllerBase
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.user.HosPharmaMedicinePairModel
import sdmed.back.service.UserMappingService

@Tag(name = "intra 유저매핑")
@RestController
@RequestMapping(value = ["/intra/userMapping"])
class UserMappingController: FControllerBase() {
	@Autowired lateinit var userMappingService: UserMappingService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(userMappingService.getList(token))
	@Operation(summary = "유저 누르면 보이는 거")
	@GetMapping(value = ["/data/user/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
	            @RequestParam(required = false) childView: Boolean = false,
	            @RequestParam(required = false) relationView: Boolean = false,
	            @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userMappingService.getUserData(token, thisPK, childView, relationView, pharmaOwnMedicineView))
	@Operation(summary = "병원 조회 like code, innerName, orgName")
	@GetMapping(value = ["/list/hospitalSearch"])
	fun getHospitalAllSearch(@RequestHeader token: String,
	                         @RequestParam searchString: String,
	                         @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(userMappingService.getHospitalAllSearch(token, searchString, isSearchTypeCode))
	@Operation(summary = "제약사 조회 like code, innerName, orgName")
	@GetMapping(value = ["/list/pharmaSearch"])
	fun getPharmaAllSearch(@RequestHeader token: String,
	                       @RequestParam searchString: String,
	                       @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(userMappingService.getPharmaAllSearch(token, searchString, isSearchTypeCode))
	@Operation(summary = "제약사 조회")
	@GetMapping(value = ["/data/pharma/{hospitalPK}/{pharmaPK}"])
	fun getPharmaData(@RequestHeader token: String,
	                  @PathVariable("pharmaPK") pharmaPK: String,
	                  @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userMappingService.getPharmaData(token, pharmaPK, pharmaOwnMedicineView))

	@Operation(summary = "유저-병원-제약사-약품 관계 변경")
	@PutMapping(value = ["/data/user/{userPK}"])
	fun putUserRelationModifyByPK(@RequestHeader token: String,
	                              @PathVariable userPK: String,
	                              @RequestBody hosPharmaMedicinePairModel: List<HosPharmaMedicinePairModel>) =
		responseService.getResult(userMappingService.userRelationModify(token, userPK, hosPharmaMedicinePairModel))

	@Operation(summary = "유저-병원-제약사-약품 관계 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.USER_MAPPING)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}
	@Operation(summary = "유저-병원-제약사-약품 관계 엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	              @RequestParam file: MultipartFile) =
		responseService.getResult(userMappingService.userRelationUpload(token, file))
	@Operation(summary = "유저-병원-제약사-약품 엑셀 다운로드")
	@GetMapping(value = ["/file/download/excel"])
	fun getDownloadExcel(@RequestHeader token: String): ResponseEntity<Resource> {
		val ret = userMappingService.getDownloadExcel(token)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_data.xlsx\"")
			.body(ret)
	}
}