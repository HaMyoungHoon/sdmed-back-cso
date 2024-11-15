package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sdmed.back.repository.sqlCSO.ICorrespondentRepository
import sdmed.back.repository.sqlCSO.ICorrespondentSubRepository

@Service
class CorrespondentService {
	@Autowired lateinit var correspondentRepository: ICorrespondentRepository
	@Autowired lateinit var correspondentSubRepository: ICorrespondentSubRepository
	@Autowired lateinit var userService: UserService
}