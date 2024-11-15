package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sdmed.back.config.FAmhohwa
import sdmed.back.config.security.JwtTokenProvider

@Service
class CorrespondentService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var fAmhohwa: FAmhohwa

}