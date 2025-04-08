package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.extra.ExtraMyInfoHospital
import sdmed.back.model.sqlCSO.hospital.HospitalModel

@Repository
interface ExtraHospitalRepository: JpaRepository<HospitalModel, String> {
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMyInfoHospital(a.thisPK, a.orgName, a.address) " +
            "FROM HospitalModel a " +
            "WHERE a.thisPK IN (:thisPKs) " +
            "ORDER BY a.orgName")
    fun selectAllMyHospital(thisPKs: List<String>): List<ExtraMyInfoHospital>
}