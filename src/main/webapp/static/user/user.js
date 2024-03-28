window.onload = function ()
{
    let deleteButton = document.querySelector('[name="delete-button"]');
    let userId = document.querySelector('[name="user-id"]').value;
    let statusDelete = document.querySelector('#delete-status');
    if (deleteButton)
    {
        deleteButton.addEventListener("click", async () => {
            let url = `http://localhost:8888/EducationSchedule/users/${userId}`;
            let response = await fetch(url, {method: 'DELETE'});
            if (response.status == 204)
                statusDelete.textContent = 'Пользователь удален';
            else
                statusDelete.textContent = 'Произошла ошибка';
        });
    }
};