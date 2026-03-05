// 機能の種類
const sections = ['diary-section', 'memo-section'];
/**
 * 機能ごとにセクションを表示を切り替える。
 * @param {*} id 
 */
export function showSection(id) {
    sections.forEach(sectionId => {
        document.getElementById(sectionId)
            .classList.toggle('d-none', sectionId !== id);
    });
}