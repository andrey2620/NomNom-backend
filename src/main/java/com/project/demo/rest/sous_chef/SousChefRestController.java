package com.project.demo.rest.sous_chef;

import com.project.demo.services.SousChefService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sous-chef")
@CrossOrigin
public class SousChefRestController {

    private final SousChefService sousChefService;

    public SousChefRestController(SousChefService sousChefService) {
        this.sousChefService = sousChefService;
    }

    public static class TextRequest {
        private String text;
        private String voiceName;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getVoiceName() {
            return voiceName;
        }

        public void setVoiceName(String voiceName) {
            this.voiceName = voiceName;
        }
    }

    @PostMapping(value = "/speak", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> speak(@RequestBody TextRequest request) {
        byte[] audio = sousChefService.generateAudio(request.getText(), request.getVoiceName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(audio);
    }
}
