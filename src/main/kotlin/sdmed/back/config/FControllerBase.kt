package sdmed.back.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService

@RestController
@CrossOrigin(origins = [FConstants.HTTP_SD_MED, FConstants.HTTPS_SD_MED], allowedHeaders = ["*"])
open class FControllerBase {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var azureBlobService: AzureBlobService
}