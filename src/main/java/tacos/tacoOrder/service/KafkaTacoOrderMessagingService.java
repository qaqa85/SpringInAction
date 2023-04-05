package tacos.tacoOrder.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tacos.tacoOrder.TacoOrder;

@Service @Profile("kafkamq")
@RequiredArgsConstructor
public class KafkaTacoOrderMessagingService implements OrderMessagingService {
    private final KafkaTemplate<String, TacoOrder> kafkaTemplate;

    @Override
    public void sendOrder(TacoOrder order) {
        kafkaTemplate.send("tacocloud.orders.topic", order);
    }

    @Override
    public TacoOrder receiveMessage() {
         ConsumerRecord<String, TacoOrder> record = kafkaTemplate.receive("tacocloud.orders.topic", 1, 0);
         return record != null
                 ? record.value()
                 : null;
    }

    @KafkaListener(topics = "tacocloud.orders.topic")
    void listenToTacoOrders(TacoOrder tacoOrder) {
        System.out.println(tacoOrder);
    }
}
