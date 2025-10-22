// --- Вспомогательная функция fetch с обработкой ошибок ---
async function fetchJSON(url, options = {}) {
    const res = await fetch(url, options);
    if (!res.ok) throw new Error(await res.text());
    return res.status === 204 ? null : res.json();
}

// --- Загрузка текущего пользователя ---
async function loadCurrentUser() {
    try {
        const user = await fetchJSON('/api/user');
        $('#current-user').text(user.username);
        $('#current-roles').text('with roles: ' + Array.from(user.roles).join(', '));
    } catch (err) {
        $('#current-user').text('anonymous');
        $('#current-roles').text('');
        console.error(err);
    }
}

// --- Загрузка всех пользователей ---
async function loadUsers() {
    try {
        const users = await fetchJSON('/api/admin/users');
        const tbody = $('#users-table tbody');
        tbody.empty();

        users.forEach(u => {
            const rolesStr = Array.from(u.roles).join(', ');
            const tr = $(`
                <tr>
                    <td>${u.id}</td>
                    <td>${u.username}</td>
                    <td>${u.name}</td>
                    <td>${u.age}</td>
                    <td>${u.country}</td>
                    <td>${rolesStr}</td>
                    <td>
                        <button class="btn btn-sm btn-info me-2 edit-btn" data-id="${u.id}">Edit</button>
                        <button class="btn btn-sm btn-danger delete-btn" data-id="${u.id}" data-name="${u.username}">Delete</button>
                    </td>
                </tr>
            `);
            tbody.append(tr);
        });
    } catch (err) {
        console.error('Error loading users', err);
    }
}

// --- Загрузка всех доступных ролей для селектов ---
async function loadRolesOptions() {
    try {
        const roles = await fetchJSON('/api/admin/roles');
        const makeOptions = (selected = []) => {
            return roles.map(r => `<option value="${r}">${r}</option>`).join('');
        };
        // Инициализируем списки без выбранных значений, выбор установим при открытии модалок
        $('#create-roles').html(makeOptions());
        $('#edit-roles').html(makeOptions());
    } catch (err) {
        console.error('Error loading roles', err);
    }
}

// --- Открытие модального окна редактирования ---
$(document).on('click', '.edit-btn', async function() {
    const id = $(this).data('id');
    const user = await fetchJSON(`/api/admin/users/${id}`);

    $('#edit-id').val(user.id);
    $('#edit-username').val(user.username);
    $('#edit-name').val(user.name);
    $('#edit-age').val(user.age);
    $('#edit-country').val(user.country);
    $('#edit-password').val('');
    // выставляем выбранные роли в мультиселекте
    const rolesSelect = $('#edit-roles');
    rolesSelect.find('option').each(function() {
        const val = $(this).val();
        $(this).prop('selected', user.roles.includes(val));
    });

    $('#editUserModal').modal('show');
});

// --- Сохранение изменений пользователя ---
$('#editUserForm').on('submit', async function(e) {
    e.preventDefault();

    const id = $('#edit-id').val();
    const roles = ($('#edit-roles').val() || []).map(r => r.trim());
    const password = $('#edit-password').val().trim();

    const user = {
        username: $('#edit-username').val(),
        name: $('#edit-name').val(),
        age: parseInt($('#edit-age').val()),
        country: $('#edit-country').val(),
        roles: roles
    };

    // только если пароль введён — кодируем его на сервере
    if (password) user.password = password;

    try {
        await fetchJSON(`/api/admin/users/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(user)
        });
        $('#editUserModal').modal('hide');
        await loadUsers();
    } catch (err) {
        alert('Error updating user: ' + err.message);
    }
});

// --- Открытие модального окна удаления ---
$(document).on('click', '.delete-btn', function() {
    const id = $(this).data('id');
    const name = $(this).data('name');

    $('#del-name').text(name);
    $('#confirm-delete').data('id', id);

    $('#deleteUserModal').modal('show');
});

// --- Подтверждение удаления ---
$('#confirm-delete').on('click', async function() {
    const id = $(this).data('id');

    try {
        await fetchJSON(`/api/admin/users/${id}`, {method: 'DELETE'});
        $('#deleteUserModal').modal('hide');
        await loadUsers();
    } catch (err) {
        alert('Error deleting user: ' + err.message);
    }
});

// --- Создание нового пользователя ---
$('#createUserForm').on('submit', async function(e) {
    e.preventDefault();

    const roles = ($('#create-roles').val() || []).map(r => r.trim());

    const user = {
        username: $('#create-username').val(),
        password: $('#create-password').val(),
        name: $('#create-name').val(),
        age: parseInt($('#create-age').val()),
        country: $('#create-country').val(),
        roles: roles
    };

    try {
        await fetchJSON('/api/admin/users', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(user)
        });
        $('#createUserModal').modal('hide');
        $('#createUserForm')[0].reset(); // Очистка формы
        await loadUsers();
    } catch (err) {
        alert('Error creating user: ' + err.message);
    }
});


// --- Инициализация страницы ---
$(document).ready(async function() {
    await loadCurrentUser();
    await loadRolesOptions();
    await loadUsers();
});
