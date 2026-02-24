import { loadDiary, editDiaryEntry, deleteDiaryEntry } from './views/diary/diary-crud.js';
import { validForm } from './views/diary/diary-validator.js';
import { initModal } from './components/modal.js';
import { updatePaginationUI } from './components/pagination.js';
import { renderDiaryTable, hideDiaryModal } from './views/diary/diary-ui.js';
import { showNotify } from './components/toast.js';

console.log("REST API 連携の準備ができました。");

let currentPage = 0;
// サイト読み込み時に日誌一覧取得
window.onload = function () {
    refreshDiary(currentPage);
};

// ボタンイベント初期化
// ページ表示 -> DOMtreeが読み込まれたらボタンにイベント追加
document.addEventListener('DOMContentLoaded', () => {
    // ***モーダルの初期化
    initModal();

    // ***モーダルの保存ボタンクリックのイベント登録
    const saveForm = document.getElementById('diary-form');
    saveForm.addEventListener('submit', async (event) => {
        // submitによって勝手にロードされるので妨害
        event.preventDefault();

        const id = document.getElementById('diaryModal').dataset.currentId;

        // formの入力チェック合格後にapi通信
        if (validForm() === true) {
            try {
                await editDiaryEntry(id);
                // modalを非表示
                hideDiaryModal();
                // toast表示
                const msg = method === 'PUT' ? '日誌が更新されました' : '日誌が作成されました';
                showNotify(msg)
                // テーブルを表示
                await refreshDiary(getCurrentPage());

            } catch (error) {
                const msg = method === 'PUT' ? '更新に失敗しました' : '作成に失敗しました';
                showNotify(msg, 'error');

                console.error(`${error.name}: ${error.message}`);
                // 複数のフィールドでのエラーも表示
                error.errors?.forEach(err => {
                    console.error(`${err.field}: ${err.reason}`);
                });
            }
        }
    });

    // ***deleteボタンクリックのイベント登録
    const listElement = document.getElementById('diary-list');
    listElement.addEventListener('click', async (event) => {
        const target = event.target;
        const id = target.dataset.id;

        if (target.classList.contains('btn-delete')) {
            try {
                await deleteDiaryEntry(id);
                // 日誌再読み込み
                await refreshDiary(getCurrentPage());
                // toast通知
                showNotify('削除に成功しました');

            } catch (error) {
                // toast通知
                showNotify('削除に失敗しました', 'error');
                // errorログ出力
                console.error(`${error.name}: ${error.message}`);
            }
        }

    });

    // ***ページングのイベント登録
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

/**
 * 指定ページの日誌データを再読み込みし、UIを更新する
 * @param {number} [page=0] 読み込むページ番号
 * @returns {Promise<void>}
 */
export async function refreshDiary(page = 0) {
    try {
        currentPage = page;

        const pageData = await loadDiary(currentPage);

        // 現在のページが空かつ現在のページが最初ではない場合にひとつ前のページに戻る
        if (pageData.content.length === 0 && page > 0) {
            await refreshDiary(page - 1);
            return;
        }
        // テーブルを表示
        renderDiaryTable(pageData);
        // ボタンの状態を更新
        updatePaginationUI(pageData.page);
    } catch (error) {
        showNotify('読み込みに失敗しました', 'error');
        console.error(`${error.name}: ${error.message}`);
    }
}

export function getCurrentPage() {
    return currentPage;
}



