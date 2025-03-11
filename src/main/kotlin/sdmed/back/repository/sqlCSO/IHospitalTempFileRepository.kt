package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.hospital.HospitalTempFileModel

@Repository
interface IHospitalTempFileRepository: JpaRepository<HospitalTempFileModel, String> {
	fun findAllByHospitalTempPK(hospitalTempPK: String): List<HospitalTempFileModel>
}