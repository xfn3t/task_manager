package com.task.tracker.service;

import com.task.tracker.model.Priority;
import com.task.tracker.repository.PriorityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PriorityService {

    private final PriorityRepository priorityRepository;

    public List<Priority> getAll() {
        return priorityRepository.findAll();
    }

    public Optional<Priority> getById(Long id) {
        return priorityRepository.findById(id);
    }
}
