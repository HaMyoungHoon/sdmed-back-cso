package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.advice.exception.*
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.*

open class PharmaService: FServiceBase() {
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository

	fun getPharmaAllSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<PharmaModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		val ret: List<PharmaModel> = if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				pharmaRepository.selectAllByCodeContainingOrderByCode(x.toString())
			} ?: pharmaRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		} else {
			pharmaRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		}

		return ret
	}
	fun getPharmaData(token: String, pharmaPK: String, pharmaOwnMedicineView: Boolean = false): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		if (pharmaOwnMedicineView) {
			ret.medicineList = medicineRepository.findAllByClientCodeOrderByOrgNameAsc(ret.code).toMutableList()
		}
		return ret
	}
}