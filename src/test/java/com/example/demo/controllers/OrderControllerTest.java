package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.coyote.Response;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private final OrderRepository orderRepo = mock(OrderRepository.class);

    private final UserRepository userRepo = mock(UserRepository.class);

    private User u;
    private Cart c;
    private Item i;
    private UserOrder order;

    List<Item> items = new ArrayList<>();
    List<UserOrder> orders = new ArrayList<>();

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
        TestUtils.injectObjects(orderController, "userRepository", userRepo);

        u = new User();
        c = new Cart();
        i = new Item();
        order = new UserOrder();

        u.setCart(c);
        u.setUsername("u");
        u.setPassword("pw");
        u.setId(1L);

        i.setId(1L);
        i.setName("itemTest");
        i.setPrice(BigDecimal.valueOf(10));
        i.setDescription("itemTestDesc");

        items.add(i);

        c.setUser(u);
        c.setId(1L);
        c.setItems(items);
        c.setTotal(BigDecimal.valueOf(10));

        order.setUser(u);
        order.setId(1L);
        order.setItems(items);
        order.setTotal(BigDecimal.valueOf(10));

        orders.add(order);
    }

    @Test
    public void submit_order_by_user() throws Exception {
        when(userRepo.findByUsername("u")).thenReturn(u);
        final ResponseEntity<UserOrder> response = orderController.submit("u");

        UserOrder userOrder = response.getBody();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("u", userOrder.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(10), userOrder.getTotal());
        assertEquals("itemTest", userOrder.getItems().get(0).getName());
    }

    @Test
    public void find_order_by_user() throws Exception {
        when(userRepo.findByUsername("u")).thenReturn(u);
        when(orderRepo.findByUser(u)).thenReturn(orders);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("u");

        List<UserOrder> theOrders = response.getBody();
        assertEquals(1, theOrders.size());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("u", theOrders.get(0).getUser().getUsername());
        assertEquals(BigDecimal.valueOf(10), theOrders.get(0).getTotal());
        assertEquals("itemTest", theOrders.get(0).getItems().get(0).getName());
    }

    @Test
    public void submit_order_by_user_not_found() throws Exception{
        final ResponseEntity<UserOrder> response = orderController.submit("negOrder");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void order_by_user_not_found() throws Exception {
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("negUser");
        assertEquals(404, response.getStatusCodeValue());
    }

}
