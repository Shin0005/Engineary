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



@RestController
@RequestMapping("/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;

    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    //CRUD作成
    //select all
    @GetMapping
    public ResponseEntity<List<DiaryEntryResponse>> getAllEntries(){
        List<DiaryEntry> entities = diaryEntryService.getAllEntries();
        //List<response>形式で返却
        return ResponseEntity.ok(DiaryEntryMappar.toListResponse(entities));
    }

    //create
    @PostMapping
    public ResponseEntity<DiaryEntryResponse> createDiaryEntry(@RequestBody DiaryEntryRequest request){
        //requestにnullがあり得るがDTOの@でせき止め
        
        //request(dto)からentityに変換してcreate
        DiaryEntry entity = diaryEntryService.createDiaryEntry(DiaryEntryMappar.toEntity(request));
        //entityからresponseに変換
        DiaryEntryResponse response = DiaryEntryMappar.toResponse(entity);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //update
    @PutMapping("/{id}")
    public ResponseEntity<DiaryEntryResponse> updateDiaryEntry(
        @PathVariable Long id,
        @RequestBody DiaryEntryRequest req) {
            
        //reqからentityに変換
        DiaryEntry entityDetail = DiaryEntryMappar.toEntity(req);
        //update実行
        try{
            DiaryEntry updatedEntry = diaryEntryService.updateDiaryEntry(id, entityDetail);
            DiaryEntryResponse res = DiaryEntryMappar.toResponse(updatedEntry);
            return ResponseEntity.ok(res);
        }catch(RuntimeException e){
            //eをどう使う。
            return ResponseEntity.notFound().build();
        }
    }

    //delete
    @DeleteMapping("/{id}")
    public ResponseEntity<DiaryEntry> deleteDiaryEntry(@PathVariable Long id){
        try{
            diaryEntryService.deleteDiaryEntry(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    
}
