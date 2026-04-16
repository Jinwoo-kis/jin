package com.mindease.demo.service;

import com.mindease.demo.model.Mood;
import com.mindease.demo.model.User;
import com.mindease.demo.repository.MoodRepository;
import com.mindease.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MoodService {

    private final MoodRepository moodRepository;
    private final UserRepository userRepository;

    public MoodService(MoodRepository moodRepository, UserRepository userRepository) {
        this.moodRepository = moodRepository;
        this.userRepository = userRepository;
    }

    public Mood saveMood(String username, String moodName, String emoji) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Mood mood = new Mood();
        mood.setUser(user);
        mood.setMood(moodName);
        mood.setEmoji(emoji);
        return moodRepository.save(mood);
    }

    public List<Mood> getMoodHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return moodRepository.findAllByUserOrderByCreatedAtDesc(user);
    }
}
