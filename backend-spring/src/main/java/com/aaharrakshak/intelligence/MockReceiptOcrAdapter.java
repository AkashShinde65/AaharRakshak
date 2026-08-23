package com.aaharrakshak.intelligence;

import com.aaharrakshak.intelligence.dto.ReceiptMetadataRequest;
import org.springframework.stereotype.Component;

@Component
public class MockReceiptOcrAdapter implements ReceiptOcrAdapter {

    @Override
    public ReceiptOcrResult verify(ReceiptMetadataRequest receipt) {
        String token = "mock-receipt-" + receipt.checksumSha256().substring(0, 12).toLowerCase();
        return new ReceiptOcrResult(
                true,
                token,
                receipt.originalFileName().replace('.', ' '),
                "Mock OCR verified receipt metadata for academic demonstration.");
    }
}
