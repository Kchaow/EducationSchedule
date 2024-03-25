window.onload = function ()
{
    let scheduleStrokeContainer = document.querySelectorAll('.schedule-stroke-container');
    let groupSelect = document.querySelector('[name="group-select"]');

    groupSelect.addEventListener("change", () =>
    {
        let subjectSelect = document.querySelectorAll('.subject-select');
        if (groupSelect.value != "default")
        {
            subjectSelect.forEach(el => el.removeAttribute("disabled"));
        }
        else
        {
            subjectSelect.forEach(el => {
                el.selectedIndex = 0;
                el.dispatchEvent(new Event("change"));
                el.setAttribute("disabled", "");
            });
        }
    });
    
    scheduleStrokeContainer.forEach( (stroke) =>
    {
        let subjectSelect = stroke.querySelector('.subject-select');
        let teacherSelect = stroke.querySelector('.teacher-select');
        let audienceInput = stroke.querySelector('.audience-input');

        subjectSelect.addEventListener("change", () =>
        {
            if (subjectSelect.value != 'default')
            {
                teacherSelect.removeAttribute("disabled");
                audienceInput.removeAttribute("disabled");
            }
            else
            {
                teacherSelect.selectedIndex = 0;
                teacherSelect.setAttribute("disabled", "");
                audienceInput.setAttribute("disabled", "");
                audienceInput.value = 0;
            }
        });
    });
}