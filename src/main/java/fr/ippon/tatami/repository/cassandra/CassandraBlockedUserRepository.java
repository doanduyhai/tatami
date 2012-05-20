package fr.ippon.tatami.repository.cassandra;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USERBLOCK_CF;
import static fr.ippon.tatami.config.CounterKeys.USERBLOCK_COUNTER;

import java.util.Collection;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import fr.ippon.tatami.repository.BlockedUserRepository;

public class CassandraBlockedUserRepository extends CassandraAbstractRepository implements BlockedUserRepository
{

	@CacheEvict(value = "userblock-cache", key = "#userLogin")
	@Override
	public void addUserToBlockRepository(String userLogin, String blockedUserlogin)
	{
		this.insertIntoCF(USERBLOCK_CF, userLogin, blockedUserlogin);
		this.incrementCounter(USERBLOCK_COUNTER, userLogin);
	}

	@CacheEvict(value = "userblock-cache", key = "#userLogin")
	@Override
	public void removeUserFromBlockRepository(String userLogin, String blockedUserlogin)
	{
		this.removeFromCF(USERBLOCK_CF, userLogin, blockedUserlogin);
		this.decrementCounter(USERBLOCK_COUNTER, userLogin);

	}

	@Override
	@Cacheable(value = "userblock-cache", key = "#userLogin")
	public Collection<String> getUsersFromBlockRepository(String userLogin)
	{
		long blockedUserCount = this.getCounterValue(USERBLOCK_COUNTER, userLogin);
		return this.findRangeFromCF(USERBLOCK_CF, userLogin, null, false, (int) blockedUserCount);
	}

}
