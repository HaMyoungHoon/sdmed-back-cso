package sdmed.back.advice.exception

class AccessDeniedException: RuntimeException {
	constructor(msg: String, t: Throwable): super(msg, t)
	constructor(msg: String): super(msg)
	constructor(): super()
}