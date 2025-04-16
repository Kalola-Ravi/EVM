package com.ems.events.service;

import com.ems.events.entity.Event;
import com.ems.events.entity.User;
import com.ems.events.exception.EventNotFoundException;
import com.ems.events.exception.UserNotFoundException;
import com.ems.events.repo.EventRepository;
import com.ems.events.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event testEvent;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUserName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setContactNumber("9876543210");

        testEvent = new Event();
        testEvent.setName("Tech Conference");
        testEvent.setCategory("Technology");
        testEvent.setLocation("New York");
        testEvent.setDate(LocalDateTime.of(2025, 5, 20, 10, 0));
        testEvent.setUser(testUser);
        testEvent.setActive(true);
    }

    
    @Test
    void testCreateEvent_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> eventService.createEvent(testEvent));
    }



    @Test
    void testUpdateEvent_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(100L, testEvent));
    }

    @Test
    void testDeleteEvent_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));

        eventService.deleteEvent(100L);

        assertFalse(testEvent.isActive());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void testGetAllEvents() {
        when(eventRepository.findByIsActiveTrue()).thenReturn(List.of(testEvent));

        List<Event> events = eventService.getAllEvents();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
    }

    @Test
    void testGetEventByCategory() {
        when(eventRepository.findByCategoryAndIsActiveTrue("Technology")).thenReturn(List.of(testEvent));

        List<Event> events = eventService.getEventsByCategory("Technology");

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertEquals("Technology", events.get(0).getCategory());
    }

    @Test
    void testGetUpcomingEvents() {
        when(eventRepository.findByDateAfterAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(List.of(testEvent));

        List<Event> events = eventService.getUpcomingEvents();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.get(0).getDate().isAfter(LocalDateTime.now()));
    }

    @Test
    void testGetPastEvents() {
        testEvent.setDate(LocalDateTime.of(2020, 1, 1, 10, 0)); // Set past date
        when(eventRepository.findByDateBeforeAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(List.of(testEvent));

        List<Event> events = eventService.getPastEvents();

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.get(0).getDate().isBefore(LocalDateTime.now()));
    }

    @Test
    void testSearchEventsByName() {
        when(eventRepository.findByNameContainingIgnoreCaseAndIsActiveTrue("Tech"))
                .thenReturn(List.of(testEvent));

        List<Event> events = eventService.searchEventsByName("Tech");

        assertFalse(events.isEmpty());
        assertEquals(1, events.size());
        assertTrue(events.get(0).getName().contains("Tech"));
    }
}