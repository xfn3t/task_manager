package com.task.tracker.service;

import com.task.tracker.dto.StatusDTO;
import com.task.tracker.exception.ForbiddenException;
import com.task.tracker.exception.NotFoundException;
import com.task.tracker.model.Status;
import com.task.tracker.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final UserService userService;

    public List<Status> getAll() {

        return statusRepository.findAll();
    }

    public Optional<Status> getById(Long id) {
        return statusRepository.findById(id);
    }

    public Status add(StatusDTO statusDTO, String email) {

        Status status = new Status();

        status.setTitle(statusDTO.getTitle());
        status.setIsGlobal(false);

        if (userService.isAdminByEmail(email)) {
            if (statusDTO.getUserId() != null && statusDTO.getIsGlobal() != null) {
                status.setUser(
                        userService.getUserById(statusDTO.getUserId())
                                .orElseThrow(() -> new NotFoundException("User not found"))
                );
                status.setIsGlobal(statusDTO.getIsGlobal());
            }
        }

        statusRepository.save(status);
        return status;
    }

    public Status update(Long statusId, StatusDTO statusDTO) {

        Status status = getById(statusId)
                .orElseThrow(() -> new NotFoundException("Status not found"));

        status.setTitle(statusDTO.getTitle());

        statusRepository.save(status);
        return status;

    }

    public void delete(Long statusId, String email) throws ForbiddenException {


        Status status = getById(statusId)
                .orElseThrow(() -> new NotFoundException("Status not found"));

        if (userService.isAdminByEmail(email)) {
            if (status.getUser().getId() != null && status.getIsGlobal() != null) {
                if (status.getIsGlobal()) {
                    statusRepository.deleteById(statusId);
                } else {
                    throw new ForbiddenException();
                }
            }
        }
    }
}
