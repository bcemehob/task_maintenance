package net.erply.demo.task.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import net.erply.demo.base.entity.Task;

public interface TaskJpaRepo extends JpaRepository<Task, Integer>, QueryByExampleExecutor<Task> {

}
