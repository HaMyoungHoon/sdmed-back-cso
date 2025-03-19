package sdmed.back.model.sqlCSO.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import sdmed.back.model.sqlCSO.BlobUploadModel
import java.util.Date
import java.util.UUID

@Entity
data class UserTrainingModel(
    @Id
    @Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
    var thisPK: String = UUID.randomUUID().toString(),
    @Column(columnDefinition = "nvarchar(36)", updatable = false)
    var userPK: String = "",
    @Column(columnDefinition = "text")
    var blobUrl: String = "",
    @Column(columnDefinition = "text")
    var originalFilename: String = "",
    @Column(columnDefinition = "nvarchar(100)")
    var mimeType: String = "",
    @Column
    var trainingDate: Date = Date(),
    @Column
    var regDate: Date = Date()
) {
    fun safeCopy(rhs: BlobUploadModel): UserTrainingModel {
        this.blobUrl = rhs.blobUrl
        this.originalFilename = rhs.originalFilename
        this.mimeType = rhs.mimeType
        return this
    }
}