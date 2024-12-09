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
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.MedicineModel
import sdmed.back.service.MedicineService
import sdmed.back.service.ResponseService

@Tag(name = "약품목록")
@RestController
@RequestMapping(value = ["/intra/medicineList"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class MedicineListController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var medicineService: MedicineService
	
	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicineService.getAllMedicine(token))

	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(medicineService.getMedicineData(token, thisPK))

	@Operation(summary = "주성분코드 리스트")
	@GetMapping(value = ["/list/mainIngredient"])
	fun getMainIngredientList(@RequestHeader token: String) =
		responseService.getResult(medicineService.getMainIngredientList(token))

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	              @RequestParam file: MultipartFile) =
		responseService.getResult(medicineService.medicineUpload(token, file))

	@Operation(summary = "약품 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody medicineModel: MedicineModel) =
		responseService.getResult(medicineService.addMedicineData(token, medicineModel))

	@Operation(summary = "엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.MEDICINE).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
				.body(x)
		}

	@Operation(summary = "약품 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
	            @RequestBody medicineModel: MedicineModel) =
		responseService.getResult(medicineService.medicineDataModify(token, medicineModel))
}