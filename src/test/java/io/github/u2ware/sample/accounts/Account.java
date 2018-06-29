package io.github.u2ware.sample.accounts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners({ AuditingEntityListener.class })
@Table(name = "USER_ACCOUNT")
public class Account {

	@Id
	private @Getter @Setter UUID uuid;

	@Column(length = 512, nullable = false)
	private @Getter @Setter @JsonIgnore String password;
	@Column(nullable = false)
	private @Getter @Setter String username;
	private @Getter @Setter String nickname;
	private @Getter @Setter boolean enabled = true;

	@CreatedBy
	protected @Getter @Setter String insertedUser;

	@CreatedDate
	protected @Getter @Setter Long insertedDatetime;

	@LastModifiedBy
	protected @Getter @Setter String updatedUser;

	@LastModifiedDate
	protected @Getter @Setter Long updatedDatetime;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	private @Getter @Setter @JsonIgnore Collection<Authority> authorities = new ArrayList<Authority>();

	@Entity
	@Table(name = "USER_ACCOUNT_AUTHORITY")
	public @SuppressWarnings("serial") @Data static class Authority implements GrantedAuthority {

		private @Id @GeneratedValue Long id;
		private @ManyToOne(fetch = FetchType.LAZY) @JsonIgnore Account account;
		private @NotNull String authority;

		public Authority() {
		}

		protected Authority(Account account, String authority) {
			this.account = account;
			this.authority = authority;
		}

		public String toString() {
			return authority;
		}
	}

	/////////////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////////////
	@Transient
	public void setRoles(String... authoritiesValues) {
		List<Authority> authorities = new ArrayList<Authority>();
		for (String authority : authoritiesValues) {
			authorities.add(new Authority(this, authority));
		}
		getAuthorities().clear();
		getAuthorities().addAll(authorities);
	}

	@Transient
	public String[] getRoles() {
		List<String> authorities = new ArrayList<String>();
		for (Authority authority : getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		String[] r = new String[authorities.size()];
		authorities.toArray(r);
		return r;
	}

	public @Transient boolean hasRoles(String... roles) {
		for (Authority authority : getAuthorities()) {
			for (String role : roles) {
				if (authority.getAuthority().equals(role)) {
					return true;
				}
			}
		}
		return false;
	}
}
