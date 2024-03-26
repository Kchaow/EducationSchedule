let isSetupSchedule = true;

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
            isSetupSchedule = true;
            el.dispatchEvent(new Event("change"));
            isSetupSchedule = false;
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
        let weekNumber = document.querySelector('.current-week-number').textContent;
        let dayOfWeek = stroke.getAttribute('data-weekday-index');
        let classNumber = stroke.getAttribute('data-stroke-number');
        let scheduleTemplateId = document.querySelector('.template-id').value;
        let classId = stroke.getAttribute('data-class-id');

        subjectSelect.addEventListener("change", () =>
        {
            let groupId = document.querySelector('[name="group-select"]').value;

            if (subjectSelect.value != 'default')
            {
                teacherSelect.removeAttribute("disabled");
                audienceInput.removeAttribute("disabled");
                if (!isSetupSchedule)
                {
                    saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
                }
            }
            else
            {
                stroke.setAttribute('data-class-id', '');
                teacherSelect.selectedIndex = 0;
                teacherSelect.setAttribute("disabled", "");
                audienceInput.setAttribute("disabled", "");
                audienceInput.value = 0;
            }
        });
        teacherSelect.addEventListener("change", () =>
        {
            let groupId = document.querySelector('[name="group-select"]').value;
            saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
        });
        audienceInput.addEventListener("change", () =>
        {
            let groupId = document.querySelector('[name="group-select"]').value;
            saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
        });
    });
    groupSelect.addEventListener("change", setupSchedule);
}

async function setupSchedule()
{
    let scheduleTemplateId = document.querySelector('.template-id').value;
    let group = document.querySelector('[name="group-select"]');
    group = group.options[group.selectedIndex].textContent;
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
                    scheduleStroke.setAttribute('data-class-id', el.id);
                    subjectSelect.value = el.subject.id;
                    isSetupSchedule = true;
                    subjectSelect.dispatchEvent(new Event("change"));
                    isSetupSchedule = false;
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

async function saveChanges(classId ,subjectId, teacherId, audience, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId)
{
    
    let status = document.querySelector('.status');
    status.textContent = 'Сохранение...';
    let userNamesDto;
    if (teacherId == "default")
        userNamesDto = null
    else
        userNamesDto = { id: Number(teacherId) };
    let subjectDto = {
        id: Number(subjectId)
    };  
    let classDto = {
        id: classId.length != 0 ? Number(classId) : null,
        weekNumber: Number(weekNumber),
        userNamesDto: userNamesDto,
        groupsId : [Number(groupId)],
        dayOfWeek: Number(dayOfWeek),
        classNumber: Number(classNumber),
        audience: Number(audience),
        subject: subjectDto
    }
    console.log(JSON.stringify(classDto));
    let url = `http://localhost:8888/EducationSchedule/schedule/templates/${scheduleTemplateId}`;
    let response = await fetch(url, {
        method: 'PUT',
        body: JSON.stringify(classDto),
        headers: {
            "Content-type": "application/json; charset=UTF-8"	
          }
    });
    if (response.ok)
    {
        status.textContent = 'Сохранено';
        return response.body;
    }
    else
    {
        status.textContent = 'Ошибка';
        status.style.color = 'red';
    }
}