window.onload = function ()
{
    let select = document.querySelector('[name="group-select"]');
    select.addEventListener("change", getClasses);
}

async function getClasses()
{
    clearStrokes();
    let groupInput = document.querySelector('[name="group-select"]')
    let group = groupInput.value;
    if (group === "default")
        return;
    console.log(group);
    let url = `http://localhost:8888/EducationSchedule/schedule/${group}/1`;
    let response = await fetch(url);

    if (response.ok) 
    { 
        let json = await response.json();
        let days = document.querySelectorAll('.schedule-day');
        json.classes.forEach(el => {
            let day = new Date(el.date.replace('/', '-')).getDay() - 1;
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
        teachers = document.querySelectorAll('[data-teacher]');
        subjects = document.querySelectorAll('[data-subject-name]');
        strokes = document.querySelectorAll('[data-stroke-audience]');

        teachers.forEach(el => el.textContent = "");
        subjects.forEach(el => el.textContent = "");
        strokes.forEach(el => el.textContent = "");
    }
}
