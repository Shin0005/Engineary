package com.example.engineary.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.engineary.dto.MemoRequest;
import com.example.engineary.dto.MemoResponse;
import com.example.engineary.model.Memo;

/**
 * 入出力処理用DTOとEntity(Memo)のMapper
 */
public class MemoMapper {
    /**
     * Request -> Entity の変換メソッド
     * 
     * @param req MemoRequest
     */
    public static Memo toEntity(MemoRequest request) {
        Memo entity = new Memo();

        entity.setTitle(request.getTitle());
        entity.setContents(request.getContents());
        entity.setWorkedTime(request.getWorkedTime());
        entity.setWorkedDate(request.getWorkedDate());
        return entity;
    }

    /**
     * Entity -> Response の変換メソッド
     * 
     * @param entity Memo
     * @return MemoResponse
     */
    public static MemoResponse toResponse(Memo entity) {
        MemoResponse response = new MemoResponse();

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
     * @param entities List<Memo>
     * @return List<MemoResponse>
     */
    public static List<MemoResponse> toListResponse(List<Memo> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        List<MemoResponse> responses = new ArrayList<>();
        for (Memo entity : entities) {
            responses.add(toResponse(entity));
        }
        return responses;
    }
}
