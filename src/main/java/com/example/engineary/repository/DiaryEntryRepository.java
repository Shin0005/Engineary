package com.example.engineary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.engineary.model.DiaryEntry;

/**
 * 日誌レポジトリーインターフェース
 */
@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Long>{
    //現在hibernate+Jpaを使っているが、この程度のクエリを生成できないなら将来的にMybatisを使いたい。
    @Query("select d.id from DiaryEntry d")
    public List<Long> findIdList();

    public List<DiaryEntry> findByIdIn(List<Long> ids);
}
