package com.example.engineary.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;

import com.example.engineary.exception.ResourceNotFoundException;
import com.example.engineary.mapper.MemoMapper;
import com.example.engineary.model.Memo;
import com.example.engineary.repository.MemoRepository;

import jakarta.transaction.Transactional;

@Service
public class MemoService {

    private final MemoRepository memoRepository;

    public MemoService(MemoRepository memoRepository) {
        this.memoRepository = memoRepository;
    }

    /**
     * ページングを用いてレポジトリから日誌を取得する<br>
     * 1. repositoryから日誌を取得<br>
     * 2. MemoからResponse変換して返却
     * 
     * @param pageable
     * @return Page&lt;MemoResponse&gt; 複数の日誌
     */
    @Transactional
    public Page<MemoResponse> getAllEntries(Pageable pageable) {
        // ページング 10個ごと
        Page<Memo> entities = memoRepository.findAll(pageable);
        Page<MemoResponse> responses = entities.map(MemoMapper::toResponse);

        return responses;
    }

    /**
     * リクエストを受け取り日誌を新規作成<br>
     * 1. requestをEntityに変換<br>
     * 2. DBに保存・結果を取得<br>
     * 3. 結果をresponseにして返却
     * 
     * @param request MemoRequest
     * @return MemoResponse 保存した日誌
     */
    @Transactional
    public MemoResponse createMemo(MemoRequest request) {

        Memo inputEntity = MemoMapper.toEntity(request);
        Memo outputEntity = memoRepository.save(inputEntity);

        MemoResponse response = MemoMapper.toResponse(outputEntity);

        return response;
    }

    /**
     * id指定されたリクエストを受け取り日誌を更新<br>
     * 1. 指定されたidの日誌を検索し、日誌を返却<br>
     * 2. 日誌内容をrequestに代入<br>
     * 3. 日誌をDBに保存
     * 
     * @param id      Long
     * @param request MemoRequest
     */
    @Transactional
    public void updateMemo(Long id, MemoRequest request) {
        Memo entry = memoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));

        Memo entryDetails = MemoMapper.toEntity(request);
        entry.setTitle(entryDetails.getTitle());
        entry.setContents(entryDetails.getContents());
        entry.setWorkedTime(entryDetails.getWorkedTime());
        entry.setWorkedDate(entryDetails.getWorkedDate());

        memoRepository.save(entry);
    }

    /**
     * 指定されたidの日誌を削除<br>
     * 1. 指定されたidの日誌を検索し、日誌を返却
     * 2. DBから日誌を削除
     * 
     * @param id Long
     */
    @Transactional
    public void deleteMemo(Long id) {

        // エラーを明確に出すためにfind->delete
        memoRepository.findById(id)
                .ifPresentOrElse(memoRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException(id);
                        });
    }

}
