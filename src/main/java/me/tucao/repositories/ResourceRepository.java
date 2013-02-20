package me.tucao.repositories;

import me.tucao.domains.Resource;

public interface ResourceRepository extends 
		AtomicOperationsRepository<Resource, String> {
	
	Resource getByResId(String resId);
}
