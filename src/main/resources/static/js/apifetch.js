/**
 * 共通APIフェッチ関数
 * @param {string} url リクエストURL
 * @param {Object} options fetchオプション
 * @param {string} [options.method='GET'] HTTPメソッド
 * @param {Object} [options.headers={}] 追加ヘッダー
 * @param {Object|null} [options.body=null] リクエストボディ
 * @returns {Promise<Object>} JSONレスポンス
 * @throws {Error} HttpError or NetworkError
 */
export async function apiFetch(url, { method = 'GET', headers = {}, body = null }) {
    // 引数をfetch用に整理
    const config = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...headers,
        }
    };

    // bodyがStringのときJSON文字列化して追加
    if (body && typeof body != 'string') {
        config.body = JSON.stringify(body);
    }

    // 取り出した情報をもとにfetch
    try {
        const response = await fetch(url, config);
        // パース失敗したら{}を返す
        const result = await response.json().catch(() => ({}));

        // catchで統一的にエラー出力
        if (!response.ok) {
            // 呼び出し元にerrorをスロー
            const error = new Error("HttpError");
            error.message = result.detail;
            error.name = result.title;
            error.errors = result.errors;
            throw error;
        }

        return result;

    } catch (error) {
        // 呼び出し元にerror投げる
        throw error;
    }

}