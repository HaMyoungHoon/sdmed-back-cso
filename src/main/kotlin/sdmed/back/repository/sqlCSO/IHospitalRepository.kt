package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.hospital.HospitalModel

@Repository
interface IHospitalRepository: JpaRepository<HospitalModel, String> {
	fun findByThisPK(thisPK: String): HospitalModel?
	fun findByCode(code: String): HospitalModel?
	fun findAllByThisPKIn(thisPK: List<String>): List<HospitalModel>
	fun findAllByCodeIn(codes: List<String>): List<HospitalModel>

	@Query("SELECT * FROM HospitalModel " +
			"WHERE inVisible = :inVisible AND code LIKE %:code% " +
			"ORDER BY code ASC", nativeQuery = true)
	fun selectAllByCodeContainingOrderByCode(code: String, inVisible: Boolean = false): List<HospitalModel>

	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.inVisible = :inVisible AND (a.innerName LIKE %:innerName% OR a.orgName LIKE %:orgName%) " +
			"ORDER BY a.code ASC")
	fun selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(innerName: String, orgName: String, inVisible: Boolean = false): List<HospitalModel>

	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInVisibleOrderByCode(inVisible: Boolean = false): List<HospitalModel>

	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.code = :code AND a.orgName = :orgName")
	fun selectByNewHospital(code: String = FConstants.NEW_HOSPITAL_CODE, orgName: String = FConstants.NEW_HOSPITAL_NAME): HospitalModel?
	@Query("SELECT a FROM HospitalModel a " +
			"WHERE a.code = :code AND a.orgName = :orgName")
	fun selectByTransferHospital(code: String = FConstants.TRANSFER_HOSPITAL_CODE, orgName: String = FConstants.TRANSFER_HOSPITAL_NAME): HospitalModel?
}