console.log("REST API 連携の準備ができました。");
//サイト読み込み時に日誌一覧取得
window.onload = function(){
    loadDiary();
};

//selectAll
async function loadDiary(){
    try{
        const res = await fetch('/diary');
        const entities = await res.json();

        if (!res.ok) {
            alert('データ取得に失敗しました');
        } else {
            //tbody(diary-list)の取得
            const listElement = document.getElementById('diary-list');
            //初期化して、取得したentitiesを代入
            listElement.innerHTML = '';
            entities.forEach(entity => {
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
                            <button class="btn btn-sm btn-danger" data-id="${entity.id}">削除</button>
                        </td>
                    </tr>`;
                listElement.insertAdjacentHTML('beforeend',row);
            })
        }
        
    }catch(e){
        console.error(e);
    }
}

//DOMtreeが読み込まれたらボタンにイベント追加
document.addEventListener('DOMContentLoaded', () => {
    //delete
    const listElement = document.getElementById('diary-list');
    listElement.addEventListener('click', (event) => {
        const target = event.target;
        const id = target.dataset.id;

        if (target.classList.contains('btn-delete')) {
            deleteDiaryEntry(id);
        }
    });

    //create or update
    const saveBtn = document.getElementById('diary-btn-save');

    saveBtn.addEventListener('click', () => {
        const id = document.getElementById('diaryModal').dataset.currentId;
        // IDがあればPUT、なければPOST
        const method = id ? 'PUT' : 'POST';
        const url = id ? `/diary/${id}` : '/diary';

        editDiaryEntry(url, method)
        
    });
});

//createAndupdateメソッド
async function editDiaryEntry(url, method) {
    const data = {
            title: document.getElementById('diary-title').value,
            contents: document.getElementById('diary-contents').value,
            workedTime: document.getElementById('diary-workedTime').value,
            workedDate: document.getElementById('diary-workedDate').value
        };
    try{
        const response = await fetch(url, {
            method: method,
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            alert('保存に失敗しました');
        } else {
            location.reload();
        }
    }catch(e){
        console.error(e);
    }
}

//deleteメソッド 
async function deleteDiaryEntry(id){ 
    try{
        const response = await fetch(`/diary/${id}`,{
            method: 'DELETE'
        });
        if(!response.ok){
            alert('削除に失敗しました。');
        }else{
            location.reload();
        }
    }catch(e){ 
        //サーバー側エラーはここには来ないのでネットワークエラーが来る 
        //ネットワークエラーはエラーを投げずにありましたよと言っとけばいい。 
        console.error(e);
    }
} 