package com.project.demo.logic.entity.diet_preferences;

import com.project.demo.logic.entity.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class Diet_PreferencesSeederTest {

    @Mock
    private Diet_PreferenceRepository dietPreferenceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ContextRefreshedEvent event;

    private Diet_PreferencesSeeder seeder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        seeder = new Diet_PreferencesSeeder(dietPreferenceRepository, userRepository);
    }

    @Test
    void onApplicationEvent_ShouldSeedDietPreferences() {
        // Arrange
        when(dietPreferenceRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(dietPreferenceRepository.save(any(Diet_Preferences.class))).thenReturn(new Diet_Preferences());

        // Act
        seeder.onApplicationEvent(event);

        // Assert
        // Verify that save was called at least once (actual count depends on your JSON file)
        verify(dietPreferenceRepository, atLeastOnce()).save(any(Diet_Preferences.class));
    }

    @Test
    void onApplicationEvent_ExistingPreferences_ShouldNotSaveDuplicates() {
        // Arrange
        Diet_Preferences existingPreference = new Diet_Preferences();
        existingPreference.setName("Vegetarian");
        
        when(dietPreferenceRepository.findByName(anyString())).thenReturn(Optional.of(existingPreference));

        // Act
        seeder.onApplicationEvent(event);

        // Assert
        // Verify that save was not called for existing preferences
        verify(dietPreferenceRepository, never()).save(any(Diet_Preferences.class));
    }
}