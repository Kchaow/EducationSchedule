package unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.letunov.controller.ScheduleController;
import org.letunov.service.ScheduleService;
import org.letunov.service.dto.ScheduleDto;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import unit.DomainObjectGenerator;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ScheduleControllerTest
{
    private MockMvc mockMvc;
    @Mock
    private ScheduleService scheduleService;
    @InjectMocks
    private ScheduleController scheduleController;
    private final DomainObjectGenerator domainObjectGenerator = new DomainObjectGenerator();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @BeforeEach
    public void setup()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleController).build();
    }

    @Test
    public void getScheduleShouldReturnScheduleDto() throws Exception
    {
        ScheduleDto scheduleDto = domainObjectGenerator.convertToScheduleDto(domainObjectGenerator.getClassList(3));
        String groupName = "group";
        int weekNumber = 1;
        when(scheduleService.getGroupSchedule(weekNumber, groupName)).thenReturn(ResponseEntity.ok(scheduleDto));

        String expectedJson = objectMapper.writeValueAsString(scheduleDto);
        mockMvc.perform(get("/schedule/{groupName}/{weekNumber}", groupName, weekNumber))
                .andDo(log())
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }
}
