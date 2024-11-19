package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.PharmaceuticalModel

@Repository
interface IPharmaceuticalRepository: JpaRepository<PharmaceuticalModel, Long> {
	fun findAllByOrderByCode(): List<PharmaceuticalModel>
	fun findAllByOrderByCode(pageable: Pageable): Page<PharmaceuticalModel>

	fun findAllByCodeIn(codes: List<Int>): List<PharmaceuticalModel>
}