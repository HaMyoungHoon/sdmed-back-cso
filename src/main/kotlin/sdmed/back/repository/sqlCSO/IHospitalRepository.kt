package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.HospitalModel

@Repository
interface IHospitalRepository: JpaRepository<HospitalModel, String> {
	fun findByThisPK(thisPK: String): HospitalModel?
	fun findByCode(code: Int): HospitalModel?
	fun findAllByThisPKIn(thisPK: List<String>): List<HospitalModel>
	fun findAllByCodeIn(codes: List<Int>): List<HospitalModel>

	@Query("SELECT * FROM HospitalModel " +
			"WHERE inVisible = :inVisible AND code LIKE %:code% " +
			"ORDER BY code ASC", nativeQuery = true)
	fun selectAllByCodeContainingOrderByCode(code: String, inVisible: Boolean = false): List<HospitalModel>

	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.inVisible = :inVisible AND (a.innerName LIKE %:innerName% OR a.orgName LIKE %:orgName%) " +
			"ORDER BY a.code ASC")
	fun selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(innerName: String, orgName: String, inVisible: Boolean = false): List<HospitalModel>

	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInVisibleOrderByCode(inVisible: Boolean = false): List<HospitalModel>
}