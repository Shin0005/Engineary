import { apiFetch } from '../../apifetch.js';
import { showNotify } from '../../components/toast.js'
import { refreshDiary, getCurrentPage } from '../../app.js';

// selectAll
export async function loadDiary(page) {
    try {
        const response = await apiFetch(`/api/diary?page=${page}`, {});

        // tbody(diary-list)の取得
        const listElement = document.getElementById('diary-list');
        // 初期化して、取得したentitiesを代入
        listElement.innerHTML = '';
        response.content.forEach(entity => {
            const row = `
                <tr>
                    <td>${entity.workedDate}</td>
                    <td>${entity.title}</td>
                    <td>${entity.contents}</td>
                    <td>${entity.workedTime}</td>
                    <td>
                        <button class="btn btn-sm btn-primary" 
                            data-bs-toggle="modal" 
                            data-bs-target="#diaryModal" 
                            data-mode="edit" 
                            data-id="${entity.id}"
                            data-title="${entity.title}"
                            data-contents="${entity.contents}"
                            data-date="${entity.workedDate}"
                            data-time="${entity.workedTime}">編集</button>
                        <button class="btn btn-sm btn-danger btn-delete" data-id="${entity.id}">削除</button>
                    </td>
                </tr>`;
            listElement.insertAdjacentHTML('beforeend', row);
        })
        // ページ情報返却
        return response;


    } catch (error) {
        showNotify('読み込みに失敗しました', 'error');
        console.error(`${error.status} ${error.name}: ${error.message}`);
    }
}


// createAndupdateメソッド
export async function editDiaryEntry(url, method) {
    const data = {
        title: document.getElementById('diary-title').value,
        contents: document.getElementById('diary-contents').value,
        workedTime: document.getElementById('diary-workedTime').value,
        workedDate: document.getElementById('diary-workedDate').value
    };


    try {
        await apiFetch(url, {
            method: method,
            body: data
        });

        // モーダルを閉じる処理を追加
        const modalElement = document.getElementById('diaryModal');
        const modalInstance = bootstrap.Modal.getInstance(modalElement);
        if (modalInstance) {
            modalInstance.hide();
        }

        const msg = method === 'PUT' ? '日誌が更新されました' : '日誌が作成されました';
        showNotify(msg)

        await refreshDiary(getCurrentPage());
    } catch (error) {
        const msg = method === 'PUT' ? '更新に失敗しました' : '作成に失敗しました';
        showNotify(msg, 'error');

        console.error(error.status + " " + error.name + ": " + error.message);
        // 複数のフィールドでのエラーも表示
        error.errors?.forEach(err => {
            console.error(`${err.field}: ${err.reason}`);
        });
    }
}

// deleteメソッド 
export async function deleteDiaryEntry(id) {
    try {
        await apiFetch(`/api/diary/${id}`, {
            method: 'DELETE'
        });

        // 日誌再読み込み
        await refreshDiary(getCurrentPage());
        //toast通知
        showNotify('削除に成功しました');

    } catch (error) {
        showNotify('削除に失敗しました', 'error');
        console.error(`${error.status} ${error.name}: ${error.message}`);
    }
}



