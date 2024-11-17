package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
data class UserPharmaceuticalHospital(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@ManyToOne
	@JoinColumn(name = "user_id")
	var userDataModel: UserDataModel? = null,
	@ManyToOne
	@JoinColumn(name = "pharmaceutical_id")
	var pharmaceuticalModel: PharmaceuticalModel? = null,
	@ManyToOne
	@JoinColumn(name = "hospital_id")
	var hospitalModel: HospitalModel? = null,
) {
}