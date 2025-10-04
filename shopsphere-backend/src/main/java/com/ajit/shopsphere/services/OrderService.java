package com.ajit.shopsphere.services;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.ajit.shopsphere.auth.dto.OrderResponse;
import com.ajit.shopsphere.auth.entities.User;
import com.ajit.shopsphere.dtos.OrderDetails;
import com.ajit.shopsphere.dtos.OrderItemDetails;
import com.ajit.shopsphere.dtos.OrderRequest;
import com.ajit.shopsphere.entities.Address;
import com.ajit.shopsphere.entities.Order;
import com.ajit.shopsphere.entities.OrderItem;
import com.ajit.shopsphere.entities.OrderStatus;
import com.ajit.shopsphere.entities.Payment;
import com.ajit.shopsphere.entities.PaymentStatus;
import com.ajit.shopsphere.entities.Product;
import com.ajit.shopsphere.repositories.OrderRepository;
import com.stripe.model.PaymentIntent;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ProductService productService;

    @Autowired
    PaymentIntentService paymentIntentService;
    
    @Transactional
    public OrderResponse creatOrder(OrderRequest orderRequest, Principal principal) throws Exception {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Address address = user.getAddressList().stream().filter(address1 -> orderRequest.getAddressId().equals(address1.getId())).findFirst().orElseThrow(BadRequestException::new);
        
        Order order = Order.builder()
                .user(user)
                .address(address)
                .totalAmount(orderRequest.getTotalAmount())
                .orderDate(orderRequest.getOrderDate())
                .discount(orderRequest.getDiscount())
                .expectedDeliveryDate(orderRequest.getExpectedDeliveryDate())
                .paymentMethod(orderRequest.getPaymentMethod())
                .OrderStatus(OrderStatus.PENDING)
                .build();

        List<OrderItem> orderItems = orderRequest.getOrderItemRequests().stream().map(orderItemRequest -> {
            try{
                Product product = productService.fetchProductById(orderItemRequest.getProductId());
                OrderItem orderItem = OrderItem.builder()
                        .product(product)
                        .productVariantId(orderItemRequest.getProductVariantId())
                        .quantity(orderItemRequest.getQuantity())
                        .order(order)
                        .build();
                return orderItem;
            }catch(Exception e){
                throw new RuntimeException(e);
            }
        }).toList();

        order.setOrderItemList(orderItems);
        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(new Date());
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(order.getPaymentMethod());
        order.setPayment(payment);
        Order savedOrder = orderRepository.save(order);

        OrderResponse orderResponse = OrderResponse.builder()
                .paymentMethod(orderRequest.getPaymentMethod())
                .orderId(savedOrder.getId())
                .build();

        if(Objects.equals(orderRequest.getPaymentMethod(), "CARD")){
            orderResponse.setCredentials(paymentIntentService.createPaymentIntent(order));
        }
        return orderResponse;
    }

    // public Map<String, String> updateStatus(String paymentIntentId, String status) {
    //     try{
    //         PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

    //         if(paymentIntent != null && paymentIntent.getStatus().equals("succeeded")){
    //             String orderId = paymentIntent.getMetadata().get("orderId");
    //             Order order = orderRepository.findById(UUID.fromString(orderId)).orElseThrow(BadRequestException::new);
    //             Payment payment = order.getPayment();
    //             payment.setPaymentStatus(PaymentStatus.COMPLETED);
    //             payment.setPaymentMethod(paymentIntent.getPaymentMethod());
    //             order.setPaymentMethod(paymentIntent.getPaymentMethod());
    //             //order.setOrderStatus(OrderStatus.IN_PROGRESS);
    //             order.setPayment(payment);
    //             Order savedOrder = orderRepository.save(order);
    //             Map<String,String> map = new HashMap<>();
    //             map.put("orderId", String.valueOf(savedOrder.getId()));
    //             return map;

    //         }else{
    //             throw new IllegalArgumentException("PaymentIntent not found or missing metadata");
    //         }

    //     }catch(Exception e){
    //         throw new IllegalArgumentException("PaymentIntent not found or missing metadata");
    //     }
    // }

    public Map<String, String> updateStatus(String paymentIntentId, String status){
        try{
            if (paymentIntentId == null || paymentIntentId.isBlank()) {
            throw new IllegalArgumentException("paymentIntentId is null/empty");
        }

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        if (paymentIntent == null) {
            throw new IllegalArgumentException("PaymentIntent not found: " + paymentIntentId);
        }

        String stripeStatus = paymentIntent.getStatus();
        if (!"succeeded".equalsIgnoreCase(stripeStatus)) {
            throw new IllegalArgumentException("PaymentIntent not succeeded. Status: " + stripeStatus);
        }

        Map<String, String> metadata = paymentIntent.getMetadata();
        if (metadata == null || !metadata.containsKey("orderId")) {
            throw new IllegalArgumentException("PaymentIntent metadata missing orderId: " + paymentIntentId);
        }

        String orderIdStr = metadata.get("orderId");
        if (orderIdStr == null || orderIdStr.isBlank()) {
            throw new IllegalArgumentException("orderId in metadata is empty: " + paymentIntentId);
        }

        UUID orderUuid;
        try {
            orderUuid = UUID.fromString(orderIdStr);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException("orderId in metadata is not a valid UUID: " + orderIdStr, iae);
        }

        Order order = orderRepository.findById(orderUuid)
                .orElseThrow(() -> new BadRequestException("Order not found: " + orderUuid));

        Payment payment = order.getPayment();
        if (payment == null) {
            payment = new Payment();
            order.setPayment(payment);
        }
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentMethod(paymentIntent.getPaymentMethod());
        order.setPayment(payment);
        order.setPaymentMethod(paymentIntent.getPaymentMethod());

        Order savedOrder = orderRepository.save(order);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", savedOrder.getId().toString());
        map.put("paymentIntentId", paymentIntentId);
        return map;

        }catch(Exception e){
            throw new RuntimeException("Failed to update payment status", e);
        }
    }

    public List<OrderDetails> getOrdersByUser(String name) {
        User user = (User) userDetailsService.loadUserByUsername(name);
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream().map(order -> {
            return OrderDetails.builder()
                    .id(order.getId())
                    .orderDate(order.getOrderDate())
                    .orderStatus(order.getOrderStatus())
                    .shipmentNumber(order.getShipmentTrackingNumber())
                    .address(order.getAddress())
                    .totalAmount(order.getTotalAmount())
                    .orderItemList(getItemDetails(order.getOrderItemList()))
                    .expectedDeliveryDate(order.getExpectedDeliveryDate())
                    .build();
        }).toList();
    }

    private List<OrderItemDetails> getItemDetails(List<OrderItem> orderItemList){
        return orderItemList.stream().map(orderItem -> {
            return OrderItemDetails.builder()
                    .id(orderItem.getId())
                    .itemPrice(orderItem.getItemPrice())
                    .product(orderItem.getProduct())
                    .productVariantId(orderItem.getProductVariantId())
                    .quantity(orderItem.getQuantity())
                    .build();
        }).toList();
    }

    public void cancelOrder(UUID id, Principal principal) {
        User user = (User) userDetailsService.loadUserByUsername(principal.getName());
        Order order = orderRepository.findById(id).get();
        if(order != null && order.getUser().getId().equals(user.getId())){
            order.setOrderStatus(OrderStatus.CANCELLED);
            //logic to refund amount
            orderRepository.save(order);
        }else{
            new RuntimeException("Invalid request");
        }
    }
}
