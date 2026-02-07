const modal = document.getElementById("diaryModal");
modal.addEventListener("show.bs.modal", (event) => {
    //押されたボタンを判別(bs特有)
    const button = event.relatedTarget;
    // ボタンのdate-modeを取得
    const mode = button.getAttribute("data-mode");

    const titleInput = document.getElementById('diary-title');
    const contentsInput = document.getElementById('diary-contents');
    const timeInput = document.getElementById('diary-workedTime');
    const dateInput = document.getElementById('diary-workedDate');
    const modalTitle = modal.querySelector(".modal-title");

    if (mode === "create") {

        modalTitle.textContent = "新規作成";
        // フォームを空にする
        titleInput.value = "";
        contentsInput.value = "";
        timeInput.value = "";
        // 今日の"YYYY-MM-DD" を取得して設定
        //api修正後に修正
        dateInput.value = "";
        //  new Date().toISOString().split('T')[0];
        delete modal.dataset.currentId;

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