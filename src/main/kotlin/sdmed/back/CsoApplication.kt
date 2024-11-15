package sdmed.back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.ApplicationPidFileWriter
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
class CsoApplication {
	companion object {
		var ctx: ConfigurableApplicationContext? = null
	}
}

fun main(args: Array<String>) {
	val app = SpringApplicationBuilder()
	app.sources(CsoApplication::class.java)
		.listeners(ApplicationPidFileWriter("./sdmed.back.cso"))
		.build()

	CsoApplication.ctx = app.run(*args)
}
