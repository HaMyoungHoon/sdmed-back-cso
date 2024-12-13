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
import sdmed.back.config.*
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.service.MedicineListService

@Tag(name = "약품목록")
@RestController
@RequestMapping(value = ["/intra/medicineList"])
class MedicineListController: FControllerBase() {
	@Autowired lateinit var medicineListService: MedicineListService
	
	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicineListService.getList(token))
	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(medicineListService.getMedicineData(token, thisPK))
	@Operation(summary = "주성분코드 리스트")
	@GetMapping(value = ["/list/mainIngredient"])
	fun getMainIngredientList(@RequestHeader token: String) =
		responseService.getResult(medicineListService.getMainIngredientList(token))
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

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	              @RequestParam file: MultipartFile) =
		responseService.getResult(medicineListService.medicineUpload(token, file))
	@Operation(summary = "약품 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody medicineModel: MedicineModel) =
		responseService.getResult(medicineListService.addMedicineData(token, medicineModel))

	@Operation(summary = "약품 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
	            @RequestBody medicineModel: MedicineModel) =
		responseService.getResult(medicineListService.medicineDataModify(token, medicineModel))
}