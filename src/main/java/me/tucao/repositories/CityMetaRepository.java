package me.tucao.repositories;

import me.tucao.domains.CityMeta;

public interface CityMetaRepository extends
		AtomicOperationsRepository<CityMeta, String> {
	
	CityMeta getByPinyin(String pinyin);
	
	CityMeta getByName(String name);
}
