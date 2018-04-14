package luizsignorelli.prometheusdemo.order

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram

object OrderMetrics {
    private val ordersCounter = Counter.build()
            .name("orders_total")
            .labelNames("status")
            .help("Orders counter").register()

    private val ordersProcessingGauge = Gauge.build()
            .name("processing_orders")
            .help("Number of orders being processed").register()

    private val orderProcessingDuration = Histogram.build()
            .name("orders_processing_duration_seconds")
            .buckets(0.2, 0.5, 0.8, 1.0, 1.5, 2.0, 3.0 )
            .help("Order processing duration in seconds.").register()

    fun orderCreated() {
        ordersCounter.labels( OrderStatus.CREATED.name.toLowerCase() ).inc()
    }

    fun orderProcessingStarted() {
        ordersProcessingGauge.inc()
    }

    fun orderCompleted() {
        ordersCounter.labels( OrderStatus.COMPLETED.name.toLowerCase() ).inc()
        ordersProcessingGauge.dec()
    }

    fun orderFailed() {
        ordersCounter.labels( OrderStatus.FAILED.name.toLowerCase() ).inc()
        ordersProcessingGauge.dec()
    }

    fun processingTime(processingLogic: Runnable) {
        orderProcessingDuration.time(processingLogic)
    }
}