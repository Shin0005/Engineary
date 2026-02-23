import { apiFetch } from '../../apifetch.js';

// selectAll
export async function loadDiary(page) {
    const response = await apiFetch(`/api/diary?page=${page}`, {});
    // ページ情報返却
    return response;
}

// createAndupdateメソッド
export async function editDiaryEntry(url, method) {
    const data = {
        title: document.getElementById('diary-title').value,
        contents: document.getElementById('diary-contents').value,
        workedTime: document.getElementById('diary-workedTime').value,
        workedDate: document.getElementById('diary-workedDate').value
    };

    await apiFetch(url, {
        method: method,
        body: data
    });
}

// deleteメソッド 
export async function deleteDiaryEntry(id) {
    await apiFetch(`/api/diary/${id}`, {
        method: 'DELETE'
    });
    return;
}



