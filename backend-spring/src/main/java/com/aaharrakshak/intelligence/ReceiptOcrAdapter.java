package com.aaharrakshak.intelligence;

import com.aaharrakshak.intelligence.dto.ReceiptMetadataRequest;

public interface ReceiptOcrAdapter {

    ReceiptOcrResult verify(ReceiptMetadataRequest receipt);
}
