package com.mindease.demo.controller;

import com.mindease.demo.model.JournalEntry;
import com.mindease.demo.service.JournalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journal")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveJournalEntry(@RequestParam String username,
                                             @RequestParam String text,
                                             @RequestParam(required = false) String title,
                                             @RequestParam(required = false) String mood,
                                             @RequestParam(required = false) MultipartFile media) {
        System.out.println("Journal save request - username: " + username + ", text: " + text + ", media: " + (media != null ? media.getOriginalFilename() : "null"));
        if (username == null || text == null || text.trim().isEmpty()) {
            return badRequest("Username and journal text are required.");
        }
        try {
            JournalEntry entry = journalService.saveJournalEntry(username, text, mood != null ? mood : "😊", media);
            System.out.println("Journal entry saved with mediaPath: " + entry.getMediaPath());
            return ResponseEntity.ok(entry);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        } catch (IOException ex) {
            return badRequest("Failed to save media file: " + ex.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> getJournalList(@RequestParam String username) {
        try {
            List<JournalEntry> entries = journalService.getJournalEntries(username);
            return ResponseEntity.ok(entries);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable Long id, @RequestParam String username) {
        try {
            journalService.deleteJournalEntry(id, username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static class SaveJournalRequest {
        private String username;
        private String text;
        private String mood;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }
    }
}
