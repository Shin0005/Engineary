/**
 * ページングUIの表示状態（ボタンの可視化、ページ番号）を更新する
 * @param {Object} pageData ページング情報オブジェクト
 */
export function updatePaginationUI(pageData) {
    const currentSpan = document.getElementById('current-page');
    const totalSpan = document.getElementById('total-pages');
    const totalElements = document.getElementById('total-elements');

    currentSpan.textContent = pageData.number + 1; // 表示用は+1
    totalSpan.textContent = pageData.totalPages;
    totalElements.textContent = pageData.totalElements;

    const prevBtn = document.getElementById('prev-page');
    const nextBtn = document.getElementById('next-page');

    // 前のページがないなら隠す、あるなら表示する
    if (pageData.number === 0) {
        prevBtn.classList.add('invisible');
    } else {
        prevBtn.classList.remove('invisible');
    }

    // 次のページがないなら隠す、あるなら表示する
    if (pageData.number === pageData.totalPages - 1) {
        nextBtn.classList.add('invisible');
    } else {
        nextBtn.classList.remove('invisible');
    }
}