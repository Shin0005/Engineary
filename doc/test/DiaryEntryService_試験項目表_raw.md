# DiaryEntryService 試験項目表

## 試験項目表: DiaryEntryService#getAllEntries

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | DBに複数件のデータがある場合、ページング結果が返る | pageable=page0,size10, DBに3件 | 3件のDiaryEntryResponseを含むPageが返る | - |
| 2 | 正常系 | DBにデータが1件だけある場合 | pageable=page0,size10, DBに1件 | 1件のDiaryEntryResponseを含むPageが返る | - |
| 3 | 準正常系 | DBにデータが0件の場合 | pageable=page0,size10, DBに0件 | 空のPageが返る | コンテンツが空リスト |
| 4 | 準正常系 | 2ページ目を指定した場合 | pageable=page1,size10, DBに11件 | 2ページ目のデータが返る | ページング境界 |
| 5 | 境界値系 | size=1で取得した場合 | pageable=page0,size1, DBに3件 | 1件のみ含むPageが返る（totalElements=3） | 最小サイズ |

---

## 試験項目表: DiaryEntryService#createDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 6 | 正常系 | 全フィールドが有効な値の場合、保存されてレスポンスが返る | title="テスト", contents="内容", workedTime=60, workedDate=2024-01-01 | idが付与されたDiaryEntryResponseが返る | - |
| 7 | 正常系 | contentsがnullの場合でも保存できる | title="テスト", contents=null, workedTime=30, workedDate=2024-01-01 | contents=nullのDiaryEntryResponseが返る | contentsはオプション |
| 8 | 正常系 | saveが呼ばれてリポジトリに委譲されること | 有効なrequest | diaryEntryRepository.saveが1回呼ばれる | Mockitoで検証 |
| 9 | 異常系 | repositoryがRuntimeExceptionをスローした場合 | 有効なrequest | RuntimeExceptionが伝播する | DB障害想定 |
| 10 | 境界値系 | titleが100文字（最大値ちょうど）の場合 | title=100文字の文字列 | 正常に保存されてレスポンスが返る | 上限境界値 |

---

## 試験項目表: DiaryEntryService#updateDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 11 | 正常系 | 存在するidを指定して更新した場合、各フィールドが更新される | id=1L（DBに存在）, 有効なrequest | エンティティの各フィールドが更新されsaveが呼ばれる | - |
| 12 | 正常系 | 戻り値がvoidであること | id=1L（DBに存在）, 有効なrequest | 例外がスローされない（assertDoesNotThrow） | - |
| 13 | 異常系 | 存在しないidを指定した場合 | id=999L（DBに存在しない） | ResourceNotFoundExceptionがスローされる | - |
| 14 | 異常系 | idがnullの場合 | id=null | 例外がスローされる | JPA依存 |
| 15 | 境界値系 | id=1（最小の正のid）の場合 | id=1L（DBに存在） | 正常に更新される | 最小id |
| 16 | 準正常系 | contentsをnullに更新する場合 | id=1L（DBに存在）, contents=null | contents=nullで保存される | null上書き |

---

## 試験項目表: DiaryEntryService#deleteDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 17 | 正常系 | 存在するidを指定した場合、deleteが呼ばれる | id=1L（DBに存在） | diaryEntryRepository.deleteが1回呼ばれる | - |
| 18 | 正常系 | 戻り値がvoidであること | id=1L（DBに存在） | 例外がスローされない（assertDoesNotThrow） | - |
| 19 | 異常系 | 存在しないidを指定した場合 | id=999L（DBに存在しない） | ResourceNotFoundExceptionがスローされる | - |
| 20 | 境界値系 | id=1（最小の正のid）の場合 | id=1L（DBに存在） | 正常に削除される | 最小id |
| 21 | 準正常系 | 同じidで2回deleteを呼んだ場合 | id=1L（1回目は存在、2回目は存在しない） | 2回目はResourceNotFoundExceptionがスローされる | 冪等性確認 |
