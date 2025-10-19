package com.visits.service;

import com.visits.model.Visit;

public interface VisitEmailService {
    void onVisitCompleted(Visit visit);
}