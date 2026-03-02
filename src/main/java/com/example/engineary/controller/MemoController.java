package com.example.engineary.controller;

import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;
import com.example.engineary.service.MemoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * 日誌機能コントローラ
 */
@RestController
@RequestMapping("/api/diary")
public class MemoController {

    private final MemoService memoService;

    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    /**
     * 全項目取得メソッド
     * ページング 10個ごと
     */
    @GetMapping
    public ResponseEntity<Page<MemoResponse>> getAllEntries(@PageableDefault(size = 10) Pageable pageable) {
        Page<MemoResponse> responses = memoService.getAllEntries(pageable);

        return ResponseEntity.ok(responses);
    }

    /**
     * 新規作成メソッド
     * 
     * @param request フロント入力
     */
    @PostMapping
    public ResponseEntity<MemoResponse> createMemo(@Valid @RequestBody MemoRequest request) {
        MemoResponse response = memoService.createMemo(request);

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
    public ResponseEntity<Void> updateMemo(
            @PathVariable Long id,
            @Valid @RequestBody MemoRequest request) {

        memoService.updateMemo(id, request);

        return ResponseEntity.ok().build();
    }

    /**
     * 削除メソッド<br>
     * 指定されたidのレコードを削除する。
     * 
     * @param id 日誌Id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@NotNull @PathVariable Long id) {

        memoService.deleteMemo(id);
        return ResponseEntity.noContent().build();
    }

}
