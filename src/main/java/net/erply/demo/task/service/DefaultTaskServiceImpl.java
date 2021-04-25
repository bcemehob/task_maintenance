package net.erply.demo.task.service;

import static net.erply.demo.base.entity.ErrorCode.DUPLICATED_TASK_NAME;
import static net.erply.demo.base.entity.ErrorCode.EMPTY_TASK_FIELD;
import static net.erply.demo.base.entity.ErrorCode.EMPTY_TASK_FIELD_DESCRIPTION;
import static net.erply.demo.base.entity.ErrorCode.EMPTY_TASK_FIELD_NAME;
import static net.erply.demo.base.entity.ErrorCode.TASK_NOT_FOUND;
import static net.erply.demo.base.entity.ErrorCode.UNEXPECTED_ERROR;
import static net.erply.demo.base.entity.ErrorCode.WRONG_TASK_ID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.hibernate.PropertyValueException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.erply.demo.base.exception.AddTaskBadResponseException;
import net.erply.demo.base.exception.DeleteTaskBadResponseException;
import net.erply.demo.base.exception.EditTaskBadResponseException;
import net.erply.demo.task.api.TaskDto;
import net.erply.demo.task.api.TaskMapper;
import net.erply.demo.task.dao.TaskDao;
import net.erply.demo.base.entity.Task;
import net.erply.demo.task.entity.TaskStatus;

@Slf4j
@Service
public class DefaultTaskServiceImpl extends DefaultTaskService {

    private final TaskDao dao;

    private final TaskMapper mapper;

    public DefaultTaskServiceImpl(TaskDao dao, TaskMapper mapper) {
        this.dao = dao;
        this.mapper = mapper;
    }

    @Override
    public TaskDto addTask(TaskDto taskDto) {
        Task task = checkAndConvertNewTask(taskDto, null);
        return addTask(task);
    }

    @Override
    public void deleteTask(Integer id) throws DeleteTaskBadResponseException{
        try {
            dao.deleteById(id);
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("Error delete task: wrong id  {} ", id, e);
            throw new DeleteTaskBadResponseException(WRONG_TASK_ID.name());
        } catch (EmptyResultDataAccessException e) {
            log.error("Error delete task: not found task with id  {} ", id, e);
            throw new DeleteTaskBadResponseException(TASK_NOT_FOUND.name());
        } catch (Exception e) {
            log.error("Error delete task with id  {} ", id, e);
            throw new DeleteTaskBadResponseException(UNEXPECTED_ERROR.name());
        }
    }

    @Override
    public TaskDto editTask(Integer id, TaskDto taskDto) {
        try {
            Task task = checkAndConvertNewTask(taskDto, id);
            return mapper.mapToDto(dao.update(task));
        } catch (DataIntegrityViolationException e) {
            log.error("Error editing task: wrong entity format ", e);
            throw new EditTaskBadResponseException(getErrorCodeForEmptyFieldOrDuplicatedName(e));
        } catch (EntityNotFoundException e) {
            log.error("Error editing task: no entity with id {} ", id, e);
            throw new EditTaskBadResponseException(TASK_NOT_FOUND.name());
        } catch (Exception e) {
            log.error("Error editing task: ", e);
            throw new EditTaskBadResponseException(UNEXPECTED_ERROR.name());
        }
    }
    @Override
    public void completeTask(Integer id) {
        try {
            dao.complete(id);
        } catch (EntityNotFoundException e) {
            log.error("Error editing task: no entity with id {} ", id, e);
            throw new EditTaskBadResponseException(TASK_NOT_FOUND.name());
        } catch (Exception e) {
            log.error("Error editing task: ", e);
            throw new EditTaskBadResponseException(UNEXPECTED_ERROR.name());
        }
    }



    @Override
    public List<TaskDto> list() {
        return dao.getAll().stream()
                .map(mapper::mapToDto)
                .collect(Collectors.toList());
    }


    private Task checkAndConvertNewTask(TaskDto dto, Integer id) {
        if(!StringUtils.hasText(dto.getName())) {
            log.warn("Task {} name is empty", id);
            throw new AddTaskBadResponseException(EMPTY_TASK_FIELD_NAME.name());
        }
        if(!StringUtils.hasText(dto.getDescription())) {
            log.warn("Task {} description is empty", id);
            throw new AddTaskBadResponseException(EMPTY_TASK_FIELD_DESCRIPTION.name());
        }
        if (Optional.ofNullable(dto.getStatus()).isEmpty()) {
            dto.setStatus(TaskStatus.AVAILABLE);
        }
        return Task.builder()
                .id(id)
                .name(dto.getName())
                .description(dto.getDescription())
                .status(dto.getStatus())
                .build();
    }

    private TaskDto addTask(Task task) throws AddTaskBadResponseException {
        try {
            task = dao.save(task);
            return mapper.mapToDto(task);
        } catch (DataIntegrityViolationException e) {
            throw new AddTaskBadResponseException(getErrorCodeForEmptyFieldOrDuplicatedName(e));
        } catch (Exception e) {
            log.error("Unknown error adding task: ", e);
            throw new AddTaskBadResponseException(UNEXPECTED_ERROR.name());
        }
    }

    private String getErrorCodeForEmptyFieldOrDuplicatedName(DataIntegrityViolationException e) {
        String errorCode = UNEXPECTED_ERROR.name();
        log.error("Error adding task: ", e);
        if (e.getCause() instanceof PropertyValueException) {
            PropertyValueException pve = (PropertyValueException) e.getCause();
            errorCode = EMPTY_TASK_FIELD.name();
            if(StringUtils.hasText(pve.getPropertyName())) {
                errorCode += "_" + pve.getPropertyName().toUpperCase();
            }
        } else if(e.getCause() instanceof ConstraintViolationException) {
            errorCode = DUPLICATED_TASK_NAME.name();
        }
        return errorCode;
    }


}
