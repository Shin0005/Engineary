export function renderDiaryTable(pageData) {
    // tbody(diary-list)の取得
    const listElement = document.getElementById('diary-list');
    // 初期化して、取得したentitiesを代入
    listElement.innerHTML = '';
    pageData.content.forEach(entity => {
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

}

export function hideDiaryModal() {
    // モーダルを閉じる処理を追加
    const modalElement = document.getElementById('diaryModal');
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    if (modalInstance) {
        modalInstance.hide();
    }
}