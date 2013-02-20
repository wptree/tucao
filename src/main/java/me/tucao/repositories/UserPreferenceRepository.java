package me.tucao.repositories;

import me.tucao.domains.User;
import me.tucao.domains.UserPreference;

public interface UserPreferenceRepository extends
		AtomicOperationsRepository<UserPreference, String> {
	
	UserPreference getByUser(User user);
}
