package com.visits.service;

import com.visits.model.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface VisitService {

    Visit createPlanned(UUID customerId, UUID siteId, UUID technicianId,
                        OffsetDateTime start, OffsetDateTime end,
                        VisitPriority priority, String purpose, String notesPlanned);

    Visit updatePlanned(UUID visitId, OffsetDateTime start, OffsetDateTime end,
                    UUID technicianId, VisitPriority priority, String purpose, String notesPlanned,
                    VisitState state);

    Visit checkIn(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng);

    Visit checkOut(UUID visitId, UUID actorId, OffsetDateTime when, Double lat, Double lng, String workSummary);

    void cancel(UUID visitId, UUID actorId);

    Page<Visit> list(UUID customerId, UUID technicianId, VisitState state,
                     OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    List<Visit> myVisitsToday(UUID technicianId, LocalDate today);

    List<VisitEvent> events(UUID visitId);

    List<VisitNote> addNote(UUID visitId, UUID authorId, NoteVisibility visibility, String body);

    Visit getById(UUID visitId);
}