package sdmed.back.model.sqlCSO

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.util.*

@Entity
data class PharmaHosModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@ManyToOne
	@JoinColumn(name = "pharmaModel_thisPK")
	var pharmaModel: PharmaModel? = null,
	@ManyToOne
	@JoinColumn(name = "hospitalModel_thisPK")
	var hospitalModel: HospitalModel? = null
) {
}