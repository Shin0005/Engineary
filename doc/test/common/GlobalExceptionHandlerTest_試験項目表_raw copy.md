# 試験項目表: GlobalExceptionHandler

## 対象クラス概要

`GlobalExceptionHandler` は `@RestControllerAdvice` として、発生した例外の種類に応じて適切な HTTPステータスと `ProblemDetail` レスポンスを返すクラス。  
各ハンドラメソッドが独立しているため、ハンドラ単位で試験を設計する。

---

## handleResourceNotFoundException

| No | 観点 | テスト項目 | 入力値（例外） | 期待結果 | 備考 |
|----|------|-----------|----------------|---------|------|
| 1 | 正常系 | ResourceNotFoundExceptionが発生した場合、404と固定メッセージを返す | `ResourceNotFoundException(1L)` | 404・detail=「指定されたリソースが見つかりませんでした。」 | - |

---

## handleValidationException

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 2 | 正常系 | バリデーション違反が1件の場合、400・errors件数が厳密に1件・field/reasonキーが含まれる | title=空・workedTime=30・workedDate=今日 | 400・detail=「リクエストが正しくありません」・errors件数=1・field=title・reasonが存在する | field/reasonキー確認を統合 |
| 3 | 正常系 | バリデーション違反が複数件の場合、errorsに複数件含まれる | title=空・workedTime=null・workedDate=今日 | 400・errors件数が2件以上 | - |

---

## handleHttpMessageNotReadableException

| No | 観点 | テスト項目 | 入力値 | 期待結果 | 備考 |
|----|------|-----------|--------|---------|------|
| 4 | 正常系 | 不正なJSON文字列が送られた場合、400とエラーメッセージを返す | `{ invalid json }` | 400・detail=「リクエストの形式が正しくありません」 | - |

---

## handleResponseStatusException

| No | 観点 | テスト項目 | 入力値（ステータスコード） | 期待結果 | 備考 |
|----|------|-----------|--------------------------|---------|------|
| 5 | 正常系 | 400のResponseStatusExceptionが発生した場合 | `ResponseStatusException(400)` | 400・detail=「不正なリクエストです」 | switch case 400 |
| 6 | 正常系 | 401のResponseStatusExceptionが発生した場合 | `ResponseStatusException(401)` | 401・detail=「認証が必要です」 | switch case 401 |
| 7 | 正常系 | 403のResponseStatusExceptionが発生した場合 | `ResponseStatusException(403)` | 403・detail=「アクセス権限がありません」 | switch case 403 |
| 8 | 正常系 | 404のResponseStatusExceptionが発生した場合 | `ResponseStatusException(404)` | 404・detail=「リソースが見つかりません」 | switch case 404 |
| 9 | 正常系 | switchに未定義のステータス(409)が発生した場合、defaultメッセージを返す | `ResponseStatusException(409)` | 409・detail=「エラーが発生しました」 | switch default節の検証。500はhandleSystemExceptionと競合するため409を使用 |

---

## handleSystemException

| No | 観点 | テスト項目 | 入力値（例外） | 期待結果 | 備考 |
|----|------|-----------|----------------|---------|------|
| 10 | 正常系 | 予期せぬExceptionが発生した場合、500とエラーメッセージを返す | `new Exception("unexpected")` | 500・detail=「予期しないエラーが発生しました。」 | - |

---

## 削除した項目とその理由

| 削除項目 | 理由 |
|----------|------|
| `ResourceNotFoundException(0L)` の準正常系 | ハンドラはIDの値を参照せず固定メッセージを返すため、`(1L)` と通るコードパスが同一。検証価値なし |
| `shouldContainFieldAndReasonInErrors` 準正常系 | 正常系（No.2）と入力が完全に同一。field/reasonの確認をNo.2に統合したため不要 |
| `handleResponseStatusException` 500テスト | `handleSystemException` の `Exception` ハンドラと競合し意図が不明確。409に変更してdefaultを明確に検証 |
