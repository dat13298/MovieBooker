package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.ShowTime;
import com.datnt.moviebooker.repository.ShowTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShowTimeService {
    private final ShowTimeRepository showTimeRepository;

    public ShowTime findById(Long id) {
        return showTimeRepository.findById(id).orElse(null);
    }
}
