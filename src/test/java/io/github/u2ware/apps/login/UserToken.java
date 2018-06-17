package io.github.u2ware.apps.login;

import javax.persistence.Entity;

import io.github.u2ware.apps.AbstractUserTokenEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @EqualsAndHashCode(callSuper=false)
@Entity
public class UserToken extends AbstractUserTokenEntity{

}
