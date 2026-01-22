package com.example.engineary.mapper;

import com.example.engineary.DTO.DiaryEntryRequest;
import com.example.engineary.DTO.DiaryEntryResponse;
import com.example.engineary.model.DiaryEntry;

/**
 * 入出力処理用DTOとEntity(DiaryEntry)のMapper
 */
public class DiaryEntryMappar {
    /* サーバ入力dto -> Entity */
    public DiaryEntry toEntity(DiaryEntryRequest req){
        if(req == null){
            return null;
        }
        DiaryEntry entity =  new DiaryEntry();
        entity.setTitle(req.getTitle());
        entity.setContents(req.getContents());
        entity.setWorkedTime(req.getWorkedTime());
        entity.setWorkedDate(req.getWorkedDate());

        return entity;
    }

    /* Entity -> サーバ出力dto */
    public DiaryEntryResponse toEntity(DiaryEntry entity){
        if(entity == null){
            return null;
        }
        DiaryEntryResponse res =  new DiaryEntryResponse();
        res.setTitle(entity.getTitle());
        res.setContents(entity.getContents());
        res.setWorkedTime(entity.getWorkedTime());
        res.setWorkedDate(entity.getWorkedDate());
        
        return res;
    }
}
