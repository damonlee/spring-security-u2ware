package io.github.u2ware.sample.accounts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import io.github.u2ware.sample.accounts.Account.Authority.AuthorityDeserializer;
import lombok.Data;

@Entity
@EntityListeners({ AuditingEntityListener.class })
@Table(name = "USER_ACCOUNT")
public @Data class Account {

	@Id
	private  UUID uuid;

	@Column(length = 512, nullable = false)
	private  @JsonIgnore String password;
	@Column(nullable = false)
	private  String username;
	private  String nickname;
	private  boolean enabled = true;

	@CreatedBy
	protected  String insertedUser;

	@CreatedDate
	protected  Long insertedDatetime;

	@LastModifiedBy
	protected  String updatedUser;

	@LastModifiedDate
	protected  Long updatedDatetime;

	@OneToMany(mappedBy = "account", fetch = FetchType.EAGER, cascade = { CascadeType.ALL }, orphanRemoval = true)
	private  @JsonIgnore Collection<Authority> authorities = new ArrayList<Authority>();

	public void setAuthorities(Collection<Authority> authorities) {
		this.authorities = authorities;
		for(Authority a : this.authorities) {
			a.setAccount(this);
		}
	}

	@Entity
	@Table(name = "USER_ACCOUNT_AUTHORITY")
	@JsonSerialize(using=ToStringSerializer.class)
	@JsonDeserialize(using=AuthorityDeserializer.class)
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

		public static class  AuthorityDeserializer extends StdDeserializer<Authority>{
			protected AuthorityDeserializer() {
				super(Authority.class);
			}
			@Override
			public Authority deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				Authority a = new Authority();
				a.setAuthority(p.getText());
				return a;
			}
		}
	}
}
