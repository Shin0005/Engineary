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
        //Optional<DEntry>をDEntryに変換できないと出るが、optionalはnullかもしれないことを
        //表すので、nullの例外処理を書かないと型変換できない?ggr
        DiaryEntry entry = diaryEntryRepository.findById(id);
        entry.setTitle(entryDetails.getTitle());
        //項目追加・lombok用の書き方を調べる
    }
}
