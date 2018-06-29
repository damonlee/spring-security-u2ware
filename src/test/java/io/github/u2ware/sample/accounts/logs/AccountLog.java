package io.github.u2ware.sample.accounts.logs;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ACCOUNT_EVENT")
public @Data class AccountLog {

	@Id
	@GeneratedValue
	private Long seq;

	private String username;
	private String eventType;
	private Long eventDate;

}