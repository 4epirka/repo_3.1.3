// --- Вспомогательная функция fetch с обработкой ошибок ---
async function fetchJSON(url, options = {}) {
    const res = await fetch(url, options);
    if (!res.ok) throw new Error(await res.text());
    return res.status === 204 ? null : res.json();
}

// --- Загрузка информации текущего пользователя ---
async function loadCurrentUser() {
    try {
        const user = await fetchJSON('/api/user');
        $('#username').text(user.username);
        $('#roles').text('with roles: ' + Array.from(user.roles).join(', '));

        // Заполнение таблицы
        const tbody = $('#user-table-body');
        tbody.empty();

        const tr = $(`
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.name}</td>
                <td>${user.age}</td>
                <td>${user.country}</td>
                <td>${Array.from(user.roles).join(', ')}</td>
            </tr>
        `);
        tbody.append(tr);

    } catch (err) {
        console.error('Error loading current user:', err);
        $('#username').text('anonymous');
        $('#roles').text('');
    }
}

// --- Инициализация страницы ---
$(document).ready(async function() {
    await loadCurrentUser();
});
