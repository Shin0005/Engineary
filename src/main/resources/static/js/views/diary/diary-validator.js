// modal-formのvalidation
export function validForm() {
    let validFlg = true;

    // タイトル：　入力必須，１００文字以内
    const titleInput = document.getElementById('diary-title');
    const titleValue = titleInput.value.trim();
    const titleError = document.getElementById("title-error");

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

    // 作業時間：　入力形式(数値ONLY)
    const timeInput = document.getElementById('diary-workedTime');
    const timeValue = timeInput.value.trim();
    const timeError = document.getElementById("workedTime-error");
    const timeNum = Number(timeValue);
        // 数値でない、または０以下、または整数でない
    if (isNaN(timeNum) || timeNum <= 0 || !Number.isInteger(timeNum)) {
        timeError.innerText = "作業時間は1分以上の整数で入力してください。";
        timeInput.classList.add('is-invalid');
        validFlg = false;
    }

    // 作業日時：　notBlank
    const dateInput = document.getElementById('diary-workedDate');
    const dateValue = dateInput.value.trim();
    const dateError = document.getElementById("workedDate-error");
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