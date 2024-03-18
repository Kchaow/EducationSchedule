let weekNumber = 1;

window.onload = function ()
{
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
    let url = `http://localhost:8888/EducationSchedule/schedule/${group}/${weekNumber}`;
    let response = await fetch(url);
    if (response.ok) 
    { 
        let json = await response.json();
        let days = document.querySelectorAll('.schedule-day');
        json.classes.forEach(el => {
            let date = new Date(el.date.replace('/', '-'));
            let day = date.getDay() - 1;
            days[day].querySelector('.class-date').textContent = `${date.getDate()}-${date.getMonth()}-${date.getFullYear()}`;
            if (day >= 0 && day <= 6)
            {
                let clazz = days[day].querySelector(`[data-stroke-number="${el.classNumber}"]`);
                clazz.querySelector('[data-teacher]').textContent = el.userNamesDto.firstName;
                clazz.querySelector('[data-subject-name]').textContent = el.subject.name;
                clazz.querySelector('[data-stroke-audience]').textContent = el.audience;
            }
        });
    } 
    else 
    {
        alert("Ошибка HTTP: " + response.status);
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
}
