let weekNumber = 1;

window.onload = function ()
{
    hideAllStatusStrokes();
    let select = document.querySelector('[name="group-select"]');
    let prevButton = document.querySelector('.prev-button');
    let nextButton = document.querySelector('.next-button');
    nextButton.addEventListener("click", incrementWeekNumber);
    prevButton.addEventListener("click", decrementWeekNumber);
    select.addEventListener("change", getClasses);
}

function incrementWeekNumber()
{
    if (weekNumber < 16)
    {
        weekNumber++;
    }
    document.querySelector('.current-week-number').textContent = weekNumber;
    if (document.querySelector('[name="group-select"]').value !== "default")
        getClasses();
}

function decrementWeekNumber()
{
    if (weekNumber > 1)
    {
        weekNumber--;
    }
    document.querySelector('.current-week-number').textContent = weekNumber;
    if (document.querySelector('[name="group-select"]').value !== "default")
        getClasses();
}

async function getClasses()
{
    clearStrokes();
    let groupInput = document.querySelector('[name="group-select"]')
    let group = groupInput.value;
    if (group === "default")
        return;
    let userId = await getUserId();
    let userGroup = await getUserGroup(userId);
    if (userGroup != null && group === userGroup)
        showAllStatusStrokes();
    else
        hideAllStatusStrokes();
    let url = `http://localhost:8888/EducationSchedule/schedule/${group}/${weekNumber}`;
    let response = await fetch(url);
    if (response.ok) 
    {
        let json = await response.json();
        let days = document.querySelectorAll('.schedule-day');
        let educationDaysCount = 6;
        for (let i = 0; i < educationDaysCount; i++)
        {
            days[i].querySelector('.date').textContent = json.dates[i];
        }
         json.classes.forEach(async el => {
            let clazz = days[el.dayOfWeek-1].querySelector(`[data-stroke-number="${el.classNumber}"]`);
                clazz.querySelector('[data-teacher]').textContent = el.userNamesDto.firstName;
                clazz.querySelector('[data-subject-name]').textContent = el.subject.name;
                clazz.querySelector('[data-stroke-audience]').textContent = el.audience;

                let status = clazz.querySelector('.stroke-status');
                if (userId != null && status.style.display !== 'none')
                {
                    status.querySelector('[data-stroke-status]').textContent = await getAttendanceStatus(userId, el.id);
                }
        });
    } 
    else 
    {
        alert("Ошибка HTTP: " + response.status);
    }
}

function clearStrokes()
{
    let dates = document.querySelectorAll('.class-date');
    let teachers = document.querySelectorAll('[data-teacher]');
    let subjects = document.querySelectorAll('[data-subject-name]');
    let strokes = document.querySelectorAll('[data-stroke-audience]');

    teachers.forEach(el => el.textContent = "");
    subjects.forEach(el => el.textContent = "");
    strokes.forEach(el => el.textContent = "");
    dates.forEach(el => el.textContent = "");
}

async function getAttendanceStatus(studentId, classId)
{
    let url = `http://localhost:8888/EducationSchedule/attendance/${studentId}/${classId}`;
    let response = await fetch(url);
    if (response.ok) 
    { 
        let json = await response.json();
        if (json.attendanceStatus === 'присутствует')
            return '+';
        else if (json.attendanceStatus === 'отсутствует')
            return '-';
        else if (json.attendanceStatus === 'отсутствует по уважительной причине')
            return 'у';
    }
    return '';
}

async function getUserId()
{
    let url = `http://localhost:8888/EducationSchedule/currentUserId`;
    let response = await fetch(url);
    if (response.ok && !response.redirected)
    {
        let json = await response.json();
        return json.id;
    }
    else
    {
        return null;
    }
}

async function getUserGroup(userId)
{
    let url = `http://localhost:8888/EducationSchedule/group/student/${userId}`;
    let response = await fetch(url);
    if (response.ok && !response.redirected)
    {
        let json = await response.json();
        return json.name;
    }
    else
    {
        return null;
    }
}

function hideAllStatusStrokes()
{
    document.querySelectorAll('.stroke-status').forEach(el => el.style.display = 'none');
}

function showAllStatusStrokes()
{
    document.querySelectorAll('.stroke-status').forEach(el => el.style.display = 'block');
}