package com.example.engineary.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;

import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.mapper.DiaryEntryMapper;
import com.example.engineary.model.DiaryEntry;
import com.example.engineary.repository.DiaryEntryRepository;

import jakarta.transaction.Transactional;

@Service
public class DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    // selectAll
    @Transactional
    public Page<DiaryEntryResponse> getAllEntries(Pageable pageable) {
        // ページング 10個ごと
        Page<DiaryEntry> entities = diaryEntryRepository.findAll(pageable);
        Page<DiaryEntryResponse> responses = entities.map(DiaryEntryMapper::toResponse);

        return responses;
    }

    // create
    public DiaryEntryResponse createDiaryEntry(DiaryEntryRequest request) {

        DiaryEntry inputEntity = DiaryEntryMapper.toEntity(request);
        DiaryEntry outputEntity = diaryEntryRepository.save(inputEntity);

        DiaryEntryResponse response = DiaryEntryMapper.toResponse(outputEntity);

        return response;
    }

    // update
    public void updateDiaryEntry(Long id, DiaryEntryRequest request) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        DiaryEntry entryDetails = DiaryEntryMapper.toEntity(request);
        entry.setTitle(entryDetails.getTitle());
        entry.setContents(entryDetails.getContents());
        entry.setWorkedTime(entryDetails.getWorkedTime());
        entry.setWorkedDate(entryDetails.getWorkedDate());
        entry.setUpdatedAt(entryDetails.getUpdatedAt());
        entry.setCreatedAt(entryDetails.getCreatedAt());

        diaryEntryRepository.save(entry);
    }

    // delete
    public void deleteDiaryEntry(Long id) {

        // エラーを明確に出すためにfind->delete
        diaryEntryRepository.findById(id)
                .ifPresentOrElse(diaryEntryRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(id);
                        });
    }

}
