let isSetupSchedule = true;
let attachmentStatus;
let dayNames = ['Понедельник', 'Вторник', 'Среда', 'Четверг', 'Пятница', 'Суббота'];

window.onload = function ()
{
    attachmentStatus = document.querySelector('.attachment-status');
    let scheduleStrokeContainer = document.querySelectorAll('.schedule-stroke-container');
    let groupSelect = document.querySelector('[name="group-select"]');

    groupSelect.addEventListener("change", () =>
    {
        clearDates();
        attachmentStatus.innerHTML = "";

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
            setupSchedule(groupSelect);
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

        subjectSelect.addEventListener("change", async () =>
        {
            let groupId = document.querySelector('[name="group-select"]').value;
            let classId = stroke.getAttribute('data-class-id');

            if (subjectSelect.value != 'default')
            {
                teacherSelect.removeAttribute("disabled");
                audienceInput.removeAttribute("disabled");
                if (!isSetupSchedule)
                {
                    classId = await saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
                    stroke.setAttribute('data-class-id', classId);
                }
            }
            else
            {
                if (!isSetupSchedule)
                    deleteClass(classId, groupId);
                stroke.setAttribute('data-class-id', '');
                teacherSelect.selectedIndex = 0;
                teacherSelect.setAttribute("disabled", "");
                audienceInput.setAttribute("disabled", "");
                audienceInput.value = 0;
            }
        });
        teacherSelect.addEventListener("change", async () =>
        {
            let classId = stroke.getAttribute('data-class-id');
            let groupId = document.querySelector('[name="group-select"]').value;
            classId = await saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
            stroke.setAttribute('data-class-id', classId);
        });
        audienceInput.addEventListener("change", async () =>
        {
            let classId = stroke.getAttribute('data-class-id');
            let groupId = document.querySelector('[name="group-select"]').value;
            classId = await saveChanges(classId ,subjectSelect.value, teacherSelect.value, audienceInput.value, groupId, weekNumber, dayOfWeek, classNumber, scheduleTemplateId);
            stroke.setAttribute('data-class-id', classId);
        });
    });
    //groupSelect.addEventListener("change", setupSchedule);
}

async function setupSchedule(groupSelect)
{
    let scheduleTemplateId = document.querySelector('.template-id').value;
    // let groupSelect = document.querySelector('[name="group-select"]');
    let group = groupSelect.options[groupSelect.selectedIndex].textContent;
    // if (group == "default")
    //     return;
    let weekNumber = document.querySelector('.current-week-number').textContent;
    let url = `http://localhost:8888/EducationSchedule/schedule/templates/${scheduleTemplateId}/${group}/${weekNumber}`;
    await fetch(url).then(async response => {
        if (response.ok)
        {
            let jsonSchedule = await response.json();
            let days = document.querySelectorAll('.schedule-day');
            let index = 0;
            let inactiveDays = [];
            days.forEach(el => {
                if (jsonSchedule.dates[index])
                {
                    let dateStroke = el.querySelector('.date');
                    el.querySelectorAll('.subject-select').forEach(el => el.removeAttribute('disabled'));
                    dateStroke.textContent = jsonSchedule.dates[index];
                }
                else
                {
                    el.querySelectorAll('.subject-select').forEach(el => el.setAttribute('disabled', ''));
                    inactiveDays.push(index+1);
                }
                    index++;
                });
            jsonSchedule.classes.forEach(async el => {
                let scheduleStroke = document.querySelector(`[data-weekday-index="${el.dayOfWeek}"][data-stroke-number="${el.classNumber}"]`)
                if (scheduleStroke && inactiveDays.indexOf(el.dayOfWeek) == -1)
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
                    await displayDettached(el, groupSelect.value);
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

async function deleteClass(classId, groupId)
{
    let status = document.querySelector('.status');
    status.textContent = 'Сохранение...';
    let url = `http://localhost:8888/EducationSchedule/schedule/class/${classId}/${groupId}`;
    let response = await fetch(url, {method: 'DELETE'});
    if (attachmentStatus.querySelector(`[data-class-id="${classId}"]`))
    {
        attachmentStatus.querySelector(`[data-class-id="${classId}"]`).remove();
    }
    if (response.status == 204)
    {
        status.textContent = 'Сохранено';
    }
    else
    {
        status.textContent = 'Ошибка';
        status.style.color = 'red';
    }
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
    let subject = {
        id: Number(subjectId)
    };  
    let selectedGroup = {
        id: groupId
    }
    let classDto = {
        id: classId.length != 0 ? Number(classId) : null,
        weekNumber: Number(weekNumber),
        userNamesDto: userNamesDto,
        groups : [selectedGroup],
        dayOfWeek: Number(dayOfWeek),
        classNumber: Number(classNumber),
        audience: Number(audience),
        subject: subject,
        scheduleTemplateId: Number(scheduleTemplateId)
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
        let jsonResponse = await response.json();
        // if (jsonResponse.groupsId.length > 1 && !attachmentStatus.querySelector(`[data-class-id="${jsonResponse.id}"]`))
        // {
        //     attachmentStatus.append = 
        //     `
        //     <div data-class-id="${jsonResponse.id}">
        //         <p>Пара ${jsonResponse.classNumber} в ${dayNames[jsonResponse.dayOfWeek-1]} связаны в группах: ${jsonResponse.groups}</p>
        //         <input class='detach-button-${jsonResponse.id}' value='Отвязать'/>
        //     </div>
        //     `;
        //     let detachButton = document.querySelector(`.detach-button-${jsonResponse.id}`).addEventListener('click', () => {
        //         attachmentStatus.querySelector(`[data-class-id="${jsonResponse.id}"]`).remove();
        //     });
        // }
        attachmentStatus = document.querySelector('.attachment-status');
        await displayDettached(jsonResponse, groupId);
        
        return jsonResponse.id;
    }
    else
    {
        status.textContent = 'Ошибка';
        status.style.color = 'red';
    }
}

async function displayDettached(clazz, groupId)
{
    if (clazz.groups.length > 1 && !attachmentStatus.querySelector(`[data-class-id="${clazz.id}"]`))
        {
            let groups = '';
            clazz.groups.forEach((element, index) => {
                if (index != 0)
                    groups += `, ${element.name}`;
                else
                    groups += `${element.name}`;
            });
            attachmentStatus.insertAdjacentHTML('beforeend', 
            `
            <div data-class-id="${clazz.id}">
                <p>Пара ${clazz.classNumber} в ${dayNames[clazz.dayOfWeek-1]} связаны в группах: ${groups}</p>
                <input type='button' class='detach-button-${clazz.id}' value='Отвязать'/>
            </div>
            `);
            let detachButton = document.querySelector(`.detach-button-${clazz.id}`).addEventListener('click', async () => {
                let url = `http://localhost:8888/EducationSchedule/schedule/class/detach/${clazz.id}/${groupId}`;
                let response = await fetch(url, {method: 'PUT'});
                if (response.ok)
                {
                    let groupSelect = document.querySelector('[name="group-select"]');
                    groupSelect.dispatchEvent(new Event('change'));
                }
                else
                {
                    console.log('Ошибка при отвязывании');
                }
            });
        }
}