package com.example.engineary.controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.service.DiaryEntryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 日誌機能コントローラ
 */
@RestController
@RequestMapping("/api/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;

    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    /**
     * 全項目取得メソッド
     * ページング 10個ごと
     */
    @GetMapping
    public ResponseEntity<Page<DiaryEntryResponse>> getAllEntries(@PageableDefault(size = 10) Pageable pageable) {
        Page<DiaryEntryResponse> responses = diaryEntryService.getAllEntries(pageable);

        return ResponseEntity.ok(responses);
    }

    /**
     * 新規作成メソッド
     * 
     * @param request フロント入力
     */
    @PostMapping
    public ResponseEntity<DiaryEntryResponse> createDiaryEntry(@Valid @RequestBody DiaryEntryRequest request) {
        DiaryEntryResponse response = diaryEntryService.createDiaryEntry(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新メソッド<br>
     * 指定されたidのレコードに対して更新を行う。
     * 
     * @param id      日誌Id
     * @param request フロント入力
     */
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDiaryEntry(
            @PathVariable Long id,
            @Valid @RequestBody DiaryEntryRequest request) {

        diaryEntryService.updateDiaryEntry(id, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 削除メソッド<br>
     * 指定されたidのレコードを削除する。
     * 
     * @param id 日誌Id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryEntry(@NotNull @PathVariable Long id) {

        diaryEntryService.deleteDiaryEntry(id);
        return ResponseEntity.noContent().build();
    }

}
