package com.ajit.shopsphere.dtos;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.ajit.shopsphere.entities.Address;
import com.ajit.shopsphere.entities.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetails {
    
    private UUID id;
    private Date orderDate;
    private Address address;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private String shipmentNumber;
    private Date expectedDeliveryDate;
    private List<OrderItemDetails> orderItemList;
}
