package com.example.engineary.dto;

import lombok.Data;

/**
 * 出力処理用のDTO<br>
 * apiからの出力をフロントに返す型
 */
@Data
public class DiaryEntryResponse {
    /** 日誌id */
    private Long id;

    /** タイトル */
    private String title;

    /** 内容 */
    private String contents;

    /** 作業時間 */
    private Double workedTime;

    /** 作業日時 デフォルトで当日 */
    private String workedDate;
}
