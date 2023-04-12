package tacos.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.core.GenericHandler;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

@Component
public class OrderLoggerHandler implements GenericHandler<EmailOrder> {
    private final Logger log = LoggerFactory.getLogger(OrderLoggerHandler.class);

    @Override
    public Object handle(EmailOrder order, MessageHeaders headers) {
        log.info(order.toString());
        return null;
    }
}
