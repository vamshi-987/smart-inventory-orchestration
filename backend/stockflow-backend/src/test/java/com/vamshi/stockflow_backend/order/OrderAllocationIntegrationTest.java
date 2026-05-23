package com.vamshi.stockflow_backend.order;


import com.vamshi.stockflow_backend.inventory.repository.InventoryRepository;
import com.vamshi.stockflow_backend.order.repository.OrderRepository;
import com.vamshi.stockflow_backend.product.repository.ProductRepository;
import com.vamshi.stockflow_backend.warehouse.repository.WarehouseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderAllocationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void placeOrder_ShouldAllocateSamePincodeWarehouse_WhenStockAvailable() throws Exception {
        // create category
        // create product
        // create warehouse with same pincode
        // create inventory
        // call POST /api/orders
        // expect status created
        // expect allocated warehouse id
    }
}