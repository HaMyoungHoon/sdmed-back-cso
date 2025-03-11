package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel

@Repository
interface IHospitalTempRepository: JpaRepository<HospitalTempModel, String> {
	fun findByThisPK(thisPK: String): HospitalTempModel?
	fun findAllByCodeInOrderByOrgNameAsc(code: List<String>): List<HospitalTempModel>
	fun findAllByOrderByOrgNameAsc(pageable: Pageable): Page<HospitalTempModel>

	@Query("SELECT a FROM HospitalTempModel a " +
			"WHERE a.orgName LIKE %:name% OR a.address LIKE %:address% " +
			"ORDER BY a.orgName ASC ")
	fun selectAllContains(name: String, address: String): List<HospitalTempModel>
	@Query("SELECT a.* FROM HospitalTempModel a " +
			"WHERE (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) " +
			"* cos(radians(a.longitude) - radians(:longitude)) " +
			"+ sin(radians(:latitude)) * sin(radians(a.latitude)))) <= :distance " +
			"ORDER BY a.orgName ASC ", nativeQuery = true)
	fun selectAllNearby(latitude: Double, longitude: Double, distance: Int): List<HospitalTempModel>
}