import { initModal } from '../../components/modal.js';

/**
 * diaryモーダルの初期化
 * 汎用リセット処理 + diary固有の値セット
 */
export function initDiaryModal() {
    // 汎用リセット処理
    initModal('diaryModal', 'diary-form');

    const modal = document.getElementById('diaryModal');
    if (!modal) return;

    // diary固有：モーダルを開いたときの値セット
    modal.addEventListener("show.bs.modal", (event) => {
        const button = event.relatedTarget;
        const mode = button.getAttribute("data-mode");
        const modalTitle = modal.querySelector(".modal-title");

        if (mode === "create") {
            modalTitle.textContent = "新規作成";
            document.getElementById('diary-workedDate').value
                = new Date().toLocaleDateString('sv-SE');

        } else if (mode === "edit") {
            modalTitle.textContent = "編集";
            document.getElementById('diary-title').value = button.dataset.title;
            document.getElementById('diary-contents').value = button.dataset.contents;
            document.getElementById('diary-workedTime').value = button.dataset.time;
            document.getElementById('diary-workedDate').value
                = new Date(button.dataset.date).toLocaleDateString('sv-SE');
            modal.dataset.currentId = button.dataset.id;
        }
    });
}