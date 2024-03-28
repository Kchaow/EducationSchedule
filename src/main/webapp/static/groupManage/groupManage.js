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
        document.querySelector('.confirmation-text').textContent = 'Вы действительно хотите удалить группу ' 
                            + document.querySelector(`.group-box[data-id="${id}"]`).textContent + '?';
    }));
    acceptButton.addEventListener("click", () =>
    {
        let url = `http://localhost:8888/EducationSchedule/groups/${id}`;
        fetch(url, {method: 'DELETE'});
        document.querySelector('.confirmation-dialog').close();
        document.querySelector(`.group-flex-container[data-id="${id}"]`).remove();
    });
    cancelButton.addEventListener("click", () =>
    {
        document.querySelector('.confirmation-dialog').close();
    });
}