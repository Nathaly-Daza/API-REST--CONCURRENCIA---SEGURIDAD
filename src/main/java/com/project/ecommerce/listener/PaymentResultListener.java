package com.project.ecommerce.listener;

import com.project.ecommerce.event.PaymentApprovedEvent;
import com.project.ecommerce.event.PaymentFailedEvent;
import com.project.ecommerce.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultListener {

    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "${app.rabbitmq.queue.payment-result}")
    public void handlePaymentResult(Object event) {
        if (event instanceof PaymentApprovedEvent approved) {
            System.out.println(" Evento recibido: PAGO APROBADO para orden #" + approved.orderId());
            orderService.confirmOrder(approved.orderId());
        } else if (event instanceof PaymentFailedEvent failed) {
            System.out.println(" Evento recibido: PAGO FALLIDO para orden #" + failed.orderId());
            orderService.cancelOrder(failed.orderId(), failed.reason());
        }
    }
}