package sdmed.back.advice.exception

class CurrentPWNotMatchException: RuntimeException {
	constructor(msg: String, t: Throwable): super(msg, t)
	constructor(msg: String): super(msg)
	constructor(): super()
}