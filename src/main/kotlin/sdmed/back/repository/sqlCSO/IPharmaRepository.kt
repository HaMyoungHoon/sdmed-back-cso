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

	fun findByThisPK(pharmaPK: String): PharmaModel?

	fun findAllByCodeIn(codes: List<Int>): List<PharmaModel>

	@Query("SELECT * FROM pharmaModel " +
			"WHERE thisPK = :pharmaPK", nativeQuery = true)
	fun selectByThisPK(pharmaPK: String): PharmaModel?
}