package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserRelationModel

@Repository
interface IUserRelationRepository: JpaRepository<UserRelationModel, String> {
	fun findAllByUserPK(userPK: String): List<UserRelationModel>
	fun findAllByHosPK(hosPK: String): List<UserRelationModel>
	fun findAllByHosPKIn(hosPK: List<String>): List<UserRelationModel>
	fun findAllByPharmaPK(pharmaPK: String): List<UserRelationModel>
	fun findAllByPharmaPKIn(pharmaPK: List<String>): List<UserRelationModel>
	fun findAllByMedicinePK(medicinePK: String): List<UserRelationModel>
	fun findAllByMedicinePKIn(medicinePK: List<String>): List<UserRelationModel>
}