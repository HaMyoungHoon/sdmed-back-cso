package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import sdmed.back.config.FExtensions
import java.util.*

@Entity
data class MedicinePriceModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var kdCode: String = "",
	@Column
	var maxPrice: Int = 0,
	@Column
	var applyDate: Date = Date(),
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JsonBackReference(value = "medicinePriceManagedReference")
	@JoinColumn
	var medicineModel: MedicineModel? = null
) {

	fun insertString(): String {
		return "('$thisPK', '$kdCode', '$maxPrice', '${FExtensions.parseDateTimeString(applyDate, "yyyy-MM-dd")}', '${medicineModel?.thisPK}', '${medicineModel?.thisPK}')"
	}
}