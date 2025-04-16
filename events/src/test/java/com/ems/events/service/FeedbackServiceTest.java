package com.ems.events.service;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // ✅ Add TicketRepository
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ems.events.entity.Event;
import com.ems.events.entity.Feedback;
import com.ems.events.entity.User;
import com.ems.events.repo.EventRepository;
import com.ems.events.repo.FeedbackRepository;
import com.ems.events.repo.TicketRepository;
import com.ems.events.repo.UserRepository;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TicketRepository ticketRepository; // ✅ Mock TicketRepository to prevent NullPointerException

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private Event testEvent;
    private User testUser;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "John Doe", "john@example.com", "password123", "9876543210","USER");

        testEvent = new Event(1L, "Tech Conference", "Technology", "New York",
                LocalDateTime.of(2025, 5, 20, 10, 0), testUser, true);

        testFeedback = new Feedback(1L, testEvent, testUser, "Great event!", 5, LocalDateTime.now(), true);
    }

    @Test
    void testSubmitFeedback_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(ticketRepository.existsByUserUserIdAndEventEventIdAndIsActiveTrue(anyLong(), anyLong()))
                .thenReturn(true); // ✅ Mock ticketRepository check
        when(feedbackRepository.save(any(Feedback.class))).thenAnswer(invocation -> {
            Feedback savedFeedback = invocation.getArgument(0);
            savedFeedback.setFeedbackId(1L); // Simulate database ID assignment
            return savedFeedback;
        });

        Feedback feedback = feedbackService.submitFeedback(1L, 1L, "Amazing event!", 5);

        assertNotNull(feedback);
        assertEquals("Amazing event!", feedback.getMessage());
        assertNotNull(feedback.getFeedbackId());
        verify(feedbackRepository, times(1)).save(any(Feedback.class));
    }
}
