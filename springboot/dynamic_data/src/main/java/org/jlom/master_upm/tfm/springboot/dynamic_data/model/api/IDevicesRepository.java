package org.jlom.master_upm.tfm.springboot.dynamic_data.model.api;

import org.springframework.stereotype.Repository;

@Repository
public interface IDevicesRepository extends DevicesCommandRepository, DevicesQueryRepository {
}
