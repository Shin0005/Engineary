# 試験項目表: MemoEntryRepository（Entity検証）

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | 全項目を指定して保存した場合、IDが自動採番される | title="test", contents="abc" | 保存後のidがnullでない | `@GeneratedValue` の検証 |
| 2 | 正常系 | saveした場合、createdAtが自動セットされる | 有効なエンティティ | createdAtがnullでない | `@CreatedDate` の検証 |
| 3 | 正常系 | saveした場合、updatedAtが自動セットされる | 有効なエンティティ | updatedAtがnullでない | `@UpdateTimestamp` の検証 |
| 4 | 正常系 | 更新した場合、updatedAtが更新前より新しくなる | 保存済みエンティティのtitleを変更してsave | updatedAt > 保存時のupdatedAt | `@UpdateTimestamp` の更新検証 |
| 5 | 正常系 | 更新した場合、createdAtは変化しない ※1 | 保存済みエンティティのtitleを変更してsave | createdAt == 保存時のcreatedAt | `updatable = false` の検証 |
| 6 | 異常系 | titleにnullを指定して保存した場合、例外がスローされる | title=null | 例外がスローされる | `@Column(nullable = false)` の検証 |

※1 refresh() でDBから再読み込みするとSQLiteのミリ秒精度に丸められ、値が変わってしまう。そのため、比較前にナノ秒を切り捨てる。

※ repositoryにメソッドはないのでEntityの試験になる。また、@系の試験はSQLite環境で動くかの試験である。