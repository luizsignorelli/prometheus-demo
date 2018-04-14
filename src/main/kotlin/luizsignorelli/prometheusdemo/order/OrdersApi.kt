package luizsignorelli.prometheusdemo.order

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*
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

    private val storage: MutableMap<UUID, Order> = mutableMapOf()

    fun save(order: Order): String {
        storage[order.id] = order
        OrderMetrics.orderCreated()
        return order.id.toString()
    }

    fun processing(id: UUID) {
        storage[id] = storage[id]!!.copy(status = OrderStatus.PROCESSING)
        OrderMetrics.orderProcessingStarted()
    }

    fun complete(id: UUID) {
        storage[id] = storage[id]!!.copy(status = OrderStatus.COMPLETED)
        OrderMetrics.orderCompleted()
        storage.remove(id)
    }

    fun fail(id: UUID) {
        storage[id] = storage[id]!!.copy(status = OrderStatus.FAILED)
        OrderMetrics.orderFailed()
        storage.remove(id)
    }

    fun size(): Int = storage.size
    fun get(orderId: UUID): Order? = storage[orderId]

}