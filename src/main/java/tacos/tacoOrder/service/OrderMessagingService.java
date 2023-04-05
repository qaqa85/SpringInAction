package tacos.tacoOrder.service;

import tacos.tacoOrder.TacoOrder;

public interface OrderMessagingService {
    void sendOrder(TacoOrder order);
    TacoOrder receiveMessage();
}
