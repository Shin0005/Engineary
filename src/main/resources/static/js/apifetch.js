// 引数から情報を取り出す(必要に合わせて加工)。取り出した情報をfetchに入れる。例外処理を行う。
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
            error.status = result.status;
            error.message = result.detail;
            error.name = result.title;
            error.errors = result.errors;
            throw error;
        }

        return result;

    } catch (error) {
        // ネットワーク系エラー
        if (!error.status) {
            // 通信不能を意味する独自のコード
            error.status = "000";
            error.name = "NetworkError";
        }
        // 呼び出し元にerror投げる
        throw error;
    }

}