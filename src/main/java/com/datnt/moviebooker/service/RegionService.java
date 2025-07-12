package com.datnt.moviebooker.service;

import com.datnt.moviebooker.entity.Region;
import com.datnt.moviebooker.repository.RegionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public Region create(Region region) {
        return regionRepository.save(region);
    }

    public Region update(Long id, Region updated) {
        Region region = regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + id));

        region.setName(updated.getName());
        return regionRepository.save(region);
    }

    public void delete(Long id) {
        if (!regionRepository.existsById(id)) {
            throw new EntityNotFoundException("Region not found: " + id);
        }
        regionRepository.deleteById(id);
    }

    public Region getById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Region not found: " + id));
    }

    public Page<Region> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return regionRepository.findAll(pageable);
    }

    public List<Region> getAll() {
        return regionRepository.findAll(Sort.by("name").ascending());
    }
}
