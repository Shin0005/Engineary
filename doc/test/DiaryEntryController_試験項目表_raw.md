# 試験項目表: DiaryEntryController

## getAllEntries

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | デフォルトページで一覧取得できる | パラメータなし（page=0, size=10） | 200 OK + ページングデータ返却 | デフォルト動作 |
| 2 | 正常系 | 件数が0件の場合に空リストが返る | データなし状態 | 200 OK + content=[] | - |
| 3 | 準正常系 | 最終ページで取得できる | page=最終ページ番号 | 200 OK + last=true | - |

## createDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | 全フィールドが有効な値で作成できる | title="タイトル", contents="内容", workedTime=60, workedDate=有効な日付 | 201 Created + レスポンスBody | - |
| 2 | 正常系 | contentsがnull（任意項目）でも作成できる | title="タイトル", contents=null, workedTime=60, workedDate=有効な日付 | 201 Created | contentsはnull許容 |
| 3 | 異常系 | titleがブランクで作成するとエラーになる | title="" | 400 Bad Request | @NotBlank違反 |
| 4 | 異常系 | titleがnullで作成するとエラーになる | title=null | 400 Bad Request | @NotBlank違反 |
| 5 | 異常系 | workedTimeがnullで作成するとエラーになる | workedTime=null | 400 Bad Request | @NotNull違反 |
| 6 | 異常系 | workedDateがnullで作成するとエラーになる | workedDate=null | 400 Bad Request | @NotNull違反 |
| 7 | 境界値系 | titleが100文字（上限）で作成できる | title="a"×100 | 201 Created | @Size(max=100) 境界値MAX |
| 8 | 境界値系 | titleが101文字（上限+1）でエラーになる | title="a"×101 | 400 Bad Request | @Size(max=100) 境界値MAX+1 |
| 9 | 境界値系 | workedTimeが1440（上限）で作成できる | workedTime=1440 | 201 Created | @Max(1440) 境界値MAX |
| 10 | 境界値系 | workedTimeが1441（上限+1）でエラーになる | workedTime=1441 | 400 Bad Request | @Max(1440) 境界値MAX+1 |
| 11 | 境界値系 | contentsが5000文字（上限）で作成できる | contents="あ"×5000 | 201 Created | @Size(max=5000) 境界値MAX |
| 12 | 境界値系 | contentsが5001文字（上限+1）でエラーになる | contents="あ"×5001 | 400 Bad Request | @Size(max=5000) 境界値MAX+1 |

## updateDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | 正常な入力で200が返る | id=1, 有効なRequest | 200 OK | - |
| 2 | 異常系 | ServiceがResourceNotFoundExceptionをスローしたとき404が返る | id=999（Serviceがスロー） | 404 Not Found | 例外→レスポンス変換の確認 |
| 3 | 異常系 | リクエストのtitleがブランクで400が返る | id=1, title="" | 400 Bad Request | @NotBlank違反 |

## deleteDiaryEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | 正常なIDで204が返る | id=1 | 204 No Content | - |
| 2 | 異常系 | ServiceがResourceNotFoundExceptionをスローしたとき404が返る | id=999（Serviceがスロー） | 404 Not Found | 例外→レスポンス変換の確認 |