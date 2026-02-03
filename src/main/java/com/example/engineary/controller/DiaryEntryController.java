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

/**
 * 日誌機能コントローラ
 */
@RestController
@RequestMapping("/api/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;
    //DI(autowiredは１コンストラクタの時省略可)
    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    /**
     * 全項目取得メソッド
     */
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponse>> getAllEntries(){
        List<DiaryEntry> entities = diaryEntryService.getAllEntries();
        //List<response>形式で返却
        List<DiaryEntryResponse> response = DiaryEntryMappar.toListResponse(entities);
        return ResponseEntity.ok(response);
    }

    /**
     * 新規作成メソッド
     * @param request フロント入力
     */
    @PostMapping
    public ResponseEntity<DiaryEntryResponse> createDiaryEntry(@RequestBody DiaryEntryRequest request){
        //request(dto)からentityに変換してcreate
        DiaryEntry entity = diaryEntryService.createDiaryEntry(DiaryEntryMappar.toEntity(request));
        //response形式で返却
        DiaryEntryResponse response = DiaryEntryMappar.toResponse(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 更新メソッド<br>
     * 指定されたidのレコードに対して更新を行う。
     * @param id 日誌Id
     * @param request フロント入力
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiaryEntryResponse> updateDiaryEntry(
        @PathVariable Long id,
        @RequestBody DiaryEntryRequest request) {
            
        //reqからentityに変換
        DiaryEntry entityDetail = DiaryEntryMappar.toEntity(request);

        try{
            DiaryEntry updatedEntry = diaryEntryService.updateDiaryEntry(id, entityDetail);
            //response形式で返却
            DiaryEntryResponse res = DiaryEntryMappar.toResponse(updatedEntry);
            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
            //eをどう使う。
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 削除メソッド<br>
     * 指定されたidのレコードを削除する。
     * @param id 日誌Id
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<DiaryEntry> deleteDiaryEntry(@PathVariable Long id){
        try{
            diaryEntryService.deleteDiaryEntry(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            //ewotukau
            return ResponseEntity.notFound().build();
        }
    }

    
}
