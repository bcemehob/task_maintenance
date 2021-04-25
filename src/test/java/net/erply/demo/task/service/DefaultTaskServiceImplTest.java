package net.erply.demo.task.service;

import static net.erply.demo.base.entity.ErrorCode.DUPLICATED_TASK_NAME;
import static net.erply.demo.base.entity.ErrorCode.EMPTY_TASK_FIELD_DESCRIPTION;
import static net.erply.demo.base.entity.ErrorCode.EMPTY_TASK_FIELD_NAME;
import static net.erply.demo.base.entity.ErrorCode.TASK_NOT_FOUND;
import static net.erply.demo.base.entity.ErrorCode.UNEXPECTED_ERROR;
import static net.erply.demo.base.entity.ErrorCode.WRONG_TASK_ID;
import static net.erply.demo.task.entity.TaskStatus.AVAILABLE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import net.erply.demo.base.exception.AddTaskBadResponseException;
import net.erply.demo.base.exception.DeleteTaskBadResponseException;
import net.erply.demo.base.exception.EditTaskBadResponseException;
import net.erply.demo.task.api.TaskDto;
import net.erply.demo.task.dao.TaskJpaRepo;
import net.erply.demo.base.entity.Task;

@SpringBootTest
public class DefaultTaskServiceImplTest {

    @MockBean
    TaskJpaRepo mockRepo;

    @Inject
    DefaultTaskServiceImpl victim;

    private static final Task TEST_TASK = Task.builder()
                                        .name("aa").description("bb")
                                        .status(AVAILABLE).build();
    private static final TaskDto TEST_TASK_DTO = TaskDto.builder().name("aa")
                                            .description("bb").status(AVAILABLE).build();

    private static final TaskDto TEST_TASK_DTO_NO_NAME = TaskDto.builder()
            .name("").description("bb")
            .status(AVAILABLE).build();
    private static final TaskDto TEST_TASK_DTO_NO_DESCR = TaskDto.builder()
            .name("aa").description(" ")
            .status(AVAILABLE).build();

    @BeforeEach
    public void setup() {
        doReturn(TEST_TASK)
                .when(mockRepo).save(any());
        doReturn(TEST_TASK)
                .when(mockRepo).getOne(any());
        doReturn(List.of(TEST_TASK))
                .when(mockRepo).findAll();
    }

    @Test
    void shouldAddTask() {
        assertEquals(TEST_TASK_DTO, victim.addTask(TEST_TASK_DTO));
    }

    @Test
    void shouldThrowEmptyNameException() {
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO_NO_NAME));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_NAME.name());
    }

    @Test
    void shouldThrowEmptyDescriptionException() {
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO_NO_DESCR));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_DESCRIPTION.name());
    }

    @Test
    void shouldThrowNullNameException() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new PropertyValueException("test", "test", "name"));
        doThrow(srcE).when(mockRepo).save(any());
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_NAME.name());
    }

    @Test
    void shouldThrowNullDescriptionException() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new ConstraintViolationException("test", new SQLException(), "description"));
        doThrow(srcE).when(mockRepo).save(any());
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), DUPLICATED_TASK_NAME.name());
    }

    @Test
    void shouldThrowDuplicateNameDescriptionException() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new PropertyValueException("test", "test", "description"));
        doThrow(srcE).when(mockRepo).save(any());
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_DESCRIPTION.name());
    }

    @Test
    void shouldThrowUnexpectedException() {
        Exception srcE = new RuntimeException("test_runtime_exception");
        doThrow(srcE).when(mockRepo).save(any());
        AddTaskBadResponseException e = assertThrows(AddTaskBadResponseException.class, () -> victim.addTask(TEST_TASK_DTO));
        assertThat(e, instanceOf(AddTaskBadResponseException.class));
        assertEquals(e.getCode(), UNEXPECTED_ERROR.name());
    }

    @Test
    void shouldDeleteTask() {
        doNothing()
                .when(mockRepo).deleteById(anyInt());
        victim.deleteTask(0);
        verify(mockRepo, times(1)).deleteById(anyInt());
    }

    @Test
    void shouldThrowErrorIfWrongInput() {
        doThrow(new InvalidDataAccessApiUsageException("TEST")).when(mockRepo).deleteById(any());
        DeleteTaskBadResponseException e = assertThrows(DeleteTaskBadResponseException.class, () -> victim.deleteTask(anyInt()));
        assertThat(e, instanceOf(DeleteTaskBadResponseException.class));
        assertEquals(e.getCode(), WRONG_TASK_ID.name());
    }

    @Test
    void shouldThrowErrorIfNotFound() {
        doThrow(new EmptyResultDataAccessException(0)).when(mockRepo).deleteById(any());
        DeleteTaskBadResponseException e = assertThrows(DeleteTaskBadResponseException.class, () -> victim.deleteTask(anyInt()));
        assertThat(e, instanceOf(DeleteTaskBadResponseException.class));
        assertEquals(e.getCode(), TASK_NOT_FOUND.name());
    }

    @Test
    void shouldThrowUnexpectedError() {
        doThrow(new RuntimeException("TEST")).when(mockRepo).deleteById(any());
        DeleteTaskBadResponseException e = assertThrows(DeleteTaskBadResponseException.class, () -> victim.deleteTask(anyInt()));
        assertThat(e, instanceOf(DeleteTaskBadResponseException.class));
        assertEquals(e.getCode(), UNEXPECTED_ERROR.name());
    }

    @Test
    void shouldEditTask() {
        assertEquals(TEST_TASK_DTO, victim.editTask(1, TEST_TASK_DTO));
    }

    @Test
    void shouldThrowEmptyNameException_editTask() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new PropertyValueException("test", "test", "name"));
        doThrow(srcE).when(mockRepo).save(any());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.editTask(1, TEST_TASK_DTO));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_NAME.name());
    }

    @Test
    void shouldThrowDuplicateNameException_editTask() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new ConstraintViolationException("test", new SQLException(), "description"));
        doThrow(srcE).when(mockRepo).save(any());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.editTask(1, TEST_TASK_DTO));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), DUPLICATED_TASK_NAME.name());
    }

    @Test
    void shouldThrowEmptyDescriptionException_editTask() {
        Exception srcE = new DataIntegrityViolationException("test");
        srcE.initCause(new PropertyValueException("test", "test", "description"));
        doThrow(srcE).when(mockRepo).save(any());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.editTask(1, TEST_TASK_DTO));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), EMPTY_TASK_FIELD_DESCRIPTION.name());
    }

    @Test
    void shouldThrowEntityNotFoundException_editTask() {
        Exception srcE = new EntityNotFoundException("test");
        doThrow(srcE).when(mockRepo).save(any());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.editTask(1, TEST_TASK_DTO));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), TASK_NOT_FOUND.name());
    }

    @Test
    void shouldThrowUnexpectedException_editTask() {
        Exception srcE = new RuntimeException("test_runtime_exception");
        doThrow(srcE).when(mockRepo).save(any());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.editTask(1, TEST_TASK_DTO));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), UNEXPECTED_ERROR.name());
    }

    @Test
    void shouldListAllTasks() {
        List<TaskDto> gotList = victim.list();
        assertEquals(1, gotList.size());
        assertEquals(gotList.get(0), TEST_TASK_DTO);
    }

    @Test
    void shouldCompleteTask() {
        victim.completeTask(0);
        verify(mockRepo, times(1)).save(any());
    }

    @Test
    void shouldThrowErrorIfNotFound_Complete() {
        doThrow(new EntityNotFoundException())
                .when(mockRepo).getOne(anyInt());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.completeTask(anyInt()));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), TASK_NOT_FOUND.name());
    }

    @Test
    void shouldThrowUnexpectedError_Complete() {
        doThrow(new RuntimeException("TEST"))
                .when(mockRepo).getOne(anyInt());
        EditTaskBadResponseException e = assertThrows(EditTaskBadResponseException.class, () -> victim.completeTask(anyInt()));
        assertThat(e, instanceOf(EditTaskBadResponseException.class));
        assertEquals(e.getCode(), UNEXPECTED_ERROR.name());
    }


}
