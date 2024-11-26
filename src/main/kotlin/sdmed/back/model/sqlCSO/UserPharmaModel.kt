package sdmed.back.model.sqlCSO

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
data class UserPharmaModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@ManyToOne
	@JoinColumn(name = "userDataModel_thisPK")
	var userDataModel: UserDataModel? = null,
	@ManyToOne
	@JoinColumn(name = "pharmaModel_thisPK")
	var pharmaModel: PharmaModel? = null
) {
}