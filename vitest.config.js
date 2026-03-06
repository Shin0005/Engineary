import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    // ブラウザのDOM APIをNode.js上で再現する
    environment: 'jsdom',
    // テストファイルの場所
    include: ['src/test/js/**/*.test.js'],
  },
});
