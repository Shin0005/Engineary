// modal-formのvalidation
/**
 * Formのバリデーションを行う
 * @returns {Boolean}
 */
export function validDiaryForm() {
    let validFlg = true;

    // タイトル：　入力必須，１００文字以内
    const titleInput = document.getElementById('diary-title');
    if (!titleInput) return false;
    const titleValue = titleInput.value.trim();
    const titleError = document.getElementById("diary-title-error");
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
    const contentsInput = document.getElementById('diary-contents');
    if (!contentsInput) return false;
    const contentsValue = contentsInput.value.trim();
    const contentsError = document.getElementById("diary-contents-error");
    if (!contentsError) return false;
    if (contentsValue.length > 5000) {
        contentsError.innerText = "内容は5000文字以内で入力する必要があります。";
        contentsInput.classList.add('is-invalid');
        validFlg = false;
    }

    // 作業時間：　入力形式(数値ONLY)
    const timeInput = document.getElementById('diary-workedTime');
    if (!timeInput) return false;
    const timeValue = timeInput.value.trim();
    const timeError = document.getElementById("diary-workedTime-error");
    if (!timeError) return false;
    const timeNum = Number(timeValue);
    // 数値でない、または０以下、または整数でない
    if (isNaN(timeNum) || timeNum <= 0 || !Number.isInteger(timeNum) || timeNum > 1440) {
        timeError.innerText = "作業時間は1分以上1440以下の整数で入力してください。";
        timeInput.classList.add('is-invalid');
        validFlg = false;
    }

    // 作業日時：　notBlank
    const dateInput = document.getElementById('diary-workedDate');
    if (!dateInput) return false;
    const dateValue = dateInput.value.trim();
    const dateError = document.getElementById("diary-workedDate-error");
    if (!dateError) return false;
    if (dateValue === "") {
        dateError.innerText = "作業日時は入力する必要があります。";
        dateInput.classList.add('is-invalid');
        validFlg = false;
    }

    if (validFlg === false) {
        return false;
    }
    return true;
}