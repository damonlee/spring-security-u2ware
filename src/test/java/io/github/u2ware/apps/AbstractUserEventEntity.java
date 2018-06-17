package io.github.u2ware.apps;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractUserEventEntity {

	@Id @GeneratedValue 
	private Long seq;

	private String username;
	private String eventType;
	private Long eventTimestamp;
}