import { loadDiary, editDiaryEntry, deleteDiaryEntry } from './views/diary/diary-crud.js';
import { validForm } from './views/diary/diary-validator.js';
import './components/modal.js';
import { updatePaginationUI } from './components/pagination.js';

console.log("REST API 連携の準備ができました。");

let currentPage = 0;
// サイト読み込み時に日誌一覧取得
window.onload = function () {
    refreshDiary(currentPage);
};

// ボタンイベント初期化
// ページ表示 -> DOMtreeが読み込まれたらボタンにイベント追加
document.addEventListener('DOMContentLoaded', () => {

    // モーダルの保存ボタンクリックで保存メソッド実行
    const saveForm = document.getElementById('diary-form');
    saveForm.addEventListener('submit', (event) => {
        // submitによって勝手にロードされるので妨害
        event.preventDefault();

        const id = document.getElementById('diaryModal').dataset.currentId;
        // IDがあればPUT、なければPOST
        const method = id ? 'PUT' : 'POST';
        const url = id ? `/api/diary/${id}` : '/api/diary';

        // formの入力チェック合格後にapi通信
        if (validForm() === true) {
            editDiaryEntry(url, method)
        }
    });

    // deleteボタンクリックで削除メソッド実行
    const listElement = document.getElementById('diary-list');
    listElement.addEventListener('click', (event) => {
        const target = event.target;
        const id = target.dataset.id;
        if (target.classList.contains('btn-delete')) {
            deleteDiaryEntry(id);
        }
    });


    // ページングのイベント登録
    // 前へボタン
    const prevBtn = document.getElementById("prev-page");
    prevBtn.addEventListener('click', () => {
        refreshDiary(currentPage - 1)
    })
    // 次へボタン
    const nextBtn = document.getElementById("next-page");
    nextBtn.addEventListener('click', () => {
        refreshDiary(currentPage + 1);
    })

});

// ページング処理
export async function refreshDiary(page = 0) {
    currentPage = page;

    const pageData = await loadDiary(currentPage);

    // 現在のページが空かつ現在のページが最初ではない場合にひとつ前のページに戻る
    if (pageData.content.length === 0 && page > 0) {
        await refreshDiary(currentPage - 1);
        return;
    }
    // ボタンの状態を更新
    updatePaginationUI(pageData.page);
}

export function getCurrentPage() {
    return currentPage;
}



