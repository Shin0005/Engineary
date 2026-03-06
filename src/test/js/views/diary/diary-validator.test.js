import { describe, it, expect, beforeEach } from 'vitest';
import { validDiaryForm } from '../../../../main/resources/static/js/views/diary/diary-validator.js';

// ============================================================
// DOM セットアップヘルパー
// ============================================================
function setupDom({ title = '', contents = '', workedTime = '', workedDate = '' } = {}) {
    document.body.innerHTML = `
        <input    id="diary-title"          value="${title}" />
        <div      id="diary-title-error"></div>
        <textarea id="diary-contents">${contents}</textarea>
        <div      id="diary-contents-error"></div>
        <input    id="diary-workedTime"     value="${workedTime}" />
        <div      id="diary-workedTime-error"></div>
        <input    id="diary-workedDate"     value="${workedDate}" />
        <div      id="diary-workedDate-error"></div>
    `;
}

// 全フィールド有効なデフォルト値
const VALID = { title: 'テストタイトル', contents: '内容', workedTime: '60', workedDate: '2024-01-01' };

describe('validDiaryForm', () => {

    beforeEach(() => {
        document.body.innerHTML = '';
    });

    // ----------------------------------------------------------
    // 正常系
    // ----------------------------------------------------------
    describe('正常系', () => {

        it('No.1: 全フィールドが有効な値の場合、trueが返る', () => {
            setupDom(VALID);
            expect(validDiaryForm()).toBe(true);
        });

        it('No.2: タイトルの前後に空白があってもtrimされtrueが返る', () => {
            setupDom({ ...VALID, title: '  タイトル  ' });
            expect(validDiaryForm()).toBe(true);
        });

        it('No.3: contentsが空文字でもtrueが返る', () => {
            setupDom({ ...VALID, contents: '' });
            expect(validDiaryForm()).toBe(true);
        });

    });

    // ----------------------------------------------------------
    // 異常系 - タイトル
    // ----------------------------------------------------------
    describe('異常系（タイトル）', () => {

        it('No.4: タイトルが空文字の場合、falseが返る', () => {
            setupDom({ ...VALID, title: '' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.5: タイトルが空文字の場合、is-invalidクラスが付与される', () => {
            setupDom({ ...VALID, title: '' });
            validDiaryForm();
            expect(document.getElementById('diary-title').classList.contains('is-invalid')).toBe(true);
        });

        it('No.6: タイトルが101文字の場合、falseが返る', () => {
            setupDom({ ...VALID, title: 'a'.repeat(101) });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.7: タイトルが101文字の場合、エラーメッセージが表示される', () => {
            setupDom({ ...VALID, title: 'a'.repeat(101) });
            validDiaryForm();
            expect(document.getElementById('diary-title-error').innerText)
                .toBe('100文字以内で入力してください。');
        });

    });

    // ----------------------------------------------------------
    // 異常系 - 内容
    // ----------------------------------------------------------
    describe('異常系（内容）', () => {

        it('No.8: contentsが5001文字の場合、falseが返る', () => {
            setupDom({ ...VALID, contents: 'a'.repeat(5001) });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.9: contentsが5001文字の場合、エラーメッセージが表示される', () => {
            setupDom({ ...VALID, contents: 'a'.repeat(5001) });
            validDiaryForm();
            expect(document.getElementById('diary-contents-error').innerText)
                .toBe('内容は5000文字以内で入力する必要があります。');
        });

    });

    // ----------------------------------------------------------
    // 異常系 - 作業時間
    // ----------------------------------------------------------
    describe('異常系（作業時間）', () => {

        it('No.10: 作業時間が0の場合、falseが返る', () => {
            setupDom({ ...VALID, workedTime: '0' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.11: 作業時間が負数の場合、falseが返る', () => {
            setupDom({ ...VALID, workedTime: '-1' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.12: 作業時間が小数の場合、falseが返る', () => {
            setupDom({ ...VALID, workedTime: '1.5' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.13: 作業時間が数値でない場合、falseが返る', () => {
            setupDom({ ...VALID, workedTime: 'abc' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.14: 作業時間が1441の場合（上限+1）、falseが返る', () => {
            setupDom({ ...VALID, workedTime: '1441' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.15: 作業時間がエラーの場合、エラーメッセージが表示される', () => {
            setupDom({ ...VALID, workedTime: '0' });
            validDiaryForm();
            expect(document.getElementById('diary-workedTime-error').innerText)
                .toBe('作業時間は1分以上1440以下の整数で入力してください。');
        });

    });

    // ----------------------------------------------------------
    // 異常系 - 作業日
    // ----------------------------------------------------------
    describe('異常系（作業日）', () => {

        it('No.16: 作業日が空の場合、falseが返る', () => {
            setupDom({ ...VALID, workedDate: '' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.17: 作業日が空の場合、エラーメッセージが表示される', () => {
            setupDom({ ...VALID, workedDate: '' });
            validDiaryForm();
            expect(document.getElementById('diary-workedDate-error').innerText)
                .toBe('作業日時は入力する必要があります。');
        });

    });

    // ----------------------------------------------------------
    // 境界値系
    // ----------------------------------------------------------
    describe('境界値系', () => {

        it('No.18: タイトルが100文字（上限）の場合、trueが返る', () => {
            setupDom({ ...VALID, title: 'a'.repeat(100) });
            expect(validDiaryForm()).toBe(true);
        });

        it('No.19: タイトルが101文字（上限+1）の場合、falseが返る', () => {
            setupDom({ ...VALID, title: 'a'.repeat(101) });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.20: 作業時間が1（下限）の場合、trueが返る', () => {
            setupDom({ ...VALID, workedTime: '1' });
            expect(validDiaryForm()).toBe(true);
        });

        it('No.21: 作業時間が1440（上限）の場合、trueが返る', () => {
            setupDom({ ...VALID, workedTime: '1440' });
            expect(validDiaryForm()).toBe(true);
        });

        it('No.22: 作業時間が1441（上限+1）の場合、falseが返る', () => {
            setupDom({ ...VALID, workedTime: '1441' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.23: contentsが5000文字（上限）の場合、trueが返る', () => {
            setupDom({ ...VALID, contents: 'a'.repeat(5000) });
            expect(validDiaryForm()).toBe(true);
        });

        it('No.24: contentsが5001文字（上限+1）の場合、falseが返る', () => {
            setupDom({ ...VALID, contents: 'a'.repeat(5001) });
            expect(validDiaryForm()).toBe(false);
        });

    });

    // ----------------------------------------------------------
    // 準正常系
    // ----------------------------------------------------------
    describe('準正常系', () => {

        it('No.25: 複数フィールドが同時にエラーの場合、falseが返る', () => {
            setupDom({ title: '', contents: 'a'.repeat(5001), workedTime: '0', workedDate: '' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.26: タイトルがスペースのみの場合、falseが返る', () => {
            setupDom({ ...VALID, title: '   ' });
            expect(validDiaryForm()).toBe(false);
        });

        it('No.27: DOM要素が存在しない場合、falseが返る', () => {
            expect(validDiaryForm()).toBe(false);
        });

    });

});