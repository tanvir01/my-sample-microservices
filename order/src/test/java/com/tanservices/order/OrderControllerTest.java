package com.tanservices.order;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Optional;

@SpringBootTest(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=sa"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        // insert three test orders into the database
        Order order1 = Order.builder()
                .customerName("John Doe")
                .customerEmail("john.doe@example.com")
                .shippingAddress("123 Main St, Anytown USA")
                .totalAmount(100.0)
                .status(Order.OrderStatus.PENDING)
                .build();
        orderRepository.save(order1);

        Order order2 = Order.builder()
                .customerName("Jane Smith")
                .customerEmail("jane.smith@example.com")
                .shippingAddress("456 High St, Anytown USA")
                .totalAmount(200.0)
                .status(Order.OrderStatus.PROCESSING)
                .build();
        orderRepository.save(order2);

        Order order3 = Order.builder()
                .customerName("Bob Johnson")
                .customerEmail("bob.johnson@example.com")
                .shippingAddress("789 Elm St, Anytown USA")
                .totalAmount(300.0)
                .status(Order.OrderStatus.COMPLETED)
                .build();
        orderRepository.save(order3);
    }

    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }

    @Test
    public void testGetAllOrders() throws Exception {


        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$", hasSize(3)))
                .andExpect((ResultMatcher) jsonPath("$[0].customerName", is("John Doe")))
                .andExpect((ResultMatcher) jsonPath("$[1].customerName", is("Jane Smith")))
                .andExpect((ResultMatcher) jsonPath("$[2].customerName", is("Bob Johnson")));
    }

    @Test
    public void testGetOrderById() throws Exception {
        Optional<Order> order = orderRepository.findById(1L);

        mockMvc.perform(get("/orders/" + order.get()))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) content().contentType(MediaType.APPLICATION_JSON))
                .andExpect((ResultMatcher) jsonPath("$.id", is(order.get().getId().intValue())))
                .andExpect((ResultMatcher) jsonPath("$.customerName", is("John Doe")))
                .andExpect((ResultMatcher) jsonPath("$.customerEmail", is("john.doe@example.com")))
                .andExpect((ResultMatcher) jsonPath("$.shippingAddress", is("123 Main St, Anytown USA")))
                .andExpect((ResultMatcher) jsonPath("$.totalAmount", is(100.0)))
                .andExpect((ResultMatcher) jsonPath("$.status", is("PENDING")));
    }

//    @Test
//    public void testCreateOrder() throws Exception {
//        Order newOrder = Order.builder()
//                .customerName("Alice Brown")
//                .customerEmail("alice.brown@example.com")
//                .shippingAddress("789 Maple St, Anytown USA")
//                .totalAmount(400.0)
//                .status(Order.OrderStatus.PENDING)
//                .build();
//
//        mockMvc.perform(post("/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(newOrder)))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").exists())
//                .andExpect(jsonPath("$.customerName", is("Alice Brown")))
//                .andExpect(jsonPath("$.customerEmail", is("alice.brown@example.com")))
//                .andExpect(jsonPath("$.shippingAddress", is("789 Maple St, Anytown USA")))
//                .andExpect(jsonPath("$.totalAmount", is(400.0)))
//                .andExpect(jsonPath("$.status", is("PENDING")));
//    }

}