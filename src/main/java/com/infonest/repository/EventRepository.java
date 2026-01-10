package com.infonest.repository;

import com.infonest.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate date);
    List<Event> findByClubId(String clubId);
    java.util.Optional<Event> findByClubIdAndEventName(String clubId, String eventName);
    
}




