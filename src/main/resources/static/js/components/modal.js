/**
 * モーダルの汎用初期化関数
 * フォームリセット・バリデーションクリア・currentIdの削除のみ行う
 * @param {string} modalId モーダル要素のID
 * @param {string} formId  フォーム要素のID
 */
export function initModal(modalId, formId) {
    const modal = document.getElementById(modalId);
    const form = document.getElementById(formId);
    if (!modal || !form) return;

    modal.addEventListener("hidden.bs.modal", () => {
        // 入力値のクリア
        form.reset();
        // バリデーションエラーの除去
        form.querySelectorAll('.is-invalid')
            .forEach(el => el.classList.remove('is-invalid'));
        // 編集用IDの削除
        delete modal.dataset.currentId;
    });
}