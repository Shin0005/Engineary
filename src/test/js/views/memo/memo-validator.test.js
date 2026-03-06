import { describe, it, expect, beforeEach } from 'vitest';
import { validMemoForm } from '../../../../main/resources/static/js/views/memo/memo-validator.js';

// ============================================================
// DOM セットアップヘルパー
// ============================================================
function setupDom({ title = '', contents = '' } = {}) {
    document.body.innerHTML = `
        <input    id="memo-title"          value="${title}" />
        <div      id="memo-title-error"></div>
        <textarea id="memo-contents">${contents}</textarea>
        <div      id="memo-contents-error"></div>
    `;
}

describe('validMemoForm', () => {

    beforeEach(() => {
        document.body.innerHTML = '';
    });

    // ----------------------------------------------------------
    // 正常系
    // ----------------------------------------------------------
    describe('正常系', () => {

        it('No.1: タイトルが有効な値の場合、trueが返る', () => {
            setupDom({ title: 'テストタイトル', contents: '内容' });
            expect(validMemoForm()).toBe(true);
        });

        it('No.2: タイトルの前後に空白があってもtrimされtrueが返る', () => {
            setupDom({ title: '  タイトル  ', contents: '内容' });
            expect(validMemoForm()).toBe(true);
        });

        it('No.3: contentsが空文字でもtrueが返る', () => {
            setupDom({ title: 'タイトル', contents: '' });
            expect(validMemoForm()).toBe(true);
        });

    });

    // ----------------------------------------------------------
    // 異常系
    // ----------------------------------------------------------
    describe('異常系', () => {

        it('No.4: タイトルが空文字の場合、falseが返る', () => {
            setupDom({ title: '', contents: '内容' });
            expect(validMemoForm()).toBe(false);
        });

        it('No.5: タイトルが空文字の場合、is-invalidクラスが付与される', () => {
            setupDom({ title: '', contents: '内容' });
            validMemoForm();
            expect(document.getElementById('memo-title').classList.contains('is-invalid')).toBe(true);
        });

        it('No.6: タイトルが空文字の場合、エラーメッセージが表示される', () => {
            setupDom({ title: '', contents: '内容' });
            validMemoForm();
            expect(document.getElementById('memo-title-error').innerText).toBe('タイトルは必須です。');
        });

        it('No.7: タイトルが101文字の場合、falseが返る', () => {
            setupDom({ title: 'a'.repeat(101), contents: '内容' });
            expect(validMemoForm()).toBe(false);
        });

        it('No.8: タイトルが101文字の場合、エラーメッセージが表示される', () => {
            setupDom({ title: 'a'.repeat(101), contents: '内容' });
            validMemoForm();
            expect(document.getElementById('memo-title-error').innerText).toBe('100文字以内で入力してください。');
        });

        it('No.9: contentsが5001文字の場合、falseが返る', () => {
            setupDom({ title: 'タイトル', contents: 'a'.repeat(5001) });
            expect(validMemoForm()).toBe(false);
        });

        it('No.10: contentsが5001文字の場合、エラーメッセージが表示される', () => {
            setupDom({ title: 'タイトル', contents: 'a'.repeat(5001) });
            validMemoForm();
            expect(document.getElementById('memo-contents-error').innerText)
                .toBe('内容は5000文字以内で入力する必要があります。');
        });

        it('No.11: DOM要素が存在しない場合、falseが返る', () => {
            expect(validMemoForm()).toBe(false);
        });

    });

    // ----------------------------------------------------------
    // 境界値系
    // ----------------------------------------------------------
    describe('境界値系', () => {

        it('No.12: タイトルが100文字（上限）の場合、trueが返る', () => {
            setupDom({ title: 'a'.repeat(100), contents: '内容' });
            expect(validMemoForm()).toBe(true);
        });

        it('No.13: タイトルが101文字（上限+1）の場合、falseが返る', () => {
            setupDom({ title: 'a'.repeat(101), contents: '内容' });
            expect(validMemoForm()).toBe(false);
        });

        it('No.14: タイトルが1文字（最小）の場合、trueが返る', () => {
            setupDom({ title: 'a', contents: '内容' });
            expect(validMemoForm()).toBe(true);
        });

        it('No.15: contentsが5000文字（上限）の場合、trueが返る', () => {
            setupDom({ title: 'タイトル', contents: 'a'.repeat(5000) });
            expect(validMemoForm()).toBe(true);
        });

        it('No.16: contentsが5001文字（上限+1）の場合、falseが返る', () => {
            setupDom({ title: 'タイトル', contents: 'a'.repeat(5001) });
            expect(validMemoForm()).toBe(false);
        });

    });

    // ----------------------------------------------------------
    // 準正常系
    // ----------------------------------------------------------
    describe('準正常系', () => {

        it('No.17: 一度エラーになった後、有効な値を入力するとis-invalidが除去される', () => {
            setupDom({ title: '', contents: '内容' });
            validMemoForm();
            const titleInput = document.getElementById('memo-title');
            expect(titleInput.classList.contains('is-invalid')).toBe(true);

            titleInput.value = '修正済みタイトル';
            expect(validMemoForm()).toBe(true);
            expect(titleInput.classList.contains('is-invalid')).toBe(false);
        });

        it('No.18: タイトルがスペースのみの場合、falseが返る', () => {
            setupDom({ title: '   ', contents: '内容' });
            expect(validMemoForm()).toBe(false);
        });

        it('No.19: タイトルとcontentsが同時にエラーの場合、falseが返る', () => {
            setupDom({ title: '', contents: 'a'.repeat(5001) });
            expect(validMemoForm()).toBe(false);
        });

    });

});