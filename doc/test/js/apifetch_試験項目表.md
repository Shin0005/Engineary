# フロントエンド ユニットテスト 試験項目表

## apiFetch（apifetch.js）

### 正常系

| No. | テスト内容 | 入力値 | 期待結果 |
|-----|-----------|--------|---------|
| 1 | GETリクエストでJSONレスポンスが返る | url: "/api/memo", options: {} | `{ id: 1, title: "テスト" }` が返る |
| 2 | デフォルトメソッドがGETである | options: {} | `fetch` が `method: 'GET'` で呼ばれる |
| 3 | Content-Typeヘッダーが自動付与される | options: {} | `'Content-Type': 'application/json'` が付与される |
| 4 | POSTリクエストでbodyがJSON文字列化される | method: "POST", body: `{ title: "タイトル" }` | `config.body` が `JSON.stringify(body)` になる |
| 5 | カスタムヘッダーがマージされる | headers: `{ 'X-Custom': 'value' }` | `Content-Type` と `X-Custom` が両方付与される |
| 6 | DELETEリクエストが正常に実行される | url: "/api/memo/1", method: "DELETE" | `fetch` が `method: 'DELETE'` で呼ばれ `{}` が返る |

### 異常系

| No. | テスト内容 | 入力値 | 期待結果 |
|-----|-----------|--------|---------|
| 7 | 400レスポンスの場合、Errorがスローされる | response: `{ ok: false, status: 400 }` | `Error` がスローされる |
| 8 | 400レスポンスの場合、error.messageにdetailが設定される | response.detail: "リクエストが正しくありません" | `error.message` が `"リクエストが正しくありません"` になる |
| 9 | 400レスポンスの場合、error.nameにtitleが設定される | response.title: "Bad Request" | `error.name` が `"Bad Request"` になる |
| 10 | 400レスポンスにerrorsが含まれる場合、error.errorsに設定される | response.errors: `[{ field: "title", reason: "必須です" }]` | `error.errors` に配列が設定される |
| 11 | 404レスポンスの場合、Errorがスローされる | response: `{ ok: false, status: 404 }` | `Error` がスローされる |
| 12 | 500レスポンスの場合、Errorがスローされる | response: `{ ok: false, status: 500 }` | `Error` がスローされる |
| 13 | ネットワークエラーの場合、Errorがスローされる | fetch が `TypeError('Failed to fetch')` を投げる | `'Failed to fetch'` エラーがスローされる |

### 境界値系

| No. | テスト内容 | 入力値 | 期待結果 |
|-----|-----------|--------|---------|
| 14 | bodyがnullの場合、config.bodyが設定されない | body: null | `config.body` が `undefined` になる |
| 15 | bodyがstring型の場合、JSON.stringifyされない | body: `'{"title":"test"}'` | `config.body` が `undefined` になる（文字列はスキップ） |
| 16 | bodyがオブジェクトの場合、JSON.stringifyされる | body: `{ title: "テスト" }` | `config.body` が `JSON.stringify(body)` になる |

### 準正常系

| No. | テスト内容 | 入力値 | 期待結果 |
|-----|-----------|--------|---------|
| 17 | レスポンスのJSONパースに失敗した場合 | `response.json()` が失敗 (ok: true) | `{}` が返る |
| 18 | エラーレスポンスのJSONパース失敗時 | `response.json()` が失敗 (ok: false) | `error.name` が `"HttpError"`、`message` と `errors` が `undefined` のエラーがスローされる |
| 19 | fetchが1回だけ呼ばれる | url: "/api/memo", options: {} | `fetch` の呼び出し回数が 1回 である |
