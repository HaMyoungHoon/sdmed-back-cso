package sdmed.back.repository.intra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIApplyDateModel

@Repository
interface IntraEDIApplyDateRepository: JpaRepository<EDIApplyDateModel, String> {
}