import { initDiaryModal } from './diary-modal.js';
import { validDiaryForm } from './diary-validator.js';
import { editDiaryEntry, deleteDiaryEntry, loadDiary } from './diary-crud.js';
import { renderDiaryTable, hideDiaryModal } from './diary-ui.js';

/**
 * diary機能の初期化
 * テーブル・モーダル・ボタンイベントをまとめて登録する
 * @param {Object}   callbacks
 * @param {Function} callbacks.onSaved   保存成功時 (msg) => void
 * @param {Function} callbacks.onDeleted 削除成功時 () => void
 * @param {Function} callbacks.onError   エラー時   (msg) => void
 * @returns {Function} refresh(page) - テーブル再描画関数
 */
export function initDiary({ onSaved, onDeleted, onError }) {

    // モーダル初期化
    initDiaryModal();

    // 保存ボタン
    initSaveBtn({ onSaved, onError });

    // 削除ボタン
    initDeleteBtn({ onDeleted, onError });

    // refresh関数を返す（app.jsのrefreshWindowが呼び出す）
    return async (page) => {
        const pageData = await loadDiary(page);
        renderDiaryTable(pageData);
        return pageData;
    };
}

/**
 * テーブル保存ボタン初期化関数\
 * 通知タイミングはapp.jsに委譲
 * @param {*} { onSaved, onError } コールバック関数
 */
function initSaveBtn({ onSaved, onError }) {
    let isSubmitting = false;
    const saveForm = document.getElementById('diary-form');
    const saveBtn = document.getElementById('diary-btn-save');
    if (!saveForm || !saveBtn) return;
    // 多重登録防止フラグ
    if (saveForm.dataset.initialized) return;
    saveForm.dataset.initialized = 'true';


    saveForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (isSubmitting) return;
        if (!validDiaryForm()) return;

        const id = document.getElementById('diaryModal').dataset.currentId;

        try {
            isSubmitting = true;
            saveBtn.disabled = true;

            await editDiaryEntry(id);
            hideDiaryModal();

            // 成功をapp.jsに通知（メッセージだけ渡す）
            const msg = id ? '日誌が更新されました' : '日誌が作成されました';
            onSaved(msg);

        } catch (error) {
            const msg = id ? '更新に失敗しました' : '作成に失敗しました';
            onError(msg);
            console.error(`${error.name}: ${error.message}`);
            error.errors?.forEach(err =>
                console.error(`${err.field}: ${err.reason}`)
            );
        } finally {
            isSubmitting = false;
            saveBtn.disabled = false;
        }
    });
}

/**
 * テーブル削除ボタン初期化関数\
 * 通知タイミングはapp.jsに委譲
 * @param {*} { onDeleted, onError } コールバック関数
 */
function initDeleteBtn({ onDeleted, onError }) {
    const listElement = document.getElementById('diary-list');
    if (!listElement) return;
    // 多重登録防止フラグ
    if (listElement.dataset.initialized) return;
    listElement.dataset.initialized = 'true';

    listElement.addEventListener('click', async (event) => {
        const target = event.target;
        if (!target.classList.contains('btn-delete')) return;
        if (!confirm("本当に削除しますか？")) return;

        const id = target.dataset.id;
        try {
            await deleteDiaryEntry(id);
            onDeleted(); // 成功をapp.jsに通知

        } catch (error) {
            onError('削除に失敗しました');
            console.error(`${error.name}: ${error.message}`);
        }
    });
}