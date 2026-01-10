
package com.infonest.controller;
import com.infonest.model.Event;
import com.infonest.model.Registration;
import com.infonest.repository.EventRepository;
import com.infonest.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/faculty")
public class ClubOfficialController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    // 1. ADD EVENT
    @PostMapping("/add-event")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> addEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return ResponseEntity.ok("Event added successfully!");
    }

    // 2. FETCH EVENT BY NAME (For Placeholders)
    @GetMapping("/event-details/{clubId}/{eventName}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Event> getEventDetails(@PathVariable String clubId, @PathVariable String eventName) {
        Event event = eventRepository.findByClubIdAndEventName(clubId, eventName)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return ResponseEntity.ok(event);
    }

    // 3. UPDATE EVENT (Event ID and Club ID are kept constant)
    @PutMapping("/update-event/{eventId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> updateEvent(@PathVariable Long eventId, @RequestBody Event eventDetails) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // Updating all fields as per your table structure
        event.setEventName(eventDetails.getEventName());
        event.setDescription(eventDetails.getDescription());
        event.setVenueId(eventDetails.getVenueId());
        event.setEventDate(eventDetails.getEventDate());
        event.setEventTime(eventDetails.getEventTime());
        event.setDeadline(eventDetails.getDeadline());
        
        // Correcting the variable name to match your DB column 'registration_form_link'
        event.setRegistrationFormLink(eventDetails.getRegistrationFormLink());

        eventRepository.save(event);
        return ResponseEntity.ok("Event details updated successfully!");
    }

    // 4. VIEW SUBMISSIONS
    @GetMapping("/submissions/{clubId}")
    @PreAuthorize("hasRole('FACULTY')")
    public List<Registration> getClubSubmissions(@PathVariable String clubId) {
        return registrationRepository.findAllByClubId(clubId);
    }

    // 5. UPDATE STATUS (Approve/Reject)
    @PutMapping("/update-status/{regId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> updateStatus(@PathVariable Long regId, @RequestParam String status) {
        Registration reg = registrationRepository.findById(regId).orElseThrow();
        reg.setStatus(status);
        registrationRepository.save(reg);
        return ResponseEntity.ok("Status updated to " + status);
    }
}