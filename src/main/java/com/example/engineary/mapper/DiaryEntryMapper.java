package com.example.engineary.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.engineary.dto.DiaryEntryRequest;
import com.example.engineary.dto.DiaryEntryResponse;
import com.example.engineary.model.DiaryEntry;

/**
 * 入出力処理用DTOとEntity(DiaryEntry)のMapper
 */
public class DiaryEntryMapper {
    /**
     * Request -> Entity の変換メソッド
     * 
     * @param req DiaryEntryRequest
     */
    public static DiaryEntry toEntity(DiaryEntryRequest request) {
        DiaryEntry entity = new DiaryEntry();

        entity.setTitle(request.getTitle());
        entity.setContents(request.getContents());
        entity.setWorkedTime(Double.valueOf(request.getWorkedTime()));
        entity.setWorkedDate(request.getWorkedDate());
        return entity;
    }

    /**
     * Entity -> Response の変換メソッド
     * 
     * @param entity DiaryEntry
     * @return DiaryEntryResponse
     */
    public static DiaryEntryResponse toResponse(DiaryEntry entity) {
        DiaryEntryResponse response = new DiaryEntryResponse();

        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setContents(entity.getContents());
        response.setWorkedTime(entity.getWorkedTime());
        response.setWorkedDate(entity.getWorkedDate());

        return response;
    }

    /**
     * List<Entity> -> List<Response> の変換メソッド
     * 
     * @param entities List<DiaryEntry>
     * @return List<DiaryEntryResponse>
     */
    public static List<DiaryEntryResponse> toListResponse(List<DiaryEntry> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        List<DiaryEntryResponse> responses = new ArrayList<>();
        for (DiaryEntry entity : entities) {
            responses.add(toResponse(entity));
        }
        return responses;
    }
}
