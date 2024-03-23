window.onload = function ()
{
    let deleteButtons = document.querySelectorAll('[name="delete-button"]');
    let acceptButton = document.querySelector('[name="accept-button"]');
    let cancelButton = document.querySelector('[name="cancel-button"]');
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
}