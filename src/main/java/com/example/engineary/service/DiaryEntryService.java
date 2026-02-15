package com.example.engineary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.mapper.DiaryEntryMappar;
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
    public List<DiaryEntryResponse> getAllEntries() {
        // OOMの可能性、findID->findbyIDのtransactionに変更
        List<Long> ids = diaryEntryRepository.findIdList();

        // findByIdをしたいが、N+1問題なのでListを与えて一気に返してもらう
        // しかし返却地が巨大だとOOM → ページング導入? -> オーバースペック
        List<DiaryEntry> entities = diaryEntryRepository.findByIdIn(ids);
        List<DiaryEntryResponse> responses = DiaryEntryMappar.toListResponse(entities);

        return responses;
    }

    // create requestで受け取り、entityでDB処理、responseで返却
    public DiaryEntryResponse createDiaryEntry(DiaryEntryRequest request) {

        DiaryEntry inputEntity = DiaryEntryMappar.toEntity(request);
        DiaryEntry outputEntity = diaryEntryRepository.save(inputEntity);

        DiaryEntryResponse response = DiaryEntryMappar.toResponse(outputEntity);

        return response;
    }

    // update
    public void updateDiaryEntry(Long id, DiaryEntryRequest request) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        DiaryEntry entryDetails = DiaryEntryMappar.toEntity(request);
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
