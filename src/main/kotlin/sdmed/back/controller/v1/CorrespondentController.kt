package sdmed.back.controller.v1

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sdmed.back.config.FConstants
import sdmed.back.service.CorrespondentService
import sdmed.back.service.ResponseService

@Tag(name = "거래처 컨트롤러")
@RestController
@RequestMapping(value = ["/v1/correspondent"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class CorrespondentController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var correspondentService: CorrespondentService


}