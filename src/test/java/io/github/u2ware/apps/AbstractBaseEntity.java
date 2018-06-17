package io.github.u2ware.apps;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class AbstractBaseEntity {

	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name="username", column = @Column(name="inserted_username") ),
		@AttributeOverride(name="timestamp", column = @Column(name="inserted_datetime") )
    })
	protected @CreatedBy Auditing insertedAuditing;

	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name="username", column = @Column(name="updated_username") ),
		@AttributeOverride(name="timestamp", column = @Column(name="updated_datetime") )
    })
	protected @LastModifiedBy Auditing updatedAuditing;
	
	
	@Data
	public static class Auditing{
		private String username;
		private Long datetime;
	}
}
