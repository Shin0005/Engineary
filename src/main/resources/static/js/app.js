import { initRouter } from './router.js';
import { initDiary } from './views/diary/diary-init.js';
import { initMemo } from './views/memo/memo-init.js';
import { initPaging, updatePaginationUI } from './components/pagination.js';
import { showNotify } from './components/toast.js';

let currentPage = 0;
// 現在の機能のrefresh関数を保持
let currentRefresh = null;

// URLと機能の対応テーブル
const features = {
    "/diary": initDiary,
    "/memo": initMemo,
};

// ルーター初期化（URLを変化させる）
initRouter((path) => initWindow(path));

// ページングイベント初期化（次へ・前へボタン）
initPaging(
    // 第一引数onPrevへコールバック関数を代入
    () => refreshWindow(currentPage - 1),
    // 第二引数onNextへ...
    () => refreshWindow(currentPage + 1)
);

// ロード時読み込み
window.onload = () => initWindow(window.location.pathname);


/**
 * 個別ウィンドウ初期化関数（URLによって機能を切り替え）\
 * ウィンドウの描画を待つ必要はないので同期処理とする。　
 * @param {*} path URL 
 */
function initWindow(path) {
    // 機能によって初期化関数を選択
    const init = features[path] ?? features["/diary"]; // 初期値diary

    // refresh関数を更新
    currentRefresh = init({
        // 各init内のmodal保存ボタン初期化するコールバック
        onSaved: (msg) => {
            showNotify(msg);
            refreshWindow(currentPage);
        },
        // 各init内のmodal削除ボタン初期化するコールバック
        onDeleted: () => {
            showNotify('削除しました');
            refreshWindow(currentPage);
        },
        onError: (msg) => showNotify(msg, 'error'),
    });

    // 初期ページ表示
    refreshWindow(0);
};


/**
 * ページング共通処理関数
 * @param {*} page 
 */
async function refreshWindow(page = 0) {
    try {
        currentPage = page;
        const pageData = await currentRefresh(page);

        // 空ページなら前へ戻る
        if (pageData.content.length === 0 && page > 0) {
            await refreshWindow(page - 1);
            return;
        }
        // ページングUIを更新
        updatePaginationUI(pageData.page);

    } catch (error) {
        showNotify('読み込みに失敗しました', 'error');
        console.error(`${error.name}: ${error.message}`);
    }
};