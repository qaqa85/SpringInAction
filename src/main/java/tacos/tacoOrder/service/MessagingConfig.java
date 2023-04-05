package tacos.tacoOrder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import tacos.tacoOrder.TacoOrder;

import java.util.HashMap;
import java.util.Map;

@Configuration @Profile({"jms", "rabbitmq"})
@RequiredArgsConstructor
public class MessagingConfig {
    private final ObjectMapper objectMapper;

//    @Bean
//    public Destination orderQueue() {
//        return new ActiveMQQueue("tacocloud.order.queue");
//    }

    @Bean
    MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);
        messageConverter.setTypeIdPropertyName("_typeId");
        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("order", TacoOrder.class);
        messageConverter.setTypeIdMappings(typeIdMappings);
        return messageConverter;
    }
}
