// toast表示関数
export function showNotify(message, type = 'success') {
  const toastBox = document.getElementById('toast-box');
  const toastMessage = document.getElementById('toast-message');

  // いったん色クラスをリセット
  toastBox.classList.remove('text-bg-success', 'text-bg-danger');

  // タイプによって色を分岐
  if (type === 'error') {
    toastBox.classList.add('text-bg-danger');
  } else {
    toastBox.classList.add('text-bg-success');
  }

  toastMessage.textContent = message;

  const toast = new bootstrap.Toast(toastBox);
  toast.show();
}