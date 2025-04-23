package com.project.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SousChefService {

    @Value("${elevenlabs.api.key}")
    private String apiKey;

    @Value("${elevenlabs.voice.daniela}")
    private String danielaId;

    @Value("${elevenlabs.voice.mauricio}")
    private String mauId;


    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateAudio(String text, String voiceName) {
        System.out.println("Texto recibido para audio: " + text);
        System.out.println("Voz solicitada: " + voiceName);

        try {
            String voiceId;
            switch (voiceName.toLowerCase()) {
                case "daniela" -> voiceId = danielaId;
                case "mauricio" -> voiceId = mauId;
                default -> throw new IllegalArgumentException("Nombre de voz inválido: " + voiceName);
            }
            String url = "https://api.elevenlabs.io/v1/text-to-speech/" + voiceId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("xi-api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(new MediaType("audio", "mpeg")));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text", text);
            requestBody.put("model_id", "eleven_monolingual_v1");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    byte[].class
            );

            byte[] audioBytes = response.getBody();
            if (audioBytes == null || audioBytes.length == 0) {
                System.err.println("No se recibió audio desde ElevenLabs.");
                throw new RuntimeException("No se recibió audio desde ElevenLabs.");
            }

            System.out.println("Tamaño del audio recibido: " + audioBytes.length + " bytes");
            return audioBytes;

        } catch (Exception e) {
            System.err.println("Error al generar audio desde ElevenLabs: " + e.getMessage());
            throw new RuntimeException("Error generando audio: " + e.getMessage(), e);
        }
    }
}
