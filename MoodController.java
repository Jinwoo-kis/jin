package com.mindease.demo.controller;

import com.mindease.demo.model.Mood;
import com.mindease.demo.service.MoodService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mood")
public class MoodController {

    private final MoodService moodService;

    public MoodController(MoodService moodService) {
        this.moodService = moodService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveMood(@RequestBody SaveMoodRequest request) {
        System.out.println("Mood save request - username: " + request.getUsername() + ", mood: " + request.getMood() + ", emoji: " + request.getEmoji());
        if (request.getUsername() == null || request.getMood() == null) {
            return badRequest("Username and mood are required.");
        }
        try {
            Mood mood = moodService.saveMood(request.getUsername(), request.getMood(), request.getEmoji());
            System.out.println("Mood saved: " + mood.getId() + ", mood: " + mood.getMood() + ", emoji: " + mood.getEmoji());
            return ResponseEntity.ok(mood);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMoodHistory(@RequestParam String username) {
        System.out.println("Mood history request for username: " + username);
        try {
            List<Mood> history = moodService.getMoodHistory(username);
            System.out.println("Returning " + history.size() + " mood records for " + username);
            return ResponseEntity.ok(history);
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

    public static class SaveMoodRequest {
        private String username;
        private String mood;
        private String emoji;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }

        public String getEmoji() {
            return emoji;
        }

        public void setEmoji(String emoji) {
            this.emoji = emoji;
        }
    }
}
