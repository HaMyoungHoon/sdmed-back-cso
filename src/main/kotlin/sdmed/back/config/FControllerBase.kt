package sdmed.back.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService

@RestController
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class FControllerBase {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var azureBlobService: AzureBlobService
}