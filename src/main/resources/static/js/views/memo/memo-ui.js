import { showSection } from '../../components/section.js';

/**
 * メモ一覧テーブルをレンダリングする
 * @param {Object} pageData APIから取得したページングデータ
 */
export function renderMemoTable(pageData) {
    // セクションを表示
    showSection('memo-section');

    // tbody(memo-list)の取得
    const listElement = document.getElementById('memo-list');
    if (!listElement) return;

    // pageDataまたはcontentが空だった場合
    if (!pageData || !Array.isArray(pageData.content)) {
        listElement.innerHTML = '<tr><td colspan="5" class="text-center">データの形式が正しくありません。</td></tr>';
        return;
    }

    pageData.content.updatedAt

    // 初期化して、取得したentitiesを代入
    listElement.innerHTML = '';
    pageData.content.forEach(entity => {
        const row = document.createElement('tr');
        const entityValues = [
            formatDateTime(entity.updatedAt),
            entity.title,
            entity.contents ?? ''

        ];
        // rowに列を追加
        entityValues.forEach(value => {
            const td = document.createElement('td');
            td.textContent = value;
            row.appendChild(td);
        });
        // ボタンを作成して列として追加
        const btnTd = document.createElement('td');
        const editBtn = createEditButton(entity);
        const deleteBtn = createDeleteButton(entity.id);
        btnTd.appendChild(editBtn);
        btnTd.appendChild(deleteBtn);
        row.appendChild(btnTd);

        listElement.appendChild(row);
    });
}

/**
 * 編集ボタンを作成する
 * @param {*} entity 
 * @returns editBtn
 */
function createEditButton(entity) {
    const btn = document.createElement('button');
    btn.className = 'btn btn-sm btn-primary';
    btn.textContent = '編集';
    btn.dataset.bsToggle = 'modal';
    btn.dataset.bsTarget = '#memoModal';
    btn.dataset.mode = 'edit';
    // datasetで設定することで予期しない入力があっても後の処理を崩さない
    btn.dataset.id = entity.id;
    btn.dataset.date = formatDateTime(entity.updatedAt);
    btn.dataset.title = entity.title;
    btn.dataset.contents = entity.contents ?? '';

    return btn;
}

/**
 * 削除ボタンを作成する
 * @param {Number} id
 * @returns delateBtn
 */
function createDeleteButton(id) {
    const btn = document.createElement('button');
    btn.className = 'btn btn-sm btn-danger btn-delete';
    btn.textContent = '削除';
    btn.dataset.id = id;
    return btn;
}
/**
 * JavaのLocalTimeDateからyyyy-MM-dd HH:mm形式へ変換する
 * @param {*} raw 
 * @returns 
 */
function formatDateTime(dateTime) {
    if (!dateTime) return '';
    const date = new Date(dateTime);
    return date.toLocaleDateString('sv-SE')
        + ' '
        + date.toLocaleTimeString('ja-JP', { hour: '2-digit', minute: '2-digit' });
}

/**
 * メモ入力モーダルを非表示にする
 */
export function hideMemoModal() {
    // モーダルを閉じる処理を追加
    const modalElement = document.getElementById('memoModal');
    const modalInstance = bootstrap.Modal.getInstance(modalElement);
    if (modalInstance) {
        modalInstance.hide();
    }
}

