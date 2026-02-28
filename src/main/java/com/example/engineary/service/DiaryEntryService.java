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

    /**
     * ページングを用いてレポジトリから日誌を取得する<br>
     * 1. repositoryから日誌を取得<br>
     * 2. DiaryEntryからResponse変換して返却
     * 
     * @param pageable
     * @return Page&lt;DiaryEntryResponse&gt; 複数の日誌
     */
    @Transactional
    public Page<DiaryEntryResponse> getAllEntries(Pageable pageable) {
        // ページング 10個ごと
        Page<DiaryEntry> entities = diaryEntryRepository.findAll(pageable);
        Page<DiaryEntryResponse> responses = entities.map(DiaryEntryMapper::toResponse);

        return responses;
    }

    /**
     * リクエストを受け取り日誌を新規作成<br>
     * 1. requestをEntityに変換<br>
     * 2. DBに保存・結果を取得<br>
     * 3. 結果をresponseにして返却
     * 
     * @param request DiaryEntryRequest
     * @return DiaryEntryResponse 保存した日誌
     */
    public DiaryEntryResponse createDiaryEntry(DiaryEntryRequest request) {

        DiaryEntry inputEntity = DiaryEntryMapper.toEntity(request);
        DiaryEntry outputEntity = diaryEntryRepository.save(inputEntity);

        DiaryEntryResponse response = DiaryEntryMapper.toResponse(outputEntity);

        return response;
    }

    /**
     * id指定されたリクエストを受け取り日誌を更新<br>
     * 1. 指定されたidの日誌を検索し、日誌を返却<br>
     * 2. 日誌内容をrequestに代入<br>
     * 3. 日誌をDBに保存
     * 
     * @param id      Long
     * @param request DiaryEntryRequest
     */
    public void updateDiaryEntry(Long id, DiaryEntryRequest request) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        DiaryEntry entryDetails = DiaryEntryMapper.toEntity(request);
        entry.setTitle(entryDetails.getTitle());
        entry.setContents(entryDetails.getContents());
        entry.setWorkedTime(entryDetails.getWorkedTime());
        entry.setWorkedDate(entryDetails.getWorkedDate());

        diaryEntryRepository.save(entry);
    }

    /**
     * 指定されたidの日誌を削除<br>
     * 1. 指定されたidの日誌を検索し、日誌を返却
     * 2. DBから日誌を削除
     * 
     * @param id Long
     */
    public void deleteDiaryEntry(Long id) {

        // エラーを明確に出すためにfind->delete
        diaryEntryRepository.findById(id)
                .ifPresentOrElse(diaryEntryRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(id);
                        });
    }

}
