package io.github.u2ware.apps;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractUserEventEntity {

	@Id @GeneratedValue 
	private @Getter @Setter Long seq;
	private @Getter @Setter String username;
	private @Getter @Setter String eventType;
	private @Getter @Setter Long eventTimestamp;
}