package luizsignorelli.prometheusdemo.order

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import org.apache.commons.lang3.RandomUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.TimeUnit
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice




@RestController
@RequestMapping("/orders")
class OrdersApi @Autowired constructor(val processor: OrderProcessor) {
    data class OrderRequest(val customerId: String, val items: List<ItemRequest>)
    data class ItemRequest(val productId: String, val price: Double, val quantity: Int)
    data class OrderResponse(val id: String)

    val log = LoggerFactory.getLogger("OrdersApi")!!

    @PostMapping
    fun createOrder(@RequestBody request: OrderRequest): OrderResponse {
        val order = Order(
                customerId = request.customerId,
                items = request.items.map {
                    Item(
                            productId = it.productId,
                            quantity = it.quantity,
                            price = it.price
                    )
                }
        )
        OrderRepository.save(order)
        processor.process(order)
        log.info("Order {} sent to processor.", order.id)

        return OrderResponse(id = order.id.toString())
    }
}

@ControllerAdvice
class RestResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(value = RuntimeException::class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    protected fun handle(ex: RuntimeException): String {
        return "This should be application specific"
    }
}

object OrderRepository {

    private val ordersCounter = Counter.build()
            .name("orders_total")
            .labelNames("status")
            .help("Orders counter").register()

    private val ordersProcessingGauge = Gauge.build()
            .name("processing_orders")
            .labelNames("status")
            .help("Orders gauge").register()

    private val storage: MutableMap<UUID, Order> = mutableMapOf()

    fun save(order: Order): String {
        storage[order.id] = order
        ordersCounter.labels( order.status ).inc()
        return order.id.toString()
    }

    fun processing(id: UUID) {
        val status = "processing"
        storage[id] = storage[id]!!.copy(status = status)
        ordersProcessingGauge.labels(status).inc()
    }

    fun complete(id: UUID) {
        val status = "completed"
        val oldStatus = storage[id]?.status
        storage[id] = storage[id]!!.copy(status = status)
        ordersCounter.labels( status ).inc()
        ordersProcessingGauge.labels(oldStatus).dec()
        storage.remove(id)
    }

    fun fail(id: UUID) {
        val status = "failed"
        val oldStatus = storage[id]?.status
        storage[id] = storage[id]!!.copy(status = status)
        ordersCounter.labels( status ).inc()
        ordersProcessingGauge.labels(oldStatus).dec()
        storage.remove(id)
    }

    fun size(): Int = storage.size
    fun get(orderId: UUID): Order? = storage[orderId]
}

data class Order(
        val id: UUID = UUID.randomUUID(),
        val customerId: String,
        val items: List<Item>,
        val status: String = "created"
)

data class Item(
        val id: UUID = UUID.randomUUID(),
        val productId: String,
        val price: Double,
        val quantity: Int
)

@Component
class OrderProcessor {

    val log = LoggerFactory.getLogger("OrdersProcessor")!!

    @Async("orderProcessorExecutor")
    fun process(order: Order) {
        try {
            log.info("Started order {} processing.", order.id)
            OrderRepository.processing(order.id)
            doProcess(order)
            OrderRepository.complete(order.id)
            log.info("Finished order {} processing.", order.id)
        } catch (e: Exception) {
            OrderRepository.fail(order.id)
            throw e
        }
    }

    private fun doProcess(order: Order) {
        if (RandomUtils.nextLong(1, 11) > 2) {
            throw RuntimeException("order ${order.id} processing failed.")
        }
        TimeUnit.MILLISECONDS.sleep(RandomUtils.nextLong(100, 2000))
    }
}