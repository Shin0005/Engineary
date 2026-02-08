const modal = document.getElementById("diaryModal");
//モーダルを閉じるタイミングの処理
modal.addEventListener("hidden.bs.modal", () => {
    const form = document.getElementById('diary-form');
    
    // 入力値のクリア
    form.reset(); 
    
    // すべてのバリデーションエラーの除去
    const inputs = form.querySelectorAll('.is-invalid');
    inputs.forEach(input => input.classList.remove('is-invalid'));
    
    // 編集用IDの削除
    delete modal.dataset.currentId;
});

//モーダルを開けたタイミングの処理
modal.addEventListener("show.bs.modal", (event) => {
    const button = event.relatedTarget;
    const mode = button.getAttribute("data-mode");

    const titleInput = document.getElementById('diary-title');
    const contentsInput = document.getElementById('diary-contents');
    const timeInput = document.getElementById('diary-workedTime');
    const dateInput = document.getElementById('diary-workedDate');
    const modalTitle = modal.querySelector(".modal-title");

    if (mode === "create") {
        modalTitle.textContent = "新規作成";
        // 今日の日付をセット
        dateInput.value = new Date().toISOString().split('T')[0];

    } else if (mode === "edit") {
        modalTitle.textContent = "編集";
        // ボタンに仕込んだ data-属性から値をセット
        titleInput.value = button.getAttribute("data-title");
        contentsInput.value = button.getAttribute("data-contents");
        timeInput.value = button.getAttribute("data-time");
        dateInput.value = button.getAttribute("data-date");
        // 現在編集中のIDをモーダル自体に覚えさせる
        modal.dataset.currentId = button.getAttribute("data-id");
    }


});