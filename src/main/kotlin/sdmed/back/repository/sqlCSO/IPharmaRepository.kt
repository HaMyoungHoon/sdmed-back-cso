package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.PharmaModel

@Repository
interface IPharmaRepository: JpaRepository<PharmaModel, Long> {
	fun findAllByOrderByCode(): List<PharmaModel>
	fun findAllByOrderByCode(pageable: Pageable): Page<PharmaModel>

	fun findAllByCodeIn(codes: List<Int>): List<PharmaModel>
}