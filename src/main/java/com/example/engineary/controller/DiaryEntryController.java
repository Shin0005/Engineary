package com.example.engineary.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.engineary.DTO.DiaryEntryRequest;
import com.example.engineary.DTO.DiaryEntryResponse;
import com.example.engineary.mapper.DiaryEntryMappar;
import com.example.engineary.model.DiaryEntry;
import com.example.engineary.service.DiaryEntryService;

import jakarta.validation.Valid;

/**
 * 日誌機能コントローラ
 */
@RestController
@RequestMapping("/api/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;

    // DI(autowiredは１コンストラクタの時省略可)
    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    /**
     * 全項目取得メソッド
     */
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponse>> getAllEntries() {
        List<DiaryEntryResponse> responses = diaryEntryService.getAllEntries();
        
        return ResponseEntity.ok(responses);
    }

    /**
     * 新規作成メソッド
     * 
     * @param request フロント入力
     */
    @PostMapping
    public ResponseEntity<DiaryEntryResponse> createDiaryEntry(@Valid @RequestBody DiaryEntryRequest request) {
        // request(dto)からentityに変換してcreate
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
        // eをどう使う。
        //return ResponseEntity.notFound().build();
    }

    /**
     * 削除メソッド<br>
     * 指定されたidのレコードを削除する。
     * 
     * @param id 日誌Id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiaryEntry(@PathVariable Long id) {
        
        diaryEntryService.deleteDiaryEntry(id);
        return ResponseEntity.noContent().build();

        //    return ResponseEntity.notFound().build();
        
    }

}
