package io.github.u2ware.apps;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class AbstractUserTokenEntity {

	private @Id String series;
	private String username;
	private String tokenValue;
	private Long tokenDate;
	
}
