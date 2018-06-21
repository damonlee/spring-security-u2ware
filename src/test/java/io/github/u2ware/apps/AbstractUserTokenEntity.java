package io.github.u2ware.apps;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public abstract class AbstractUserTokenEntity {

	@Id
	private @Getter @Setter String series;
	private @Getter @Setter String username;
	private @Getter @Setter String tokenValue;
	private @Getter @Setter Long tokenDate;

}
