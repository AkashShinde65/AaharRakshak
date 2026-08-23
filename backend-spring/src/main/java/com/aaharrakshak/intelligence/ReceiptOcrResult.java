package com.aaharrakshak.intelligence;

public record ReceiptOcrResult(
        boolean verified,
        String verificationToken,
        String detectedVendor,
        String note) {
}
