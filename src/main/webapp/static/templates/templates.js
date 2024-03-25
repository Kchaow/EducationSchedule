window.onload = function ()
{
    let deleteButtons = document.querySelectorAll('[name="delete-button"]');
    let acceptButton = document.querySelector('[name="accept-button"]');
    let cancelButton = document.querySelector('[name="cancel-button"]');
    let setActiveButton = document.querySelector('.set-active-button');
    let id;

    deleteButtons.forEach(el => el.addEventListener("click", () =>
    {
        id = el.getAttribute("data-id");
        document.querySelector('.confirmation-dialog').showModal();
        document.querySelector('.confirmation-text').textContent = 'Вы действительно хотите удалить шаблон ' 
                            + document.querySelector(`[data-id="${id}"]`).textContent + '?';
    }));
    acceptButton.addEventListener("click", () =>
    {
        let url = `http://localhost:8888/EducationSchedule/schedule/templates/${id}`;
        fetch(url, {method: 'DELETE'});
        document.querySelector('.confirmation-dialog').close();
        window.location.reload(2000);
    });
    cancelButton.addEventListener("click", () =>
    {
        document.querySelector('.confirmation-dialog').close();
    });
    setActiveButton.addEventListener("click", () =>
    {
        let template = document.querySelector('.select-template').value;
        if (template !== 'Не выбрано')
        {
            let url = `http://localhost:8888/EducationSchedule/schedule/templates?templateName=${template}`;
            fetch(url, {
                method: 'PUT'
            }).then(response => 
                {
                let status = document.querySelector('.select-template-status');
                if (response.status == 204)
                {
                    status.textContent = 'Шаблон успешно активирован';
                    status.style.cssText = 'display: block; color: green';
                }
                else
                {
                    status.textContent = 'Что-то пошло не так';
                    status.style.cssText = 'display: block; color: red';
                }
            })
        }
    });
}