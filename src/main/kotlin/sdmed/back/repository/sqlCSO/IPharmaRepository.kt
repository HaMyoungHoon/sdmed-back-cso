package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.pharma.PharmaModel

@Repository
interface IPharmaRepository: JpaRepository<PharmaModel, String> {
	fun findByThisPK(thisPK: String): PharmaModel?
	fun findByCode(code: String): PharmaModel?
	fun findAllByThisPKIn(thisPK: List<String>): List<PharmaModel>
	fun findAllByCodeIn(codes: List<String>): List<PharmaModel>
	fun findAllByOrgNameIn(orgNames: List<String>): List<PharmaModel>
	fun findAllByInnerNameIn(innerNames: List<String>): List<PharmaModel>

	@Query("SELECT * FROM PharmaModel " +
			"WHERE inVisible = :inVisible AND code LIKE %:code% " +
			"ORDER BY code ASC", nativeQuery = true)
	fun selectAllByCodeContainingOrderByCode(code: String, inVisible: Boolean = false): List<PharmaModel>

	@Query("SELECT a FROM PharmaModel a " +
			"WHERE a.inVisible = :inVisible AND (a.innerName LIKE %:innerName% OR a.orgName LIKE %:orgName%) " +
			"ORDER BY a.code ASC")
	fun selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(innerName: String, orgName: String, inVisible: Boolean = false): List<PharmaModel>

	@Query("SELECT a FROM PharmaModel a " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOrderByCode(inVisible: Boolean = false): List<PharmaModel>
}