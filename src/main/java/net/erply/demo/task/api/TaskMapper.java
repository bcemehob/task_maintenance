package net.erply.demo.task.api;

import org.mapstruct.Mapper;
import net.erply.demo.conf.MapstructConfig;
import net.erply.demo.base.entity.Task;

@Mapper(config = MapstructConfig.class)
public abstract class TaskMapper {


//    @Mapping(source="name", target="name")
    public abstract TaskDto mapToDto(Task val);
}
