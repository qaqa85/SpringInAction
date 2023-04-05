package tacos.tacoOrder.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import tacos.tacoOrder.TacoOrder;

@Service
@RequiredArgsConstructor
@Profile("rabbitmq")
public class RabbitTacoOrderMessagingService implements OrderMessagingService {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void sendOrder(TacoOrder order) {
        MessageConverter converter = rabbitTemplate.getMessageConverter();
        MessageProperties properties = new MessageProperties();
        properties.setHeader("X_ORDER_SOURCE", "WEB");
        Message message = converter.toMessage(order, properties);
        rabbitTemplate.send(message);
    }

    @Override
    public TacoOrder receiveMessage() {
        Object order = rabbitTemplate.receiveAndConvert("taco.order.queue");

        return order != null ? (TacoOrder) order : null;
    }
}
