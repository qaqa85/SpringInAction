package tacos.tacoOrder.service;

import jakarta.jms.Destination;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import tacos.tacoOrder.TacoOrder;

@Service
@Profile("amqp")
@RequiredArgsConstructor
public class JmsTacoOrderMessagingService implements OrderMessagingService {
    private final JmsTemplate jmsTemplate;
    private final Destination destination;

    @Override
    public void sendOrder(TacoOrder order) {
        jmsTemplate.convertAndSend(destination, order,
                message -> {
                     message.setStringProperty("X_ORDER_SOURCE", "WEB");
                     return message;
                });
    }

    @Override
    public TacoOrder receiveMessage() {
        throw new UnsupportedOperationException();
    }
}
