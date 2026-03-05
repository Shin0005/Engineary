package com.example.engineary.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 出力処理用のDTO<br>
 * apiからの出力をフロントに返す型
 */
@Data
public class MemoResponse {
    /** 日誌id */
    private Long id;

    /** タイトル */
    private String title;

    /** 内容 */
    private String contents;

    /** 最終更新日時 */
    private LocalDateTime updatedAt;
}
