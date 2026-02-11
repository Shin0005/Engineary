package com.example.engineary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 入力処理用のDTO<br>
 * フロントからの入力を受け取る型
 */
@Data
public class DiaryEntryRequest {
    /** タイトル */
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100字以内で入力してください")
    private String title;

    /** 内容 */
    private String contents;

    /** 作業時間 */
    private Integer workedTime;

    /** 作業日時 */
    @NotBlank
    private String workedDate;
}
