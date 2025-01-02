package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.common.IRestResult
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.service.MyInfoService


@Tag(name = "내정보")
@RestController
@RequestMapping(value = ["/extra/myInfo"])
class ExtraMyInfoController: FControllerBase() {
	@Autowired lateinit var myInfoService: MyInfoService

	@Operation(summary = "유저 정보")
	@GetMapping(value = ["/data"])
	fun getData(@RequestHeader token: String,
	            @RequestParam(required = false) childView: Boolean = false,
	            @RequestParam(required = false) relationView: Boolean = false,
	            @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(myInfoService.getMyData(token, childView, relationView, pharmaOwnMedicineView))

	@Operation(summary = "패스워드 변경")
	@PutMapping(value = ["/passwordChange"])
	fun putPasswordChange(@RequestHeader token: String,
	                      @RequestParam currentPW: String,
	                      @RequestParam afterPW: String,
	                      @RequestParam confirmPW: String) =
		responseService.getResult(myInfoService.passwordChange(token, currentPW, afterPW, confirmPW))

	@Operation(summary = "유저 세금계산서 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/taxImage"])
	fun putUserTaxImageUrl(@RequestHeader token: String,
	                       @PathVariable thisPK: String,
	                       @RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(myInfoService.userTaxImageUrlModify(token, thisPK, blobModel.blobUrl).apply {
			azureBlobService.blobUploadSave(blobModel.newSave())
		})
	@Operation(summary = "유저 통장 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/bankImage"])
	fun putUserBankImageUrl(@RequestHeader token: String,
	                        @PathVariable thisPK: String,
	                        @RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(myInfoService.userBankImageUrlModify(token, thisPK, blobModel.blobUrl).apply {
			azureBlobService.blobUploadSave(blobModel.newSave())
		})
}