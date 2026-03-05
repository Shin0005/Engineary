# 試験項目表: DiaryEntryMapper

## toEntity(DiaryEntryRequest)

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | 全フィールドが有効な値の場合 | title="テスト", contents="内容", workedTime=60, workedDate=2024-01-01 | 各フィールドが正しくマッピングされたEntityが返る | - |
| 2 | 正常系 | contentsがnullの場合 | contents=null, 他は有効 | contents=nullのEntityが返る | nullableフィールド |
| 3 | 異常系 | requestがnullの場合 | request=null | NullPointerExceptionがスローされる | - |
| 4 | 境界値系 | titleが1文字の場合 | title="a" | title="a"のEntityが返る | 最小長 |
| 5 | 境界値系 | titleが100文字の場合 | title=100文字 | title=100文字のEntityが返る | 最大長 |
| 6 | 準正常系 | contentsが空文字の場合 | contents="" | contents=""のEntityが返る | 空文字 |

## toResponse(DiaryEntry)

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 7 | 正常系 | 全フィールドが有効な値の場合 | id=1L, title="テスト", contents="内容", workedTime=60, workedDate=2024-01-01 | 各フィールドが正しくマッピングされたResponseが返る | - |
| 8 | 正常系 | contentsがnullの場合 | contents=null, 他は有効 | contents=nullのResponseが返る | - |
| 9 | 異常系 | entityがnullの場合 | entity=null | NullPointerExceptionがスローされる | - |
| 10 | 境界値系 | workedTimeが1の場合 | workedTime=1 | workedTime=1のResponseが返る | 最小値 |
| 11 | 境界値系 | workedTimeが1440の場合 | workedTime=1440 | workedTime=1440のResponseが返る | 最大値 |
| 12 | 準正常系 | idがnullの場合 | id=null, 他は有効 | id=nullのResponseが返る | 未保存Entity |

## toListResponse(List<DiaryEntry>)

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 13 | 正常系 | 複数要素のリストを渡した場合 | 3件のEntity | 3件のResponseリストが返る | - |
| 14 | 正常系 | 1件のリストを渡した場合 | 1件のEntity | 1件のResponseリストが返る | - |
| 15 | 異常系 | リストがnullの場合 | null | 空リストが返る | null安全 |
| 16 | 境界値系 | 空リストを渡した場合 | [] | 空リストが返る | サイズ0 |
| 17 | 準正常系 | contentsがnullのEntityを含むリストを渡した場合 | contents=nullのEntity1件 | contents=nullのResponseを含むリストが返る | - |
