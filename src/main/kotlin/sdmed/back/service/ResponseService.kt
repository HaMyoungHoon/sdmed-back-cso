package sdmed.back.service

import org.springframework.stereotype.Service
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.RestResult
import sdmed.back.model.common.RestResultT

@Service
class ResponseService {
	fun getFailResult(code: Int, msg: String): IRestResult = RestResult().apply {
		this.result = false
		this.code = code
		this.msg = msg
	}
	fun getSuccessResult(): IRestResult = RestResult().apply {
		this.result = true
	}
	fun <T> getResult(data: T): IRestResult = RestResultT<T>().apply {
		this.result = true
		this.data = data
	}
}