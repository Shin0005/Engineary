/**
 * ルーター初期化
 * URL state の管理のみ行う。何を描画するかはコールバックに委譲。
 * @param {Function} initWindow ウィンドウ初期化関数
 */
export const initRouter = (initWindow) => {
    // 初期機能を日誌に設定
    window.history.replaceState(null, null, '/diary');

    // リンク初期化（リンククリック → URLを特定）
    document.body.addEventListener("click", (e) => {
        if (!e.target.matches("[data-link]")) return;
        e.preventDefault();

        // URLを切り替え（未発火）
        window.history.pushState(null, null, e.target.href);

        // コールバック関数を実行
        initWindow(window.location.pathname);
    });

    // ブラウザの戻る・進むボタン対応
    window.addEventListener("popstate", () => {
        initWindow(window.location.pathname);
    });
};