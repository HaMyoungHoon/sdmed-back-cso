package sdmed.back.model.sqlCSO

import jakarta.persistence.*
import java.util.*

@Entity
data class HosMedicineModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@ManyToOne
	@JoinColumn(name = "hospitalModel_thisPK")
	var hospitalModel: HospitalModel? = null,
	@ManyToOne
	@JoinColumn(name = "medicineModel_thisPK")
	var medicineModel: MedicineModel? = null,
) {
}