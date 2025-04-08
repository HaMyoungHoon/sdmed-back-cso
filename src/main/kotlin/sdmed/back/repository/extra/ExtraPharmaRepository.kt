package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.extra.ExtraMyInfoPharma
import sdmed.back.model.sqlCSO.pharma.PharmaModel

@Repository
interface ExtraPharmaRepository: JpaRepository<PharmaModel, String> {

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMyInfoPharma(a.thisPK, a.orgName, a.address) " +
            "FROM PharmaModel a " +
            "WHERE a.thisPK IN (:thisPKs) " +
            "ORDER BY a.orgName ")
    fun selectAllMyPharma(thisPKs: List<String>): List<ExtraMyInfoPharma>
}