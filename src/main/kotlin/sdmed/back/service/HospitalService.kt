package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.repository.sqlCSO.IHospitalRepository

class HospitalService: FServiceBase() {
	@Autowired lateinit var hospitalRepository: IHospitalRepository

	fun getHospitalAllSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<HospitalModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				return hospitalRepository.selectAllByCodeContainingOrderByCode(x.toString())
			} ?: return hospitalRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		}

		return hospitalRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
	}
}