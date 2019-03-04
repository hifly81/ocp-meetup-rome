package com.redhat.kafka.order.process.consumer.handle;

import com.redhat.kafka.order.process.event.OrderEvent;
import com.redhat.kafka.order.process.shipment.ShipmentClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class OrderProcessHandle extends ConsumerHandle {

    private static final String REST_SHIPMENT_URI =
            System.getenv("shipment.url") != null? System.getenv("shipment.url") :"http://localhost:8080/shipment";


    @Override
    public void process(ConsumerRecord record) {
        OrderEvent orderEvent = (OrderEvent) record.value();
        ShipmentClient shipmentClient = new ShipmentClient();
        shipmentClient.sendOrderEvent(REST_SHIPMENT_URI, orderEvent);
    }
}

