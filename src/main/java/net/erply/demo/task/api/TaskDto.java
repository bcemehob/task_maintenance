package net.erply.demo.task.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.erply.demo.task.entity.TaskStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class TaskDto {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
}
