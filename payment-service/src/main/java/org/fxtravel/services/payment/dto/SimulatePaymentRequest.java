package org.fxtravel.services.payment.dto;

import lombok.Data;

@Data
public class SimulatePaymentRequest {
    private String orderNumber;
    private long timeout;
    private boolean inventoryResult; // 执行 Supplier 后的结果
    private Object extraData;
}
