// modal-formのvalidation
/**
 * Formのバリデーションを行う
 * @returns {Boolean}
 */
export function validMemoForm() {
    let validFlg = true;

    // タイトル：　入力必須，１００文字以内
    const titleInput = document.getElementById('memo-title');
    if (!titleInput) return false;
    const titleValue = titleInput.value.trim();
    const titleError = document.getElementById("memo-title-error");
    if (!titleError) return false;

    if (titleValue === "") {
        titleError.innerText = "タイトルは必須です。";
        titleInput.classList.add('is-invalid'); // 赤枠とメッセージを表示
        validFlg = false;
    } else if (titleValue.length > 100) {
        titleError.innerText = "100文字以内で入力してください。";
        titleInput.classList.add('is-invalid'); // 赤枠とメッセージを表示
        validFlg = false;
    } else {
        titleInput.classList.remove('is-invalid'); // エラーを消す
    }

    // 内容：5000文字以内
    const contentsInput = document.getElementById('memo-contents');
    if (!contentsInput) return false;
    const contentsValue = contentsInput.value.trim();
    const contentsError = document.getElementById("memo-contents-error");
    if (!contentsError) return false;
    if (contentsValue.length > 5000) {
        contentsError.innerText = "内容は5000文字以内で入力する必要があります。";
        contentsInput.classList.add('is-invalid');
        validFlg = false;
    }

    if (validFlg === false) {
        return false;
    }
    return true;
}