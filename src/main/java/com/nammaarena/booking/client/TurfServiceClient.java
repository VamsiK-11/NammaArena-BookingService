package com.nammaarena.booking.client;

import com.nammaarena.booking.dto.TurfDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "turf-service")
public interface TurfServiceClient {
    
    @GetMapping("/api/turfs/{id}")
    TurfDto getTurfById(@PathVariable Long id);
    
    @GetMapping("/api/turfs/owner")
    List<TurfDto> getOwnerTurfs();
}