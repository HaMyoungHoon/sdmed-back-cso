package sdmed.back.config

import java.util.*

object ContentsType {
	const val type_aac   : String = "audio/aac"
	const val type_abw   : String = "application/x-abiword"
	const val type_arc   : String = "application/octet-stream"
	const val type_avi   : String = "video/x-msvideo"
	const val type_azw   : String = "application/vnd.amazon.ebook"
	const val type_bin   : String = "application/octet-stream"
	const val type_bz    : String = "application/x-bzip"
	const val type_bz2   : String = "application/x-bzip2"
	const val type_csh   : String = "application/x-csh"
	const val type_css   : String = "text/css"
	const val type_csv   : String = "text/csv"
	const val type_doc   : String = "application/msword"
	const val type_epub  : String = "application/epub+zip"
	const val type_gif   : String = "image/gif"
	const val type_htm   : String = "text/html"
	const val type_html  : String = "text/html"
	const val type_heic  : String = "image/heic"
	const val type_heif  : String = "image/heif"
	const val type_ico   : String = "image/x-icon"
	const val type_ics   : String = "text/calendar"
	const val type_jar   : String = "application/java-archive"
	const val type_jpeg  : String = "image/jpeg"
	const val type_jpg   : String = "image/jpeg"
	const val type_js    : String = "application/js"
	const val type_json  : String = "application/json"
	const val type_mid   : String = "audio/midi"
	const val type_midi  : String = "audio/midi"
	const val type_mpeg  : String = "video/mpeg"
	const val type_mpkg  : String = "application/vnd.apple.installer+xml"
	const val type_odp   : String = "application/vnd.oasis.opendocument.presentation"
	const val type_ods   : String = "application/vnd.oasis.opendocument.spreadsheet"
	const val type_odt   : String = "application/vnd.oasis.opendocument.text"
	const val type_oga   : String = "audio/ogg"
	const val type_ogv   : String = "video/ogg"
	const val type_ogx   : String = "application/ogg"
	const val type_png   : String = "image/png"
	const val type_pdf   : String = "application/pdf"
	const val type_ppt   : String = "application/vnd.ms-powerpoint"
	const val type_rar   : String = "application/x-rar-compressed"
	const val type_rtf   : String = "application/rtf"
	const val type_sh    : String = "application/x-sh"
	const val type_svg   : String = "image/svg+xml"
	const val type_swf   : String = "application/x-shockwave-flash"
	const val type_tar   : String = "application/x-tar"
	const val type_tif   : String = "image/tiff"
	const val type_tiff  : String = "image/tiff"
	const val type_ttf   : String = "application/x-font-ttf"
	const val type_txt   : String = "plain/text"
	const val type_vsd   : String = "application/vnd.visio"
	const val type_wav   : String = "audio/x-wav"
	const val type_weba  : String = "audio/webm"
	const val type_webm  : String = "video/webm"
	const val type_webp  : String = "image/webp"
	const val type_woff  : String = "application/x-font-woff"
	const val type_xhtml : String = "application/xhtml+xml"
	const val type_xls   : String = "application/vnd.ms-excel"
	const val type_xlsx  : String = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
	const val type_xlsm  : String = "application/vnd.ms-excel.sheet.macroEnabled.12"
	const val type_xml   : String = "application/xml"
	const val type_xul   : String = "application/vnd.mozilla.xul+xml"
	const val type_zip   : String = "application/zip"
	const val type_3gp   : String = "video/3gpp"
	const val type_3g2   : String = "video/3gpp2"
	const val type_7z    : String = "application/x-7z-compressed"

	fun findContentType(fileName: String) =
		when (fileName.substring(fileName.indexOfLast { it == '.' } + 1).lowercase(Locale.getDefault())) {
			"aac" ->    ContentsType.type_aac
			"abw" ->    ContentsType.type_abw
			"arc" ->    ContentsType.type_arc
			"avi" ->    ContentsType.type_avi
			"azw" ->    ContentsType.type_azw
			"bin" ->    ContentsType.type_bin
			"bz" ->     ContentsType.type_bz
			"bz2" ->    ContentsType.type_bz2
			"csh" ->    ContentsType.type_csh
			"css" ->    ContentsType.type_css
			"csv" ->    ContentsType.type_csv
			"doc" ->    ContentsType.type_doc
			"epub" ->   ContentsType.type_epub
			"gif" ->    ContentsType.type_gif
			"heic" ->   ContentsType.type_heic
			"heif" ->   ContentsType.type_heif
			"htm" ->    ContentsType.type_htm
			"html" ->   ContentsType.type_html
			"ico" ->    ContentsType.type_ico
			"ics" ->    ContentsType.type_ics
			"jar" ->    ContentsType.type_jar
			"jpeg" ->   ContentsType.type_jpeg
			"jpg" ->    ContentsType.type_jpg
			"js" ->     ContentsType.type_js
			"json" ->   ContentsType.type_json
			"mid" ->    ContentsType.type_mid
			"midi" ->   ContentsType.type_midi
			"mpeg" ->   ContentsType.type_mpeg
			"mpkg" ->   ContentsType.type_mpkg
			"odp" ->    ContentsType.type_odp
			"ods" ->    ContentsType.type_ods
			"odt" ->    ContentsType.type_odt
			"oga" ->    ContentsType.type_oga
			"ogv" ->    ContentsType.type_ogv
			"ogx" ->    ContentsType.type_ogx
			"png" ->    ContentsType.type_png
			"pdf" ->    ContentsType.type_pdf
			"ppt" ->    ContentsType.type_ppt
			"rar" ->    ContentsType.type_rar
			"rtf" ->    ContentsType.type_rtf
			"sh" ->     ContentsType.type_sh
			"svg" ->    ContentsType.type_svg
			"swf" ->    ContentsType.type_swf
			"tar" ->    ContentsType.type_tar
			"tif" ->    ContentsType.type_tif
			"tiff" ->   ContentsType.type_tiff
			"ttf" ->    ContentsType.type_ttf
			"txt" ->    ContentsType.type_txt
			"vsd" ->    ContentsType.type_vsd
			"wav" ->    ContentsType.type_wav
			"weba" ->   ContentsType.type_weba
			"webm" ->   ContentsType.type_webm
			"webp" ->   ContentsType.type_webp
			"woff" ->   ContentsType.type_woff
			"xhtml" ->  ContentsType.type_xhtml
			"xls" ->    ContentsType.type_xls
			"xlsx" ->   ContentsType.type_xlsx
			"xlsm" ->   ContentsType.type_xlsm
			"xml" ->    ContentsType.type_xml
			"xul" ->    ContentsType.type_xul
			"zip" ->    ContentsType.type_zip
			"3gp" ->    ContentsType.type_3gp
			"3g2" ->    ContentsType.type_3g2
			"7z" ->     ContentsType.type_7z
			else ->     "application/octet-stream"
		}
}