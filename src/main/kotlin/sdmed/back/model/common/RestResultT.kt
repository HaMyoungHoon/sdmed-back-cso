package sdmed.back.model.common

class RestResultT<T>: IRestResult {
	override var result: Boolean? = null
	override var code: Int? = null
	override var msg: String? = null
	var data: T? = null
}