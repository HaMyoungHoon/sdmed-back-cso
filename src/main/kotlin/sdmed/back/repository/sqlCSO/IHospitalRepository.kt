package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.HospitalModel

@Repository
interface IHospitalRepository: JpaRepository<HospitalModel, Long> {
	fun findAllByCodeIn(codes: List<Int>): List<HospitalModel>
}