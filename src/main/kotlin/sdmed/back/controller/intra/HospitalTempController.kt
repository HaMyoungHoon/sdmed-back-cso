package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.ContentsType
import sdmed.back.config.FControllerBase
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.hospital.HospitalTempFileModel
import sdmed.back.service.HospitalTempService

@Tag(name = "intra 임시병원")
@RestController
@RequestMapping(value = ["/intra/hospitalTemp"])
class HospitalTempController: FControllerBase() {
	@Autowired lateinit var hospitalTempService: HospitalTempService

	@Operation(summary = "임시병원 리스트")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
	            @RequestParam(required = false) page: Int = 0,
	            @RequestParam(required = false) size: Int = 100) =
		responseService.getResult(hospitalTempService.getHospitalList(token, page, size))
	@Operation(summary = "임시병원 자세히")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(hospitalTempService.getHospitalDetail(token, thisPK))
	@Operation(summary = "임시병원 찾기")
	@GetMapping(value = ["/list/search"])
	fun getListSearch(@RequestHeader token: String,
										@RequestParam searchString: String) =
		responseService.getResult(hospitalTempService.getHospitalContains(token, searchString))
	@Operation(summary = "가까운 병원 찾기")
	@GetMapping(value = ["/list/nearby"])
	fun getHospitalListNearBy(@RequestHeader token: String,
														@RequestParam latitude: Double,
														@RequestParam longitude: Double,
														@RequestParam(required = false) distance: Int = 1000) =
		responseService.getResult(hospitalTempService.getNearbyHospital(token, latitude, longitude, distance))
	@Operation(summary = "가까운 약국 찾기")
	@GetMapping(value = ["/list/nearby/pharmacy"])
	fun getPharmacyListNearBy(@RequestHeader token: String,
	                          @RequestParam latitude: Double,
	                          @RequestParam longitude: Double,
	                          @RequestParam(required = false) distance: Int = 1000) =
		responseService.getResult(hospitalTempService.getNearbyPharmacy(token, latitude, longitude, distance))

	@Operation(summary = "엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.HOSPITAL_TEMP).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
				.body(x)
		}
	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel/{alreadyUpdate}"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
								@RequestParam file: MultipartFile,
								@PathVariable alreadyUpdate: Int = 0) =
		responseService.getResult(hospitalTempService.hospitalTempUpload(token, file, alreadyUpdate == 1))

	@Operation(summary = "임시병원 파일 업로드")
	@PostMapping(value = ["/data/file"])
	fun postDataFile(@RequestHeader token: String,
	                 @RequestBody hospitalTempFileModel: HospitalTempFileModel) =
		responseService.getResult(hospitalTempService.hospitalTempFileUpload(token, hospitalTempFileModel))
	@Operation(summary = "임시병원 파일 업로드")
	@PostMapping(value = ["/list/file/{thisPK}"])
	fun postListFile(@RequestHeader token: String,
	                 @PathVariable thisPK: String,
	                 @RequestBody hospitalTempFileModel: List<HospitalTempFileModel>) =
		responseService.getResult(hospitalTempService.hospitalTempFileUpload(token, thisPK, hospitalTempFileModel))

	@Operation(summary = "약국 엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample/pharmacy"])
	fun getPharmacyExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.PHARMACY_TEMP).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
				.body(x)
		}
	@Operation(summary = "약국 엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel/pharmacy/{alreadyUpdate}"], consumes = ["multipart/form-data"])
	fun postPharmacyExcel(@RequestHeader token: String,
												@RequestParam file: MultipartFile,
												@PathVariable alreadyUpdate: Int = 0) =
		responseService.getResult(hospitalTempService.pharmacyTempUpload(token, file, alreadyUpdate == 1))
}