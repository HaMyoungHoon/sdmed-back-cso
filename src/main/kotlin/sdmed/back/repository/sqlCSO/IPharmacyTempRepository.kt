package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.hospital.PharmacyTempModel

@Repository
interface IPharmacyTempRepository: JpaRepository<PharmacyTempModel, String> {
	fun findByThisPK(thisPK: String): PharmacyTempModel?
	fun findAllByCodeInOrderByOrgNameAsc(code: List<String>): List<PharmacyTempModel>

	@Query("SELECT a.* FROM PharmacyTempModel a " +
			"WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) " +
			"* cos(radians(a.longitude) - radians(:longitude)) " +
			"+ sin(radians(:latitude)) * sin(radians(a.latitude)))) <= (:distance / 1000) " +
			"ORDER BY a.orgName ASC ", nativeQuery = true)
	fun selectAllNearby(latitude: Double, longitude: Double, distance: Int): List<PharmacyTempModel>
}