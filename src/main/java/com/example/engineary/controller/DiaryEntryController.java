package com.example.engineary.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.example.engineary.model.DiaryEntry;
import com.example.engineary.service.DiaryEntryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/diary")
public class DiaryEntryController {

    private final DiaryEntryService diaryEntryService;

    public DiaryEntryController(DiaryEntryService diaryEntryService) {
        this.diaryEntryService = diaryEntryService;
    }

    //CRUD作成
    //select all
    @GetMapping("/entries")
    public ResponseEntity<List<DiaryEntry>> getAllEntries(){
        List<DiaryEntry> entries = diaryEntryService.getAllEntries();
        return ResponseEntity.ok(entries);
    }

    //create
    @PostMapping
    public ResponseEntity<DiaryEntry> createDiaryEntry(@RequestBody DiaryEntry diaryEntry){
        DiaryEntry entry = diaryEntryService.createDiaryEntry(diaryEntry);

        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }

    //update
    @PostMapping("/{id}")
    public ResponseEntity<DiaryEntry> updateDiaryEntry(
        @PathVariable Long id,
        @RequestBody DiaryEntry entryDetails) {

        //update実行
        try{
            DiaryEntry updatedEntry = diaryEntryService.updateDiaryEntry(id, entryDetails);
            return ResponseEntity.ok(updatedEntry);
        }catch(RuntimeException e){
            //eをどう使う。
            return ResponseEntity.notFound().build();
        }
    }

    //delete
    @PostMapping("/{id}")
    public ResponseEntity<DiaryEntry> deleteDiaryEntry(@PathVariable Long id){
        try{
            diaryEntryService.deleteDiaryEntry(id);
            return ResponseEntity.noContent().build();
        }catch(RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    
}
