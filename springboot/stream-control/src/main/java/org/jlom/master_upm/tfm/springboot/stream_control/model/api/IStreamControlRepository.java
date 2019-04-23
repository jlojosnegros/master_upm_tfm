package org.jlom.master_upm.tfm.springboot.stream_control.model.api;

import org.springframework.stereotype.Repository;

@Repository
public interface IStreamControlRepository extends StreamControlRepositoryCommands, StreamControlRepositoryQueries {
}
