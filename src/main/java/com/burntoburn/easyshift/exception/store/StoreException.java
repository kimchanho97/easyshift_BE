package com.burntoburn.easyshift.exception.store;

import com.burntoburn.easyshift.common.exception.BusinessException;
import com.burntoburn.easyshift.common.exception.ErrorCode;

public class StoreException extends BusinessException {

    public StoreException(ErrorCode errorCode) {
        super(errorCode);
    }

    public static StoreException storeAccessDenied() {
        return new StoreException(StoreErrorCode.STORE_ACCESS_DENIED);
    }
}