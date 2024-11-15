package sdmed.back.model.common

class RestResult: IRestResult {
	override var result: Boolean? = null
	override var code: Int? = null
	override var msg: String? = null
}