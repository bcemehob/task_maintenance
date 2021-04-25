package net.erply.demo.task.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import net.erply.demo.base.dao.Dao;
import net.erply.demo.base.entity.Task;
import net.erply.demo.task.entity.TaskStatus;

@Component("task_dao")
public class TaskDao implements Dao<Task> {

    private final TaskJpaRepo repo;

    public TaskDao(TaskJpaRepo repo) {
        this.repo = repo;
    }

    @Override
    public Task save(Task task) {
        return repo.save(task);
    }

    @Override
    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    @Override
    public Task update(Task updated) {
        repo.getOne(updated.getId());
        return repo.save(updated);
    }

    public void complete(Integer id) {
        Task task = repo.getOne(id);
        task.setStatus(TaskStatus.COMPLETED);
        repo.save(task);
    }

    @Override
    public List<Task> getAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return Optional.empty();
    }
}
