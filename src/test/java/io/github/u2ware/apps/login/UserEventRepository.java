package io.github.u2ware.apps.login;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource(exported=false)
public interface UserEventRepository extends PagingAndSortingRepository<UserEvent, Long>, JpaSpecificationExecutor<UserEvent>{

}
