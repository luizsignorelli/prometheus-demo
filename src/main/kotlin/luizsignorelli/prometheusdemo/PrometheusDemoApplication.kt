package luizsignorelli.prometheusdemo

import io.prometheus.client.spring.boot.EnablePrometheusEndpoint
import io.prometheus.client.spring.boot.EnableSpringBootMetricsCollector
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor


@SpringBootApplication
@ComponentScan(basePackages = ["luizsignorelli.prometheusdemo"])
@EnablePrometheusEndpoint
@EnableSpringBootMetricsCollector
@EnableAsync
class PrometheusDemoApplication {

    @Bean(name = ["orderProcessorExecutor"])
    fun threadPoolTaskExecutor(): Executor {
        val threadPoolTaskExecutor = ThreadPoolTaskExecutor()
        threadPoolTaskExecutor.corePoolSize = 1000
        return threadPoolTaskExecutor
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(PrometheusDemoApplication::class.java, *args)
}
