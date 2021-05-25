package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    List<Item> item;

    List<Item> itemByName;

    public static Item createItem(long id, String name, BigDecimal price, String description) {
        Item i = new Item();
        i.setId(id);
        i.setName(name);
        i.setPrice(price);
        i.setDescription(description);
        return i;
    }

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        Item i = createItem(1L, "itemTest", BigDecimal.valueOf(10), "itemTestDesc");
        Item i1 = createItem(2L, "itemTest1", BigDecimal.valueOf(15), "itemTest1Desc");

        item = Arrays.asList(i, i1);

        itemByName = Arrays.asList(i, i1);

        /*
        Item i = new Item();
        long id = 1L;
        i.setId(id);
        i.setName("itemTest");
        i.setPrice(BigDecimal.valueOf(10));
        i.setDescription("itemTestDesc");

        Item i1 = new Item();
        long id1 = 2L;
        i1.setId(id1);
        i1.setName("itemTest1");
        i1.setPrice(BigDecimal.valueOf(15));
        i1.setDescription("itemTest1Desc");
        */
    }

    /*
    @Test
    public void get_item_by_id() {
        ResponseEntity<Item> itemResponseEntity = itemController.getItemById(1L);
    }

    @Test
    public void get_item_by_desc() {
        ResponseEntity<List<Item>> itemResponseEntity = itemController.getItemsByName("item1");
    }
    */

    @Test
    public void find_item_by_id() throws Exception {
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item.get(0)));

        final ResponseEntity<Item> response = itemController.getItemById(1L);
        Item i = response.getBody();

        assertNotNull(i);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("itemTest", i.getName());
        assertEquals(BigDecimal.valueOf(10), i.getPrice());
        assertEquals("itemTestDesc", i.getDescription());
    }

    @Test
    public void find_item_by_name() throws Exception {
        when(itemRepo.findByName("itemTest")).thenReturn(itemByName);

        final ResponseEntity<List<Item>> response = itemController.getItemsByName("itemTest");

        List<Item> items = response.getBody();
        assertNotNull(item);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(itemByName, items);
    }

    @Test
    public void find_all_items() throws Exception {
        when(itemRepo.findAll()).thenReturn(item);

        final ResponseEntity<List<Item>> response = itemController.getItems();

        List<Item> items = response.getBody();
        assertNotNull(item);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        assertEquals(item, items);
    }

    @Test
    public void item_name_not_found() throws Exception {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("negItem");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void item_id_not_found() throws Exception {
        final ResponseEntity<Item> response = itemController.getItemById(3L);
        assertEquals(404, response.getStatusCodeValue());
    }

}
