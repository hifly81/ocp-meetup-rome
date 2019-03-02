package com.redhat.kafka.shipment.test;

import com.redhat.kafka.shipment.controller.ShipmentController;
import com.redhat.kafka.shipment.event.ItemEvent;
import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.store.InMemoryStore;
import com.redhat.kafka.shipment.store.ShipmentRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(ShipmentController.class)
public class ShipmentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ShipmentRepository repository;

    @Test
    public void test_no_content() throws Exception {
        mvc.perform(post("/shipment").content("{\"id\":\"11111\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void test_receive_order_created() throws Exception {
        mvc.perform(post("/shipment").content("{\"id\":\"11111\", \"eventType\":\"ORDER_CREATED\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assert.assertTrue(InMemoryStore.getStore().containsKey("11111"));
        Assert.assertTrue(InMemoryStore.getStore().size() == 1);
        Assert.assertTrue(((OrderEvent)InMemoryStore.getStore().get("11111").get(0)).getEventType() == OrderEvent.EventType.ORDER_CREATED);
    }

    @Test
    public void test_receive_order_item_ready() throws Exception {
        InMemoryStore.getStore().put("11111", new ArrayList());
        mvc.perform(post("/shipment").content("{\"id\":\"11111\", \"eventType\":\"ORDER_ITEM_READY\"}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assert.assertTrue(InMemoryStore.getStore().containsKey("11111"));
        Assert.assertTrue(InMemoryStore.getStore().size() == 1);
        Assert.assertTrue(((OrderEvent)InMemoryStore.getStore().get("11111").get(0)).getEventType() == OrderEvent.EventType.ORDER_ITEM_READY);
    }

    @Test
    public void test_receive_order_ready() throws Exception {
        Date orderTimestamp = new Date();

        OrderEvent orderEvent1 = new OrderEvent();
        orderEvent1.setEventType(OrderEvent.EventType.ORDER_CREATED);
        orderEvent1.setName("Order1");
        orderEvent1.setTimestamp(orderTimestamp);
        orderEvent1.setId("11111");
        orderEvent1.setItemIds(Arrays.asList("item1"));

        OrderEvent orderEvent2 = new OrderEvent();
        orderEvent2.setEventType(OrderEvent.EventType.ORDER_ITEM_READY);
        orderEvent2.setName("Order1");
        orderEvent2.setTimestamp(orderTimestamp);
        orderEvent2.setId("11111");
        orderEvent2.setItemIds(Arrays.asList("item1"));
        ItemEvent itemEvent = new ItemEvent();
        itemEvent.setId("03232");
        itemEvent.setName("Samsung P9");
        itemEvent.setOrderId("11111");
        itemEvent.setTimestamp(new Date());
        itemEvent.setPrice(110);
        orderEvent2.setItemEvent(itemEvent);

        List list = new ArrayList();
        list.add(orderEvent1);
        list.add(orderEvent2);

        InMemoryStore.getStore().put("11111", list);
        mvc.perform(post("/shipment").content("{\"id\":\"11111\", \"eventType\":\"ORDER_READY\", \"itemIds\": [\"item1\"]}")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assert.assertTrue(InMemoryStore.getStore().size() == 0);
    }



}
