package io.app.arbittrading.cex.service.impl;

import io.app.arbittrading.cex.bean.CexBean;
import io.app.arbittrading.cex.client.CexFeignClient;
import io.app.arbittrading.cex.service.CexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CexServiceImpl implements CexService {
    private final CexFeignClient cexFeignClient;
    @Override
    public CexBean getCexData() {
        try {
            return cexFeignClient.getCexData();
        } catch (Exception e) {
            log.error("Failed to retrieve ticker prices: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve ticker prices", e);
        }
    }

}
