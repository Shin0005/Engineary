import { apiFetch } from '../../apifetch.js';

/**
 * 日誌一覧データを取得する
 * @param {number} page  取得するページ番号
 * @returns {Promise<Object>} ページング情報を含むレスポンス
 */
export async function loadDiary(page) {
    const response = await apiFetch(`/api/diary?page=${page}`, {});
    // ページ情報返却
    return response;
}

/**
 * 日誌の登録・更新を行う
 * @param {string} url APIエンドポイント
 * @param {string} method HTTPメソッド (POST または PUT)
 * @returns {Promise<void>}
 */
export async function editDiaryEntry(id = null) {
    const data = {
        title: document.getElementById('diary-title').value.trim(),
        contents: document.getElementById('diary-contents').value.trim(),
        workedTime: document.getElementById('diary-workedTime').value.trim(),
        workedDate: document.getElementById('diary-workedDate').value
    };
    // IDがあればPUT、なければPOST
    const method = id ? 'PUT' : 'POST';
    const url = id ? `/api/diary/${id}` : '/api/diary';

    await apiFetch(url, {
        method: method,
        body: data
    });
}

/**
 * 指定したIDの日誌を削除する
 * @param {number|string} id 削除対象の日誌ID
 * @returns {Promise<void>}
 */
export async function deleteDiaryEntry(id) {
    await apiFetch(`/api/diary/${id}`, {
        method: 'DELETE'
    });
}



