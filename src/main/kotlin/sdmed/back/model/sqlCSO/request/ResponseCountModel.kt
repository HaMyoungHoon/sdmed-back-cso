package sdmed.back.model.sqlCSO.request

import sdmed.back.model.common.ResponseType

interface ResponseCountModel {
	var count: Long
	var responseType: ResponseType
}