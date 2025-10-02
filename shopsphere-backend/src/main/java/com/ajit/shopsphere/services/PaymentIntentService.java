package com.ajit.shopsphere.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ajit.shopsphere.auth.entities.User;
import com.ajit.shopsphere.entities.Order;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

@Component
public class PaymentIntentService {
    
    public Map<String, String> createPaymentIntent(Order order) throws StripeException {
        User user = order.getUser();
        Map<String, String> metaData = new HashMap<>();
        metaData.put("orderId",order.getId().toString());

        PaymentIntentCreateParams paymentIntentCreateParams = PaymentIntentCreateParams.builder()
                .setAmount((long) (order.getTotalAmount() * 100 * 88.7))
                .setCurrency("inr")
                .putAllMetadata(metaData)
                .setDescription("Test Payment Project ShopSphere")
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();
        
        PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);
        Map<String, String> map = new HashMap<>();
        map.put("client_secret", paymentIntent.getClientSecret());
        return map;
    }
}
