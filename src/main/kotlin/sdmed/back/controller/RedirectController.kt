package sdmed.back.controller

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FConstants
import sdmed.back.model.common.IRestResult
import java.net.URI

@Tag(name = "RedirectController")
@RestController
@RequestMapping(value = ["/"])
@CrossOrigin(origins = [FConstants.HTTP_SD_MED, FConstants.HTTPS_SD_MED], allowedHeaders = ["*"])
class RedirectController {
	@Hidden
	@GetMapping
	fun redirectEmptyPath(): ResponseEntity<*> {
		return ResponseEntity<IRestResult>(HttpHeaders().apply {
			location = URI.create("/swagger-ui/index.html")
		}, HttpStatus.MOVED_PERMANENTLY)
	}
}