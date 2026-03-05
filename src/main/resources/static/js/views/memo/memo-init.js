import { initMemoModal } from './memo-modal.js';
import { validMemoForm } from './memo-validator.js';
import { editMemo, deleteMemo, loadMemo } from './memo-crud.js';
import { renderMemoTable, hideMemoModal } from './memo-ui.js';

/**
 * memo機能の初期化
 * テーブル・モーダル・ボタンイベントをまとめて登録する
 * @param {Object}   callbacks
 * @param {Function} callbacks.onSaved   保存成功時 (msg) => void
 * @param {Function} callbacks.onDeleted 削除成功時 () => void
 * @param {Function} callbacks.onError   エラー時   (msg) => void
 * @returns {Function} refresh(page) - テーブル再描画関数
 */
export function initMemo({ onSaved, onDeleted, onError }) {

    // モーダル初期化
    initMemoModal();

    // 保存ボタン
    initSaveBtn({ onSaved, onError });

    // 削除ボタン
    initDeleteBtn({ onDeleted, onError });

    // refresh関数を返す（app.jsのrefreshWindowが呼び出す）
    return async (page) => {
        const pageData = await loadMemo(page);
        renderMemoTable(pageData);
        return pageData;
    };
}

/**
 * 保存ボタン初期化関数\
 * 通知タイミングはapp.jsに委譲
 * @param {*} { onSaved, onError } コールバック関数
 */
function initSaveBtn({ onSaved, onError }) {
    let isSubmitting = false;
    const saveForm = document.getElementById('memo-form');
    const saveBtn = document.getElementById('memo-btn-save');
    if (!saveForm || !saveBtn) return;
    // 多重登録防止フラグ
    if (saveForm.dataset.initialized) return;
    saveForm.dataset.initialized = 'true';

    saveForm.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (isSubmitting) return;
        if (!validMemoForm()) return;

        const id = document.getElementById('memoModal').dataset.currentId;

        try {
            isSubmitting = true;
            saveBtn.disabled = true;

            await editMemo(id);
            hideMemoModal();

            // 成功をapp.jsに通知（メッセージだけ渡す）
            const msg = id ? 'メモが更新されました' : 'メモが作成されました';
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
 * 削除ボタン初期化関数\
 * 通知タイミングはapp.jsに委譲
 * @param {*} { onDeleted, onError } コールバック関数
 */
function initDeleteBtn({ onDeleted, onError }) {
    const listElement = document.getElementById('memo-list');
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
            await deleteMemo(id);
            onDeleted(); // 成功をapp.jsに通知

        } catch (error) {
            onError('削除に失敗しました');
            console.error(`${error.name}: ${error.message}`);
        }
    });
}