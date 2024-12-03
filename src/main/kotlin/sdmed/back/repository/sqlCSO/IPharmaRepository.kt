package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.PharmaModel

@Repository
interface IPharmaRepository: JpaRepository<PharmaModel, String> {
	fun findAllByOrderByCode(): List<PharmaModel>
	fun findAllByOrderByCode(pageable: Pageable): Page<PharmaModel>
	fun findByCode(code: Int): PharmaModel?
	fun findAllByInnerNameContainingOrOrgNameContainingOrderByCode(innerName: String, orgName: String): List<PharmaModel>

	fun findByThisPK(pharmaPK: String): PharmaModel?
	fun findAllByThisPKIn(pharmaPK: List<String>): List<PharmaModel>

	fun findAllByCodeIn(codes: List<Int>): List<PharmaModel>

	@Query("SELECT * FROM pharmaModel " +
			"WHERE thisPK = :pharmaPK", nativeQuery = true)
	fun selectByThisPK(pharmaPK: String): PharmaModel?


	@Query("SELECT * FROM hospitalModel " +
		"WHERE code LIKE %:code%", nativeQuery = true)
	fun selectAllByCodeContainingOrderByCode(code: String): List<PharmaModel>
}