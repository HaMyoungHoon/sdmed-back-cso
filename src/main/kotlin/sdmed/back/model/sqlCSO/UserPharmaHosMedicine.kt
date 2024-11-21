package sdmed.back.model.sqlCSO

import jakarta.persistence.*

@Entity
data class UserPharmaHosMedicine(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
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