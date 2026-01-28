package com.example.engineary.DTO;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * 出力処理用のDTO
 * サイト構築に必要なデータを提供
 */
@Data
public class DiaryEntryResponse {
    /* 日誌id */
    private Long id;
    /* タイトル*/
    private String title;

    /* 内容 */
    private String contents;

    /* 作業時間 */
    private Double workedTime;

    /* 作業日時 デフォルトで当日*/
    private String workedDate;

    //表示しないのでDBで管理すればOK
    /* 最終更新日時 */
    //private LocalDateTime updatedAt;
    /* 作成日時 */
    //private LocalDateTime createdAt;

}
