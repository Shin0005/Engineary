import { describe, it, expect, vi, beforeEach } from 'vitest';
import { apiFetch } from '../../main/resources/static/js/apifetch.js';

// ============================================================
// fetch モックヘルパー
// ============================================================

/**
 * 正常レスポンスのモックを生成する
 * @param {Object} body  レスポンスボディ（JSON）
 * @param {number} status HTTPステータスコード
 */
function mockFetchOk(body = {}, status = 200) {
    globalThis.fetch = vi.fn().mockResolvedValue({
        ok: true,
        status,
        json: () => Promise.resolve(body),
    });
}

/**
 * エラーレスポンスのモックを生成する
 * @param {Object} body  エラーボディ（detail / title / errors）
 * @param {number} status HTTPステータスコード
 */
function mockFetchError(body = {}, status = 400) {
    globalThis.fetch = vi.fn().mockResolvedValue({
        ok: false,
        status,
        json: () => Promise.resolve(body),
    });
}

/**
 * ネットワークエラーのモックを生成する
 */
function mockFetchNetworkError() {
    globalThis.fetch = vi.fn().mockRejectedValue(new TypeError('Failed to fetch'));
}

// ============================================================
// apiFetch テスト
// ============================================================
describe('apiFetch', () => {

    beforeEach(() => {
        vi.restoreAllMocks();
    });

    // ----------------------------------------------------------
    // 正常系
    // ----------------------------------------------------------
    describe('正常系', () => {

        it('No.1: GETリクエストでJSONレスポンスが返る', async () => {
            mockFetchOk({ id: 1, title: 'テスト' });

            const result = await apiFetch('/api/memo', {});

            expect(result).toEqual({ id: 1, title: 'テスト' });
        });

        it('No.2: デフォルトメソッドがGETである', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', {});

            expect(globalThis.fetch).toHaveBeenCalledWith(
                '/api/memo',
                expect.objectContaining({ method: 'GET' })
            );
        });

        it('No.3: Content-Typeヘッダーが自動付与される', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', {});

            expect(globalThis.fetch).toHaveBeenCalledWith(
                '/api/memo',
                expect.objectContaining({
                    headers: expect.objectContaining({ 'Content-Type': 'application/json' })
                })
            );
        });

        it('No.4: POSTリクエストでbodyがJSON文字列化される', async () => {
            mockFetchOk({ id: 1 }, 201);
            const body = { title: 'タイトル', contents: '内容' };

            await apiFetch('/api/memo', { method: 'POST', body });

            expect(globalThis.fetch).toHaveBeenCalledWith(
                '/api/memo',
                expect.objectContaining({
                    method: 'POST',
                    body: JSON.stringify(body),
                })
            );
        });

        it('No.5: カスタムヘッダーがマージされる', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', { headers: { 'X-Custom': 'value' } });

            expect(globalThis.fetch).toHaveBeenCalledWith(
                '/api/memo',
                expect.objectContaining({
                    headers: expect.objectContaining({
                        'Content-Type': 'application/json',
                        'X-Custom': 'value',
                    })
                })
            );
        });

        it('No.6: DELETEリクエストが正常に実行される', async () => {
            mockFetchOk({});

            const result = await apiFetch('/api/memo/1', { method: 'DELETE' });

            expect(globalThis.fetch).toHaveBeenCalledWith(
                '/api/memo/1',
                expect.objectContaining({ method: 'DELETE' })
            );
            expect(result).toEqual({});
        });

    });

    // ----------------------------------------------------------
    // 異常系
    // ----------------------------------------------------------
    describe('異常系', () => {

        it('No.7: 400レスポンスの場合、Errorがスローされる', async () => {
            mockFetchError({ detail: 'リクエストが正しくありません', title: 'Bad Request' }, 400);

            await expect(apiFetch('/api/memo', { method: 'POST', body: {} }))
                .rejects.toThrow();
        });

        it('No.8: 400レスポンスの場合、error.messageにdetailが設定される', async () => {
            mockFetchError({ detail: 'リクエストが正しくありません', title: 'Bad Request' }, 400);

            try {
                await apiFetch('/api/memo', { method: 'POST', body: {} });
            } catch (error) {
                expect(error.message).toBe('リクエストが正しくありません');
            }
        });

        it('No.9: 400レスポンスの場合、error.nameにtitleが設定される', async () => {
            mockFetchError({ detail: 'リクエストが正しくありません', title: 'Bad Request' }, 400);

            try {
                await apiFetch('/api/memo', { method: 'POST', body: {} });
            } catch (error) {
                expect(error.name).toBe('Bad Request');
            }
        });

        it('No.10: 400レスポンスにerrorsが含まれる場合、error.errorsに設定される', async () => {
            const errors = [{ field: 'title', reason: '必須です' }];
            mockFetchError({ detail: 'バリデーションエラー', title: 'Bad Request', errors }, 400);

            try {
                await apiFetch('/api/memo', { method: 'POST', body: {} });
            } catch (error) {
                expect(error.errors).toEqual(errors);
            }
        });

        it('No.11: 404レスポンスの場合、Errorがスローされる', async () => {
            mockFetchError({ detail: '指定されたリソースが見つかりませんでした。', title: 'Not Found' }, 404);

            await expect(apiFetch('/api/memo/999', {}))
                .rejects.toThrow();
        });

        it('No.12: 500レスポンスの場合、Errorがスローされる', async () => {
            mockFetchError({ detail: 'Internal Server Error', title: 'Error' }, 500);

            await expect(apiFetch('/api/memo', {}))
                .rejects.toThrow();
        });

        it('No.13: ネットワークエラーの場合、Errorがスローされる', async () => {
            mockFetchNetworkError();

            await expect(apiFetch('/api/memo', {}))
                .rejects.toThrow('Failed to fetch');
        });

    });

    // ----------------------------------------------------------
    // 境界値系
    // ----------------------------------------------------------
    describe('境界値系', () => {

        it('No.14: bodyがnullの場合、config.bodyが設定されない', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', { body: null });

            const calledConfig = globalThis.fetch.mock.calls[0][1];
            expect(calledConfig.body).toBeUndefined();
        });

        it('No.15: bodyがstring型の場合、JSON.stringifyされずそのまま使われない', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', { body: '{"title":"test"}' });

            const calledConfig = globalThis.fetch.mock.calls[0][1];
            expect(calledConfig.body).toBeUndefined();
        });

        it('No.16: bodyがオブジェクトの場合、JSON.stringifyされる', async () => {
            mockFetchOk({});
            const body = { title: 'テスト' };

            await apiFetch('/api/memo', { method: 'POST', body });

            const calledConfig = globalThis.fetch.mock.calls[0][1];
            expect(calledConfig.body).toBe(JSON.stringify(body));
        });

    });

    // ----------------------------------------------------------
    // 準正常系
    // ----------------------------------------------------------
    describe('準正常系', () => {

        it('No.17: レスポンスのJSONパースに失敗した場合、{}が返る', async () => {
            globalThis.fetch = vi.fn().mockResolvedValue({
                ok: true,
                status: 200,
                json: () => Promise.reject(new SyntaxError('invalid json')),
            });

            const result = await apiFetch('/api/memo', {});

            expect(result).toEqual({});
        });

        it('No.18: エラーレスポンスのJSONパース失敗時、message・name・errorsがすべてundefinedのErrorがスローされる', async () => {
            globalThis.fetch = vi.fn().mockResolvedValue({
                ok: false,
                status: 500,
                json: () => Promise.reject(new SyntaxError('invalid json')),
            });

            try {
                await apiFetch('/api/memo', {});
            } catch (error) {
                // result={} になるため title・detail・errors はすべて undefined
                expect(error.message).toBeUndefined();
                expect(error.name).toBeUndefined();
                expect(error.errors).toBeUndefined();
            }
        });

        it('No.19: fetchが1回だけ呼ばれる', async () => {
            mockFetchOk({});

            await apiFetch('/api/memo', {});

            expect(globalThis.fetch).toHaveBeenCalledTimes(1);
        });

    });

});