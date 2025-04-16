package com.ems.events.service;

import com.ems.events.entity.Event;
import com.ems.events.entity.User;
import com.ems.events.entity.Ticket;
import com.ems.events.exception.EventNotFoundException;
import com.ems.events.exception.UserNotFoundException;
import com.ems.events.repo.EventRepository;
import com.ems.events.repo.UserRepository;
import com.ems.events.repo.TicketRepository;
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

class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private Event testEvent;
    private User testUser;
    private Ticket testTicket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User(1L, "John Doe", "john@example.com", "password123", "9876543210","USER");

        testEvent = new Event(1L, "Tech Conference", "Technology", "New York",
                LocalDateTime.of(2025, 5, 20, 10, 0), testUser, true);

        testTicket = new Ticket("MOV0003", testEvent, testUser, LocalDateTime.now(), "Booked", true);
    }

    @Test
    void testBookTicket_Success() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Ticket bookedTicket = ticketService.bookTicket(1L, 1L);

        assertNotNull(bookedTicket);
        assertEquals("Booked", bookedTicket.getStatus());
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @Test
    void testBookTicket_EventNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> ticketService.bookTicket(1L, 1L));
    }

    @Test
    void testBookTicket_UserNotFound() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(testEvent));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> ticketService.bookTicket(1L, 1L));
    }

 


    @Test
    void testViewTickets() {
        when(ticketRepository.findByStatusAndIsActiveTrue("Booked")).thenReturn(List.of(testTicket));

        List<Ticket> tickets = ticketService.viewTickets();

        assertFalse(tickets.isEmpty());
        assertEquals(1, tickets.size());
    }
}

