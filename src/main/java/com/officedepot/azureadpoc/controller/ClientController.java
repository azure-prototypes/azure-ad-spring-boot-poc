package com.officedepot.azureadpoc.controller;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Controller
public class ClientController {

    @GetMapping("/")
    public String index(
            Model model,
            OAuth2AuthenticationToken authentication,
            @RegisteredOAuth2AuthorizedClient("azure") OAuth2AuthorizedClient azureClient
    ) {
        model.addAttribute("userName", authentication.getName());
        String preferredUsername = Optional.of(authentication)
                .map(OAuth2AuthenticationToken::getPrincipal)
                .map(user -> (OidcUser) user)
                .map(StandardClaimAccessor::getPreferredUsername)
                .orElse("UNKNOWN");

        Collection<String> authorities = Optional.of(authentication)
                .map(OAuth2AuthenticationToken::getAuthorities).orElse(new ArrayList<>()).stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        model.addAttribute("authorities", authorities);
        model.addAttribute("preferredUsername", preferredUsername);
        model.addAttribute("clientName", azureClient.getClientRegistration().getClientName());
        return "index";
    }

    @GetMapping("/graph")
    @ResponseBody
    public OAuth2AuthorizedClient graph(@RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphClient) {
        return graphClient;
    }

}
