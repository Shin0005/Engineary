import { showNotify } from './components/toast.js'

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
    let result;
    try {
        const response = await fetch(url, config);
        result = await response.json(); // error起こりうる？
    } catch (error) {
        // ネットワーク系エラー
        console.error(error.message)
        // 呼び出し元にerror投げる
        throw new Error("Network Error");
    }


    handleNotify(result, method, response.ok);

    const contentType = response.headers.get('content-type');

    if (!contentType) {
        return; // bodyなし
    } else if (contentType.includes('application/json')) {
        return result;
    }
    return await response.text();

}

// response.okとそうでない場合でtoast表示およびエラー表示を変更
async function handleNotify(result, method, response_ok) {
    // response.okの場合 
    if (response_ok) {
        let msg;
        switch (method) {
            case 'POST':
                msg = "作成が成功しました";
                break;
            case 'DELETE':
                msg = "削除に成功しました";
                break;
            case 'PUT':
                msg = "更新に成功しました"
                break;
            case 'GET':
                break;
        }
        if (method != 'GET') {
            showNotify(msg, "success");
        }

    } else {
        // http系エラー
        let msg;
        switch (result.status) {
            case 400:
                msg = "入力内容に誤りがあります。";
                break;
            case 404:
                msg = "通信先が見つかりません";
                break;
            default:
                msg = "サーバーで障害が起きています";
        }
        showNotify(msg, "error");

        // 呼び出し元にerrorをスロー
        const error = new Error("HttpError");
        error.status = result.status;
        error.message = result.detail;
        error.name = result.title;
        error.errors = result.errors;
        throw error;
    }
}