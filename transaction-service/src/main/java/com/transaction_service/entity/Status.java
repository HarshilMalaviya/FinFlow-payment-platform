package com.transaction_service.entity;

import com.transaction_service.state.Processing;

public enum Status {
    INITIATED ,
    COMPLETED ,
    FAILED ,
    REFUNDED,
    PROCESSING

}
