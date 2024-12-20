package sdmed.back.advice.exception

class EDIApplyDateNotExistException: RuntimeException {
	constructor(msg: String, t: Throwable): super(msg, t)
	constructor(msg: String): super(msg)
	constructor(): super()
}