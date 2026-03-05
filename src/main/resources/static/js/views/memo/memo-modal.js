import { initModal } from '../../components/modal.js';

/**
 * memoモーダルの初期化
 * 汎用リセット処理 + memo固有の値セット
 */
export function initMemoModal() {
    // 汎用リセット処理
    initModal('memoModal', 'memo-form');

    const modal = document.getElementById('memoModal');
    if (!modal) return;

    // memo固有：モーダルを開いたときの値セット
    modal.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget;
        const mode = button.getAttribute("data-mode");
        const modalTitle = modal.querySelector(".modal-title");

        if (mode === "create") {
            modalTitle.textContent = "新規作成";

        } else if (mode === "edit") {
            modalTitle.textContent = "編集";
            document.getElementById('memo-title').value = button.dataset.title;
            document.getElementById('memo-contents').value = button.dataset.contents;
            modal.dataset.currentId = button.dataset.id;
        }
    });
}