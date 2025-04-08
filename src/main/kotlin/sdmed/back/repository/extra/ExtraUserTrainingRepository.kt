package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import sdmed.back.model.sqlCSO.user.UserTrainingModel
import java.util.Date

interface ExtraUserTrainingRepository: JpaRepository<UserTrainingModel, String> {
    fun findAllByUserPKOrderByTrainingDateDesc(userPK: String): List<UserTrainingModel>
    fun findByUserPKAndTrainingDate(userPK: String, trainingDate: Date): UserTrainingModel?
}