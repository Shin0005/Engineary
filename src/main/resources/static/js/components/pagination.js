/**
 * ページングUIの表示状態（ボタンの可視化、ページ番号）を更新する
 * @param {Object} pageData ページング情報オブジェクト
 */
export function updatePaginationUI(pageData) {
    const currentSpan = document.getElementById('current-page');
    const totalSpan = document.getElementById('total-pages');
    const totalElements = document.getElementById('total-elements');

    currentSpan.textContent = pageData.totalPages === 0 ? 0 : pageData.number + 1;
    totalSpan.textContent = pageData.totalPages;
    totalElements.textContent = pageData.totalElements;

    const prevBtn = document.getElementById('prev-page');
    const nextBtn = document.getElementById('next-page');

    prevBtn.classList.toggle('invisible', pageData.number === 0);
    nextBtn.classList.toggle('invisible', pageData.totalPages === 0 || pageData.number === pageData.totalPages - 1);
}
/**
 * ページングボタンのイベント登録
 * @param {Function} onPrev 前へボタンクリック時のコールバック
 * @param {Function} onNext 次へボタンクリック時のコールバック
 */
export function initPaging(onPrev, onNext) {
    // 前のページがないなら隠す、あるなら表示する
    document.getElementById('prev-page')
        .addEventListener('click', onPrev);

    // 次のページがないなら隠す、あるなら表示する
    document.getElementById('next-page')
        .addEventListener('click', onNext);
}