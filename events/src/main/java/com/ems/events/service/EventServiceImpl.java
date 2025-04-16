package com.ems.events.service;

import com.ems.events.entity.Event;
import com.ems.events.entity.User;
import com.ems.events.exception.EventNotFoundException;

import com.ems.events.exception.UserNotFoundException;
import com.ems.events.repo.EventRepository;
import com.ems.events.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventServiceImpl {
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationServiceImpl notificationService;

    public Event createEvent(Event event) {
        User user = userRepository.findById(event.getUser().getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        event.setUser(user);
        event.setActive(true);
        Event savedEvent = eventRepository.save(event);

        notificationService.notifyAllUersAboutNewEvent(savedEvent);

        return savedEvent;
    }

    public Event updateEvent(Long eventId, Event eventDetails) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        existingEvent.setName(eventDetails.getName());
        existingEvent.setCategory(eventDetails.getCategory());
        existingEvent.setLocation(eventDetails.getLocation());
        existingEvent.setDate(eventDetails.getDate());
        Event updatedEvent = eventRepository.save(existingEvent);

        notificationService.notifyAllUsersAboutEventUpdate(updatedEvent);

        return updatedEvent;
    }

     

    public void deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        event.setActive(false);
        eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findByIsActiveTrue();
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findByEventIdAndIsActiveTrue(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    public List<Event> getEventsByUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        return eventRepository.findByUserUserIdAndIsActiveTrue(userId);
    }

    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategoryAndIsActiveTrue(category);
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateAfterAndIsActiveTrue(LocalDateTime.now());
    }

    public List<Event> getPastEvents() {
        return eventRepository.findByDateBeforeAndIsActiveTrue(LocalDateTime.now());
    }

    public List<Event> searchEventsByName(String query) {
        return eventRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query);
    }
}
