package com.example.engineary.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 入力処理用のDTO<br>
 * フロントからの入力を受け取る型
 */
@Data
public class MemoRequest {
    /** タイトル */
    @NotBlank(message = "タイトルは必須です")
    @Size(max = 100, message = "タイトルは100字以内で入力してください")
    private String title;

    /** 内容 */
    @Size(max = 5000, message = "内容は5000文字以内で入力してください")
    private String contents;

    /** 作業時間 */
    @NotNull
    @Max(value = 1440, message = "作業時間は1440分以内で入力してください")
    @Min(value = 1, message = "作業時間は1分以上で入力してください")
    private Integer workedTime;

    /** 作業日時 */
    @NotNull
    private LocalDate workedDate;
}
