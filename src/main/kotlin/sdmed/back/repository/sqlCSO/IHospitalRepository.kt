package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.HospitalModel

@Repository
interface IHospitalRepository: JpaRepository<HospitalModel, String> {
	fun findAllByOrderByCode(): List<HospitalModel>
	fun findAllByOrderByCode(pageable: Pageable): Page<HospitalModel>
	fun findAllByInnerNameContainingOrOrgNameContainingOrderByCode(innerName: String, orgName: String): List<HospitalModel>
	fun findAllByCodeIn(codes: List<Int>): List<HospitalModel>

	fun findAllByThisPKIn(hosPK: List<String>): List<HospitalModel>

	@Query("SELECT * FROM hospitalModel " +
		"WHERE code LIKE %:code%", nativeQuery = true)
	fun selectAllByCodeContainingOrderByCode(code: String): List<HospitalModel>
}