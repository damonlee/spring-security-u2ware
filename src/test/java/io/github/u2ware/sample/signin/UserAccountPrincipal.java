package io.github.u2ware.sample.sign;

public interface UserAccountPrincipal {

	public String getUsername();

	public String getNickname();

	public boolean hasRoles(String... roles);

}
