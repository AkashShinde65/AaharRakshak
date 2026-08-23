package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.DetectedFoodDetails;
import com.aaharrakshak.complaint.dto.PackagedFoodScanRequest;

public interface OcrAdapter {

    DetectedFoodDetails detectPackagedFoodDetails(PackagedFoodScanRequest request);
}
