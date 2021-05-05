package com.officedepot.azureadpoc.controller;


import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimAccessor;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
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

    @GetMapping("/me")
    @ResponseBody
    public User me(@RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient cl) {
        OAuth2AccessToken accessToken = cl.getAccessToken();
        final List<String> scopes = Arrays.asList("User.Read");

        GraphServiceClient graphServiceClient = getClient();
        User me = graphServiceClient
                .me()
                .buildRequest()
                .select("displayName")
                .get();

        return me;
    }

    @SuppressWarnings("unchecked")
    private GraphServiceClient<Request> getClient() {
        final List<String> scopes = Arrays.asList("https://graph.microsoft.com/.default");

        final ClientSecretCredential defaultCredential = new ClientSecretCredentialBuilder()
                .clientId("8751c852-f58e-47d9-a16b-51df4e0f1e73")
                .clientSecret("r89HXq8z_mZRV2m_mZlW88LI-gslWk.v88")
                .tenantId("c06a9d9f-5fa8-4ba3-9c9f-4aae1705ad7a")
                .build();

        final IAuthenticationProvider authProvider = new TokenCredentialAuthProvider(scopes, defaultCredential);
        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }

}
