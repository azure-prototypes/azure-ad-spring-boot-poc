package com.officedepot.azureadpoc.service;


import org.apache.commons.io.FileUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class LoggingService {

    private final FileOutputStream s;

    public LoggingService() throws IOException {
        s = FileUtils.openOutputStream(new File("calls.log"));
    }

    public void write(String message, Authentication auth) throws IOException {
        var fullMessage = getCurrentTime() + " "
                + message + "  "
                + auth.getName() + "  "
                + ((DefaultOidcUser) auth.getPrincipal()).getAttribute("preferred_username") + "  "
                + "\n";
        s.write(fullMessage.getBytes(StandardCharsets.UTF_8));
    }

    private String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
