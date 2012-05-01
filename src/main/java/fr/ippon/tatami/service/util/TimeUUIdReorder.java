package fr.ippon.tatami.service.util;

import java.util.StringTokenizer;

public class TimeUUIdReorder
{

	public static String reorderTimeUUId(String originalTimeUUID)
	{
		StringTokenizer tokens = new StringTokenizer(originalTimeUUID, "-");
		if (tokens.countTokens() == 5)
		{
			String time_low = tokens.nextToken();
			String time_mid = tokens.nextToken();
			String time_high_and_version = tokens.nextToken();
			String variant_and_sequence = tokens.nextToken();
			String node = tokens.nextToken();

			return time_high_and_version + '-' + time_mid + '-' + time_low + '-' + node + '-' + variant_and_sequence;

		}

		return originalTimeUUID;
	}
}
