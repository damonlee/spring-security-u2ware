package io.github.u2ware.sample.signin;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ACCOUNT_TOKEN")
public @Data class AccountToken {

	@Id
	private String series;

	private String username;
	private String tokenValue;
	private Long tokenDate;

}
