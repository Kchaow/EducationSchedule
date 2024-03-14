window.onload = function ()
{
    let showButton = document.querySelector(".show-button");
    showButton.onclick = test;
}

function test()
{
    let groupInput = document.querySelector(".group-input");
    let group = groupInput.getAttribute("value");
    let url = `http://localhost:8888/EducationSchedule/schedule/${group}/1`;
    let response = fetch(url);
    console.log(response);

    let days = document.querySelectorAll('.schedule-day');
    response.classes.forEach(el => {
        let day = new Date(el.date.replace('/', '-')).getDay();
        let clazz = days[day].querySelector(`[data-stroke-number=${el.classNumber}]`);
        clazz.querySelector('[data-teacher]').textContent = el.userNamesDto.firstName;
        clazz.querySelector('[data-subject-name]').textContent = el.subject.name;
        clazz.querySelector('[data-stroke-audience]').textContent = el.audience;
    });
}
