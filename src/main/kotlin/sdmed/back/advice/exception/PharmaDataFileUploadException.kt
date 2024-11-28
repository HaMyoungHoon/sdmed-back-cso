package sdmed.back.advice.exception

class PharmaDataFileUploadException: RuntimeException {
	constructor(msg: String, t: Throwable): super(msg, t)
	constructor(msg: String): super(msg)
	constructor(): super()
}