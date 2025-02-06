package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.common.QuarantineAuthNumberModel

@Repository
interface IQuarantineAuthNumberRepository: JpaRepository<QuarantineAuthNumberModel, String> {
	fun findByUserPK(userPK: String): QuarantineAuthNumberModel?
}