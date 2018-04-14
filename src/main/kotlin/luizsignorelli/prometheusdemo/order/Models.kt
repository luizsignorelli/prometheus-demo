package luizsignorelli.prometheusdemo.order

import java.util.*

data class Order(
        val id: UUID = UUID.randomUUID(),
        val customerId: String,
        val items: List<Item>,
        val status: OrderStatus = OrderStatus.CREATED
)
enum class OrderStatus {
    CREATED,
    PROCESSING,
    COMPLETED,
    FAILED
}

data class Item(
        val id: UUID = UUID.randomUUID(),
        val productId: String,
        val price: Double,
        val quantity: Int
)