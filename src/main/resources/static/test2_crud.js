console.log("REST API 連携の準備ができました。");
//サイト読み込み時に日誌一覧取得
window.onload = function () {
    loadDiary();
};

//selectAll
async function loadDiary() {
    try {
        const entities = await apiFetch('/api/diary', { method: 'GET' });

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
                        <button class="btn btn-sm btn-danger btn-delete" data-id="${entity.id}">削除</button>
                    </td>
                </tr>`;
            listElement.insertAdjacentHTML('beforeend', row);
        })


    } catch (error) {
        console.error(error.message);
        //画面にも表示失敗した旨を表示したい。
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
    const saveForm = document.getElementById('diary-form');

    saveForm.addEventListener('submit', (event) => {
        // submitによって勝手にロードされるので妨害
        event.preventDefault();

        const id = document.getElementById('diaryModal').dataset.currentId;
        // IDがあればPUT、なければPOST
        const method = id ? 'PUT' : 'POST';
        const url = id ? `/api/diary/${id}` : '/api/diary';

        if (validForm() === true) {
            editDiaryEntry(url, method)
        }
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

        try {
            await apiFetch(url, {
                method: method,
                body: data
            });

            // モーダルを閉じる処理を追加
            const modalElement = document.getElementById('diaryModal');
            const modalInstance = bootstrap.Modal.getInstance(modalElement);
            if (modalInstance) {
                modalInstance.hide();
            }

            await loadDiary();
        } catch (error) {
            console.error(error.message);
            //画面にも表示失敗した旨を表示したい。
        }
    }

    //deleteメソッド 
    async function deleteDiaryEntry(id) {
        try {
            await apiFetch(`/api/diary/${id}`, {
                method: 'DELETE'
            });

            await loadDiary();

        } catch (error) {
            console.error(error.message);
            //画面にも表示失敗した旨を表示したい。
        }
    }

    //引数から情報を取り出す(必要に合わせて加工)。取り出した情報をfetchに入れる。例外処理を行う。
    async function apiFetch(url, { method = 'GET', headers = {}, body = null }) {
        //headersはbodyがStringなので固定(formでやるかも)

        const config = {
            method,
            headers: {
                'Content-Type': 'application/json',
                ...headers,
            }
        };

        // bodyがあるかつStringのみJSON文字列化して追加
        if (body && typeof body != 'string') {
            config.body = JSON.stringify(body);
        }
        //取り出した情報をfetchに入れて例外処理
        let response;
        try {
            response = await fetch(url, config);
        } catch (error) {
            //ネットワーク系エラー
            console.error(error.message)
            //呼び出し元にerror投げる
            throw new Error("Network Error");
        }

        //http系エラー 後で番号で分岐
        if (!response.ok) {
            const error = new Error("HttpError");
            error.status = response.status;
            throw error;
        }
        const contentType = response.headers.get('content-type');
        // bodyなし
        if (!contentType) {
            return;
        } else if (contentType.includes('application/json')) {
            return await response.json();
        }
        return await response.text();

    }
    //formのvalidation
    function validForm() {
        // タイトル：　入力必須，１００文字以内
        const titleInput = document.getElementById('diary-title');
        const titleValue = titleInput.value.trim();
        const titleError = document.getElementById("title-error");
        if (titleValue === "") {
            titleError.innerText = "タイトルは必須です。";
            titleInput.classList.add('is-invalid'); // 赤枠とメッセージを表示
            return false;
        } else if (titleValue.length > 100) {
            titleError.innerText = "100文字以内で入力してください。";
            titleInput.classList.add('is-invalid'); // 赤枠とメッセージを表示
            return false;
        } else {
            titleInput.classList.remove('is-invalid'); // エラーを消す
        }

        //作業時間：　入力形式(数値ONLY)
        const timeInput = document.getElementById('diary-workedTime');
        const timeValue = timeInput.value.trim();
        const timeError = document.getElementById("workedTime-error");
        const timeNum = Number(timeValue);
        //数値でない、または０以下、または整数でない
        if (isNaN(timeNum) || timeNum <= 0 || !Number.isInteger(timeNum)) {
            timeError.innerText = "作業時間は1分以上の整数で入力してください。";
            timeInput.classList.add('is-invalid');
            return false;
        }
        
        return true;
    }