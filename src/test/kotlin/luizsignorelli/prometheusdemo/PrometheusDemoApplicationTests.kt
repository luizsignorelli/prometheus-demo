package luizsignorelli.prometheusdemo

import com.fasterxml.jackson.databind.ObjectMapper
import luizsignorelli.prometheusdemo.order.OrderRepository
import org.hamcrest.Matchers.*
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PrometheusDemoApplicationTests {


    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
        val body = """
            {
                "customerId": "customer-id",
                "items": [
                  {
                    "productId": "product-1",
                    "price": 10.00,
                    "quantity": 1
                  },
                  {
                    "productId": "product-2",
                    "price": 14.99,
                    "quantity": 5
                  }
                ]
            }
            """

        val orderResponse = mockMvc.perform(
                post("/orders")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk)
                .andReturn().response.contentAsString
        val orderId = ObjectMapper().readTree(orderResponse)["id"].asText()

        Assert.assertEquals(1, OrderRepository.size())
        val order = OrderRepository.get(UUID.fromString(orderId))
        Assert.assertNotNull(order)

        TimeUnit.SECONDS.sleep(3)
        mockMvc.perform(get("/prometheus"))
                .andDo(print())
                .andExpect(status().isOk)
                .andExpect(content().string(containsString("counter_status_200_orders")))
                .andExpect(content().string(containsString("counter_order{status=\"created\"")))
                .andExpect(content().string(containsString("counter_order{status=\"completed\"")))
    }

}
