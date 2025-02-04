package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.common.VersionCheckModel
import sdmed.back.model.sqlCSO.common.VersionCheckType

@Repository
interface IVersionCheckRepository: JpaRepository<VersionCheckModel, String> {
	fun findByThisPK(thisPK: String): VersionCheckModel?

	fun findByVersionCheckTypeAndAbleOrderByRegDateDesc(versionCheckType: VersionCheckType, able: Boolean = true): List<VersionCheckModel>
}