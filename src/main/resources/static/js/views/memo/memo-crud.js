import { apiFetch } from '../../apifetch.js';

/**
 * メモ一覧データを取得する
 * @param {number} page  取得するページ番号
 * @returns {Promise<Object>} ページング情報を含むレスポンス
 */
export async function loadMemo(page) {
    const response = await apiFetch(`/api/memo?page=${page}`, {});
    // ページ情報返却
    return response;
}

/**
 * メモの登録・更新を行う
 * @param {string} url APIエンドポイント
 * @param {string} method HTTPメソッド (POST または PUT)
 * @returns {Promise<void>}
 */
export async function editMemo(id = null) {
    const data = {
        title: document.getElementById('memo-title').value.trim(),
        contents: document.getElementById('memo-contents').value.trim()
    };
    // IDがあればPUT、なければPOST
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/memo/${id}` : '/api/memo';

    await apiFetch(url, {
        method: method,
        body: data
    });
}

/**
 * 指定したIDのメモを削除する
 * @param {number|string} id 削除対象のメモID
 * @returns {Promise<void>}
 */
export async function deleteMemo(id) {
    await apiFetch(`/api/memo/${id}`, {
        method: 'DELETE'
    });
}



