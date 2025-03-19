package sdmed.back.advice.exception

class UserTrainingFileUploadException: RuntimeException {
    constructor(msg: String, t: Throwable): super(msg, t)
    constructor(msg: String): super(msg)
    constructor(): super()
}