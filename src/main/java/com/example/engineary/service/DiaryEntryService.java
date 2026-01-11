package com.example.engineary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.engineary.model.DiaryEntry;
import com.example.engineary.repository.DiaryEntryRepository;


@Service
public class DiaryEntryService {

    /* レポジトリーフィールド */
    private final DiaryEntryRepository diaryEntryRepository;

    /* レポジトリーコンストラクタ */
    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository){
        this.diaryEntryRepository = diaryEntryRepository;
    }

    public List<DiaryEntry> getAllEntries(){
        return diaryEntryRepository.findAll();
    }
}
