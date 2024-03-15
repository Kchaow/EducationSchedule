window.onload = function ()
{
    let showButton = document.querySelector(".show-button");
    showButton.onclick = test;
}

async function test()
{
    let groupInput = document.querySelector(".group-input");
    let group = groupInput.value;
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
}
