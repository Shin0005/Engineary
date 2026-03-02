package com.example.engineary.dto;

import jakarta.validation.constraints.NotBlank;
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

}
