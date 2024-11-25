package sdmed.back.model.sqlCSO

import jakarta.persistence.*
import java.util.*

@Entity
data class UserPharmaHosMedicine(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@ManyToOne
	@JoinColumn(name = "user_id")
	var userDataModel: UserDataModel? = null,
	@ManyToOne
	@JoinColumn(name = "pharma_id")
	var pharmaModel: PharmaModel? = null,
	@ManyToOne
	@JoinColumn(name = "hospital_id")
	var hospitalModel: HospitalModel? = null,
	@ManyToOne
	@JoinColumn(name = "medicine_id")
	var medicineModel: MedicineModel? = null,
) {
}