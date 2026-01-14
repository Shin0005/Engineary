package com.example.engineary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.engineary.model.DiaryEntry;
import com.example.engineary.repository.DiaryEntryRepository;


@Service
public class DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;

    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository){
        this.diaryEntryRepository = diaryEntryRepository;
    }

    //selectAll
    public List<DiaryEntry> getAllEntries(){
        return diaryEntryRepository.findAll();
    }

    //create
    public DiaryEntry createDiaryEntry(DiaryEntry diaryEntry){
        return diaryEntryRepository.save(diaryEntry);
    }

    //update
    public DiaryEntry updateDiaryEntry(Long id, DiaryEntry entryDetails){
        DiaryEntry entry = diaryEntryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("タスクが見つかりません: " + id));

        entry.setTitle(entryDetails.getTitle());
        entry.setContents(entryDetails.getContents());
        //追加

        return diaryEntryRepository.save(entry);
    }
}
