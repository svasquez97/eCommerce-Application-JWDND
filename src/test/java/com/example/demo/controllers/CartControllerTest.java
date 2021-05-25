package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    private User u;
    private User newU;
    private Cart c;
    private Cart mewC;
    private Item i;
    private Item newI;
    private ModifyCartRequest removeFromCart;
    private ModifyCartRequest modifyCart;
    private ModifyCartRequest negativeUserReq;
    private ModifyCartRequest negativeItemReq;


    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        u = new User();
        c = new Cart();
        i = new Item();

        newU = new User();
        mewC = new Cart();
        newI = new Item();

        removeFromCart = new ModifyCartRequest();
        modifyCart = new ModifyCartRequest();

        negativeUserReq = new ModifyCartRequest();
        negativeItemReq = new ModifyCartRequest();

        u.setId(1L);
        u.setUsername("u");
        u.setPassword("pw");
        newU.setId(1L);
        i.setName("itemTest");
        i.setPrice(BigDecimal.valueOf(10.0));
        i.setDescription("itemTestDesc");
        c.setUser(u);
        u.setCart(c);

        newU.setId(2L);
        newU.setUsername("newUser");
        newU.setPassword("pw");
        newI.setId(2L);
        newI.setName("itemTest1");
        newI.setPrice(BigDecimal.valueOf(15.0));
        newI.setDescription("itemTest1Desc");
        mewC.setUser(newU);
        newU.setCart(mewC);

        modifyCart.setItemId(1L);
        modifyCart.setQuantity(1);
        modifyCart.setUsername("u");

        removeFromCart.setItemId(2L);
        removeFromCart.setQuantity(1);
        removeFromCart.setUsername("newUser");

        negativeUserReq.setUsername("negUser");
        negativeUserReq.setItemId(1L);
        negativeUserReq.setQuantity(1);

        negativeItemReq.setUsername("u");
        negativeItemReq.setItemId(3L);
        negativeItemReq.setQuantity(1);

    }

    @Test
    public void add_to_cart() throws Exception {
        when(userRepo.findByUsername("u")).thenReturn(u);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(i));

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCart);

        Cart cart = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals("u",cart.getUser().getUsername());
        assertEquals("itemTest",cart.getItems().get(0).getName());

    }

    @Test
    public void remove_from_cart() throws Exception {
        when(userRepo.findByUsername("newUser")).thenReturn(newU);
        when(itemRepo.findById(2L)).thenReturn(Optional.of(newI));

        final ResponseEntity<Cart> response = cartController.removeFromcart(removeFromCart);

        Cart cart = response.getBody();
        assertEquals(200,response.getStatusCodeValue());
        assertEquals(0, cart.getItems().size());
        assertEquals("newUser",cart.getUser().getUsername());
    }

    @Test
    public void user_not_found() throws Exception {
        final ResponseEntity<Cart> response = cartController.addTocart(negativeUserReq);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void item_not_found() throws Exception {
        final ResponseEntity<Cart> response = cartController.addTocart(negativeItemReq);
        assertEquals(404, response.getStatusCodeValue());
    }
}
