package com.example.engineary.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@Table(name = "entries")
public class DiaryEntry {
    /* 投稿ID 日付で生成*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* タイトル*/
    @NotBlank(message = "タイトルは必須です")
    private String title;

    /* tag 別テーブルから参照*/
    //private Set<Tag> tag;

    /* 内容 */
    private String contents;

    /* 作業時間 */
    private Double workedTime;

    /* 作業日時 デフォルトで当日*/
    @CreatedDate
    private LocalDate workedDate;

    /* 最終更新日時 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* 作成日時 */
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DiaryEntry(Long id, String title, String contents, Double workedTime, LocalDate workedDate, LocalDateTime updatedAt, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.workedTime = workedTime;
        this.workedDate = workedDate;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
    }
    //JPA用の空コンストラクタ
    protected DiaryEntry(){
    }

    //getter setterはlombokにより省略

    


}
