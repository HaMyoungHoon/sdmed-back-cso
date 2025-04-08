package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.user.UserFileType
import sdmed.back.service.extra.ExtraMyInfoService
import java.util.Date


@Tag(name = "extra 내정보")
@RestController
@RequestMapping(value = ["/extra/myInfo"])
class ExtraMyInfoController: FControllerBase() {
	@Autowired lateinit var extraMyInfoService: ExtraMyInfoService

	@Operation(summary = "유저 정보")
	@GetMapping(value = ["/data"])
	fun getData(@RequestHeader token: String) =
		responseService.getResult(extraMyInfoService.getMyData(token))

	@Operation(summary = "패스워드 변경")
	@PutMapping(value = ["/passwordChange"])
	fun putPasswordChange(@RequestHeader token: String,
	                      @RequestParam currentPW: String,
	                      @RequestParam afterPW: String,
	                      @RequestParam confirmPW: String) =
		responseService.getResult(extraMyInfoService.passwordChange(token, currentPW, afterPW, confirmPW))

	@Operation(summary = "유저 파일 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}"])
	fun putUserFileImageUrl(@RequestHeader token: String,
	                        @PathVariable thisPK: String,
	                        @RequestParam userFileType: UserFileType,
	                        @RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(extraMyInfoService.userFileUrlModify(token, thisPK, blobModel, userFileType).apply {
			azureBlobService.blobUploadSave(blobModel.newSave())
		})
	@Operation(summary = "유저 교육수료증 등록")
	@PostMapping(value = ["/file/training/{thisPK}"])
	fun postUserTrainingData(@RequestHeader token: String,
							 @PathVariable thisPK: String,
							 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) trainingDate: Date,
							 @RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(extraMyInfoService.addMyTrainingModel(token, thisPK, trainingDate, blobModel))
}