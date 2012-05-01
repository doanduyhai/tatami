package fr.ippon.tatami.repository;

import java.util.UUID;

import me.prettyprint.cassandra.utils.TimeUUIDUtils;

public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		UUID uid1 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		UUID uid2 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		UUID uid3 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		UUID uid4 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();
		UUID uid5 = TimeUUIDUtils.getUniqueTimeUUIDinMillis();

		System.out.println(" 1:" + uid1.timestamp());
		System.out.println(" 2:" + uid2.timestamp());
		System.out.println(" 3:" + uid3.timestamp());
		System.out.println(" 4:" + uid4.timestamp());
		System.out.println(" 5:" + uid5.timestamp());

	}
}
