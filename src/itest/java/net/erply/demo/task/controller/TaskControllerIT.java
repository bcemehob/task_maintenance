package net.erply.demo.task.controller;

import static net.erply.demo.base.entity.ErrorCode.WRONG_TASK_ID;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.erply.demo.IntegrationTestConfiguration;
import net.erply.demo.task.api.TaskDto;
import net.erply.demo.task.service.TaskService;

@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
@ContextConfiguration(classes = IntegrationTestConfiguration.class)
@AutoConfigureMockMvc
public class TaskControllerIT {

    @Autowired
    TaskService taskService;

    @Autowired
    private MockMvc mvc;


    private static final String TASK_NAME_BEFORE_EDIT = "task name before update";
    private static final String TASK_DESCR_BEFORE_EDIT = "task descr before update";
    private static final String TASK_NAME_AFTER_EDIT = "Task #1 edited";
    private static final String TASK_DESCR_AFTER_EDIT = "Task #1 description edited";

    private static final TaskDto TASK_DTO_BEFORE_EDIT = TaskDto.builder()
                                                    .name(TASK_NAME_BEFORE_EDIT)
                                                    .description(TASK_DESCR_BEFORE_EDIT)
                                                    .build();

    private static final TaskDto TASK_DTO_AFTER_EDIT = TaskDto.builder()
                                                    .name(TASK_NAME_AFTER_EDIT)
                                                    .description(TASK_DESCR_AFTER_EDIT)
                                                    .build();

    @Test
    public void shouldAddTask() throws Exception {
        mvc.perform(post("/api/v1/task/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TASK_DTO_BEFORE_EDIT)))
            .andExpect(status().isOk());
    }

    @Test
    public void shouldEditTask() throws Exception {
        mvc.perform(post("/api/v1/task/edit/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TASK_DTO_AFTER_EDIT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name")
                        .value(TASK_NAME_AFTER_EDIT))
                .andExpect(jsonPath("$.description")
                        .value(TASK_DESCR_AFTER_EDIT));
    }

    @Test
    public void shouldShowErrorIfWrongIdFormat() throws Exception {
        mvc.perform(post("/api/v1/task/edit/1a")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(TASK_DTO_AFTER_EDIT)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(WRONG_TASK_ID.name()));
    }

    @Test
    public void shouldDelete() throws Exception {
        mvc.perform(delete("/api/v1/task/delete/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldShowErrorIfWrongIdFormatDelete() throws Exception {
        mvc.perform(delete("/api/v1/task/delete/1a")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(WRONG_TASK_ID.name()));
    }

    @Test
    public void shouldComplete() throws Exception {
        mvc.perform(post("/api/v1/task/complete/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldShowErrorIfWrongIdFormatComplete() throws Exception {
        mvc.perform(post("/api/v1/task/complete/1a")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code")
                        .value(WRONG_TASK_ID.name()));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}

