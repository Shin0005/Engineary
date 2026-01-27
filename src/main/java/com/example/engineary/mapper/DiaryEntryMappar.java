package com.example.engineary.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.engineary.DTO.DiaryEntryRequest;
import com.example.engineary.DTO.DiaryEntryResponse;
import com.example.engineary.model.DiaryEntry;

/**
 * 入出力処理用DTOとEntity(DiaryEntry)のMapper
 */
public class DiaryEntryMappar {
    /* サーバ入力dto -> Entity */
    // なぜstaticにする必要がある？メモリ的にだいじょぶ？
    public static DiaryEntry toEntity(DiaryEntryRequest req) {
        if (req == null) {
            return null;
        }
        DiaryEntry entity = new DiaryEntry();
        entity.setTitle(req.getTitle());
        entity.setContents(req.getContents());
        entity.setWorkedTime(req.getWorkedTime());
        // String(YYYY/MM/DD)からLocalDate変換
        entity.setWorkedDate(LocalDate.parse(req.getWorkedDate(),
                DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        return entity;
    }

    /* Entity -> サーバ出力dto */
    public static DiaryEntryResponse toResponse(DiaryEntry entity) {
        if (entity == null) {
            return null;
        }
        DiaryEntryResponse res = new DiaryEntryResponse();
        res.setTitle(entity.getTitle());
        res.setContents(entity.getContents());
        res.setWorkedTime(entity.getWorkedTime());
        // LocalDateをString(YYYY/MM/DD)に変換
        res.setWorkedDate(entity.getWorkedDate()
        .format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));

        return res;
    }
}
