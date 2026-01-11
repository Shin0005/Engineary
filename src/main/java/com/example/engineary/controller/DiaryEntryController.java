package com.example.engineary.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.example.engineary.model.DiaryEntry;


@RestController
@RequestMapping("/diary")
public class DiaryEntryController {

    //全投稿取得
    @GetMapping("/entries")
    public ResponseEntity<List<DiaryEntry>> getAllEntries(){
        List<DiaryEntry> entries = null;//diaryEntryService.getAllEntries();


        return ResponseEntity.ok(entries);
    }

    
}
