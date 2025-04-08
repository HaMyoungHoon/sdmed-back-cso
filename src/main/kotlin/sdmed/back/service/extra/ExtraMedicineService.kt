package sdmed.back.service.extra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.repository.extra.ExtraMedicineIngredientRepository
import sdmed.back.repository.extra.ExtraMedicinePriceRepository
import sdmed.back.repository.extra.ExtraMedicineRepository

open class ExtraMedicineService: FServiceBase() {
    @Autowired lateinit var extraMedicineRepository: ExtraMedicineRepository
    @Autowired lateinit var extraMedicineIngredientRepository: ExtraMedicineIngredientRepository
    @Autowired lateinit var extraMedicinePriceRepository: ExtraMedicinePriceRepository

    protected fun getAllMedicine(token: String): List<ExtraMedicinePriceResponse> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val ret = extraMedicineRepository.selectAllByInvisibleOpenOrderByCode()
        val ingredient = extraMedicineIngredientRepository.findAllByOrderByMainIngredientCode()
        medicineMerge(ret, ingredient)
        val recentPrice = extraMedicinePriceRepository.selectAllByRecentData()
        medicinePriceMerge(ret, recentPrice)
        return ret
    }
    protected fun getLikeMedicine(token: String, searchString: String): List<ExtraMedicinePriceResponse> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val ret = extraMedicineRepository.selectAllByInvisibleOpenLikeOrderByCode(searchString)
        val ingredient = extraMedicineIngredientRepository.findAllByMainIngredientCodeIn(ret.map { it.mainIngredientCode })
        medicineMerge(ret, ingredient)
        val recentPrice = extraMedicinePriceRepository.selectAllByRecentData(ret.map { it.kdCode })
        medicinePriceMerge(ret, recentPrice)
        return ret
    }
    protected fun getPagingAllMedicine(token: String, page: Int, size: Int): Page<ExtraMedicinePriceResponse> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val pageable = PageRequest.of(page, size)
        val ret = extraMedicineRepository.selectPagingByInvisibleOpenOrderByCode(pageable)
        val ingredient = extraMedicineIngredientRepository.findAllByMainIngredientCodeIn(ret.content.map { it.mainIngredientCode })
        medicineMerge(ret.content, ingredient)
        val recentPrice = extraMedicinePriceRepository.selectAllByRecentData(ret.content.map { it.kdCode })
        medicinePriceMerge(ret.content, recentPrice)
        return ret
    }
    protected fun getPagingLikeMedicine(token: String, searchString: String, page: Int, size: Int): Page<ExtraMedicinePriceResponse> {
        if (searchString.isBlank()) {
            return Page.empty()
        }
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val pageable = PageRequest.of(page, size)
        val ret = extraMedicineRepository.selectPagingByInvisibleOpenLikeOrderByCode(searchString, pageable)
        val ingredient = extraMedicineIngredientRepository.findAllByMainIngredientCodeIn(ret.content.map { it.mainIngredientCode })
        medicineMerge(ret.content, ingredient)
        val recentPrice = extraMedicinePriceRepository.selectAllByRecentData(ret.content.map { it.kdCode })
        medicinePriceMerge(ret.content, recentPrice)
        return ret
    }
    protected fun medicinePriceMerge(mother: List<ExtraMedicinePriceResponse>, price: List<MedicinePriceModel>) {
        val priceMap = price.groupBy { it.kdCode }
        mother.map { x ->
            priceMap[x.kdCode]?.let { y -> x.maxPrice = y.maxByOrNull { z -> z.applyDate }?.maxPrice ?: x.customPrice }
        }
    }
    protected fun medicineMerge(mother: List<ExtraMedicinePriceResponse>, ingredient: List<MedicineIngredientModel>) {
        val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
        mother.map { x ->
            ingredientMap[x.mainIngredientCode]?.let { y ->
                x.mainIngredientCode = y.mainIngredientCode
                x.mainIngredientName = y.mainIngredientName
            }
        }
    }
}