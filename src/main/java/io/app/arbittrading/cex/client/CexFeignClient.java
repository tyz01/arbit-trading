package io.app.arbittrading.cex.client;

import io.app.arbittrading.cex.bean.CexBean;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "cexFeignClient", url = "${cex.apiUrl}")
public interface CexFeignClient {
    @GetMapping("/api/last_prices/.../USDT/.../")
    CexBean getCexData();
}
