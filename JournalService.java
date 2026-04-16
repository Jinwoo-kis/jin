package com.mindease.demo.service;

import com.mindease.demo.model.JournalEntry;
import com.mindease.demo.model.User;
import com.mindease.demo.repository.JournalEntryRepository;
import com.mindease.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class JournalService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;

    public JournalService(JournalEntryRepository journalEntryRepository, UserRepository userRepository) {
        this.journalEntryRepository = journalEntryRepository;
        this.userRepository = userRepository;
    }

    public JournalEntry saveJournalEntry(String username, String text, String mood, MultipartFile mediaFile) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        JournalEntry entry = new JournalEntry();
        entry.setUser(user);
        entry.setText(text);
        entry.setMood(mood);

        if (mediaFile != null && !mediaFile.isEmpty()) {
            // Validate file type
            String contentType = mediaFile.getContentType();
            System.out.println("Media file content type: " + contentType + ", size: " + mediaFile.getSize());
            if (!isValidMediaType(contentType)) {
                throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and MP4 are allowed.");
            }

            // Validate file size (max 10MB)
            if (mediaFile.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size too large. Maximum size is 10MB.");
            }

            // Save file
            String fileName = UUID.randomUUID().toString() + "_" + mediaFile.getOriginalFilename();
            Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads", "media");
            Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(mediaFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            entry.setMediaPath("/uploads/media/" + fileName);
            System.out.println("Media file saved to: " + filePath.toString() + ", mediaPath set to: " + entry.getMediaPath());
        } else {
            System.out.println("No media file provided");
        }

        return journalEntryRepository.save(entry);
    }

    private boolean isValidMediaType(String contentType) {
        return contentType != null && (
            contentType.equals("image/jpeg") ||
            contentType.equals("image/png") ||
            contentType.equals("video/mp4")
        );
    }

    public List<JournalEntry> getJournalEntries(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return journalEntryRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public void deleteJournalEntry(Long entryId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        journalEntryRepository.findById(entryId)
                .filter(entry -> entry.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("Journal entry not found"));
        journalEntryRepository.deleteById(entryId);
    }
}
