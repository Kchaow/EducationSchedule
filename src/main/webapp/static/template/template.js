window.onload = function ()
{
    let scheduleStrokeContainer = document.querySelectorAll('.schedule-stroke-container');
    let groupSelect = document.querySelector('[name="group-select"]');

    groupSelect.addEventListener("change", () =>
    {
        clearDates();
        let subjectSelect = document.querySelectorAll('.subject-select');
        subjectSelect.forEach(el => {
            el.selectedIndex = 0;
            el.dispatchEvent(new Event("change"));
            el.setAttribute("disabled", "");
        });
        if (groupSelect.value != "default")
        {
            subjectSelect.forEach(el => el.removeAttribute("disabled"));
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
    groupSelect.addEventListener("change", setupSchedule);
}

async function setupSchedule()
{
    let scheduleTemplateId = document.querySelector('.template-id').value;
    let group = document.querySelector('[name="group-select"]').value;
    if (group == "default")
        return;
    let weekNumber = document.querySelector('.current-week-number').textContent;
    let url = `http://localhost:8888/EducationSchedule/schedule/templates/${scheduleTemplateId}/${group}/${weekNumber}`;
    await fetch(url).then(async response => {
        if (response.ok)
        {
            let jsonSchedule = await response.json();
            let days = document.querySelectorAll('.schedule-day');
            let index = 0;
            days.forEach(el => {
                    let dateStroke = el.querySelector('.date');
                    dateStroke.textContent = jsonSchedule.dates[index];
                    index++;
                });
            jsonSchedule.classes.forEach(el => {
                let scheduleStroke = document.querySelector(`[data-weekday-index="${el.dayOfWeek}"][data-stroke-number="${el.classNumber}"]`)
                if (scheduleStroke)
                {
                    let subjectSelect = scheduleStroke.querySelector('.subject-select');
                    let teacherSelect = scheduleStroke.querySelector('.teacher-select');
                    let audienceInput = scheduleStroke.querySelector('.audience-input');
                    subjectSelect.value = el.subject.id;
                    subjectSelect.dispatchEvent(new Event("change"));
                    if (el.userNamesDto != null)
                        teacherSelect.value = el.userNamesDto.id;
                    audienceInput.value = el.audience;
                }
            });
        }
    });
}

function clearDates()
{
    let dateStrokes = document.querySelectorAll('.date');
    dateStrokes.forEach(el => {
        el.textContent = "";
    });
}