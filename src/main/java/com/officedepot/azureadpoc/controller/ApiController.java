package com.officedepot.azureadpoc.controller;

import com.officedepot.azureadpoc.service.LoggingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ApiController {

    private final LoggingService loggingService;

    public ApiController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping("api1")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_PricingServiceUsers')")
    public String api1() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        loggingService.write(">API 1 call<", authentication);
        return "API 1 call has been logged";
    }

    @GetMapping("api2")
    @ResponseBody
    @PreAuthorize("hasRole('ROLE_StaticCatalogueUsers')")
    public String api2() throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        loggingService.write(">API 2 call<", authentication);
        return "API 2 call has been logged";
    }

}
