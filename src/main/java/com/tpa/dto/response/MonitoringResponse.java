package com.tpa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitoringResponse {

    private Map<String, Object> kafka;

    private List<ClaimResponse> failedClaims;

    private List<Map<String, Object>> errorLogs;
}