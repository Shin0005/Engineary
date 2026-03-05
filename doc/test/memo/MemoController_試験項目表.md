# 試験項目表: MemoEntryController

## 試験の観点

Controllerが担う責務は主に以下の3つです。

① HTTPリクエストの受け取り
正しいURLとHTTPメソッド（GET / POST / PUT / DELETE）でエンドポイントにアクセスしたとき、適切なハンドラメソッドが呼ばれるかを確認します。

② バリデーション
@Valid によるリクエストボディの入力チェックが正しく動いているかを確認します。不正な値が来たときに400 Bad Requestが返り、Serviceが呼ばれないことも検証します。今回の試験項目表でいう「異常系・境界値系」がこれに当たります。

③ HTTPレスポンスの返却
Serviceの処理結果をもとに、正しいステータスコード（200 / 201 / 204 / 404 など）とレスポンスBodyが返っているかを確認します。

## getAllEntries

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 1 | 正常系 | デフォルトページで一覧取得できる | パラメータなし（page=0, size=10） | 200 OK + ページングデータ返却 | デフォルト動作 |
| 2 | 正常系 | 件数が0件の場合に空リストが返る | データなし状態 | 200 OK + content=[] | - |
| 3 | 準正常系 | 最終ページで取得できる | page=最終ページ番号 | 200 OK + last=true | - |

## createMemoEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 4 | 正常系 | 全フィールドが有効な値で作成できる | title="タイトル", contents="内容" | 201 Created + レスポンスBody | - |
| 5 | 正常系 | contentsがnull（任意項目）でも作成できる | title="タイトル", contents=null | 201 Created | contentsはnull許容 |
| 6 | 異常系 | titleがブランクで作成するとエラーになる | title="" | 400 Bad Request | @NotBlank違反 |
| 7 | 異常系 | titleがnullで作成するとエラーになる | title=null | 400 Bad Request | @NotBlank違反 |
| 8 | 境界値系 | titleが100文字（上限）で作成できる | title="a"×100 | 201 Created | @Size(max=100) 境界値MAX |
| 9 | 境界値系 | titleが101文字（上限+1）でエラーになる | title="a"×101 | 400 Bad Request | @Size(max=100) 境界値MAX+1 |
| 10 | 境界値系 | contentsが5000文字（上限）で作成できる | contents="あ"×5000 | 201 Created | @Size(max=5000) 境界値MAX |
| 11 | 境界値系 | contentsが5001文字（上限+1）でエラーになる | contents="あ"×5001 | 400 Bad Request | @Size(max=5000) 境界値MAX+1 |

## updateMemoEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 12 | 正常系 | 正常な入力で200が返る | id=1, 有効なRequest | 200 OK | - |
| 13 | 異常系 | ServiceがResourceNotFoundExceptionをスローしたとき404が返る | id=999（Serviceがスロー） | 404 Not Found | 例外→レスポンス変換の確認 |
| 14 | 異常系 | リクエストのtitleがブランクで400が返る | id=1, title="" | 400 Bad Request | @NotBlank違反 |

## deleteMemoEntry

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 15 | 正常系 | 正常なIDで204が返る | id=1 | 204 No Content | - |
| 16 | 異常系 | ServiceがResourceNotFoundExceptionをスローしたとき404が返る | id=999（Serviceがスロー） | 404 Not Found | 例外→レスポンス変換の確認 |