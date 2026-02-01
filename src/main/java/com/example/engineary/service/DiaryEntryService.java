package com.example.engineary.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.engineary.model.DiaryEntry;
import com.example.engineary.repository.DiaryEntryRepository;

import jakarta.transaction.Transactional;

@Service
public class DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;
    //コンストラクタ一つならspringが暗黙的にコンストラクタ作成するため不要
    public DiaryEntryService(DiaryEntryRepository diaryEntryRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
    }

    // selectAll
    @Transactional
    public List<DiaryEntry> getAllEntries() {
        //OOMの可能性、findID->findbyIDのtransactionに変更
        List<Long> ids = diaryEntryRepository.findIdList();

        //findByIdをしたいが、N+1問題なのでListを与えて一気に返してもらう
        //しかし返却地が巨大だとOOM　→　ページング導入? -> オーバースペック
        List<DiaryEntry> entities = diaryEntryRepository.findByIdIn(ids);

        return entities;
    }

    // create
    public DiaryEntry createDiaryEntry(DiaryEntry diaryEntry) {
        return diaryEntryRepository.save(diaryEntry);
    }

    // update
    public DiaryEntry updateDiaryEntry(Long id, DiaryEntry entryDetails) {
        DiaryEntry entry = diaryEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("タスクが見つかりません: " + id));

        entry.setTitle(entryDetails.getTitle());
        entry.setContents(entryDetails.getContents());
        entry.setWorkedTime(entryDetails.getWorkedTime());
        entry.setWorkedDate(entryDetails.getWorkedDate());
        entry.setUpdatedAt(entryDetails.getUpdatedAt());
        entry.setCreatedAt(entryDetails.getCreatedAt());

        return diaryEntryRepository.save(entry);
    }

    // delete
    public void deleteDiaryEntry(Long id) {
        // エラーを明確に出すためにfind->delete
        diaryEntryRepository.findById(id)
                .ifPresentOrElse(diaryEntryRepository::delete,
                        () -> {
                            throw new RuntimeException("タスクが見つかりません" + id);
                        });
    }

}
