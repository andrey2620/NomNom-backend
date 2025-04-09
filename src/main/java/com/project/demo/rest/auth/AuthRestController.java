// AuthRestController.java
package com.project.demo.rest.auth;

import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.Base64;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    public AuthRestController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              RoleRepository roleRepository,
                              JwtService jwtService,
                              AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/exchange-google-code")
    public ResponseEntity<?> exchangeGoogleCode(@RequestBody Map<String, String> payload) {
        try {
            String code = payload.get("code");
            String codeVerifier = payload.get("codeVerifier");

            String tokenEndpoint = "https://oauth2.googleapis.com/token";
            RestTemplate restTemplate = new RestTemplate();

            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("code", code);
            requestBody.add("client_id", clientId);
            requestBody.add("client_secret", clientSecret);
            requestBody.add("redirect_uri", redirectUri);
            requestBody.add("grant_type", "authorization_code");
            requestBody.add("code_verifier", codeVerifier);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("id_token")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No se pudo obtener el id_token de Google");
            }

            String idToken = (String) responseBody.get("id_token");
            Claims claims = decodeJwt(idToken);
            String email = claims.get("email", String.class);

            return authenticationService.authenticateGoogle(email);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + e.getMessage());
        }
    }

    private Claims decodeJwt(String idToken) {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) throw new IllegalArgumentException("Invalid JWT token");

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();

        try {
            Map<String, Object> payloadMap = mapper.readValue(payload, Map.class);
            return Jwts.claims(payloadMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse JWT: " + e.getMessage());
        }
    }
}
