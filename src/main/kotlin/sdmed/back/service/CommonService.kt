package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sdmed.back.advice.exception.VersionCheckNotExistException
import sdmed.back.model.sqlCSO.common.VersionCheckModel
import sdmed.back.model.sqlCSO.common.VersionCheckType
import sdmed.back.repository.sqlCSO.IVersionCheckRepository

@Service
class CommonService {
	@Autowired lateinit var versionCheckRepository: IVersionCheckRepository

	fun getAbleVersion(versionCheckType: VersionCheckType): List<VersionCheckModel> {
		val ret = versionCheckRepository.findByVersionCheckTypeAndAbleOrderByRegDateDesc(versionCheckType)
		if (ret.isEmpty()) {
			throw VersionCheckNotExistException()
		}
		return ret
	}
}