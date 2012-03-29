package fr.ippon.tatami.repository.cassandra;

import java.util.Collection;

import javax.inject.Inject;

import me.prettyprint.hom.EntityManagerImpl;

import org.springframework.stereotype.Repository;

import fr.ippon.tatami.domain.TagLine;
import fr.ippon.tatami.repository.TagRepository;

@Repository
public class CassandraTagRepository implements TagRepository
{
	@Inject
	private EntityManagerImpl em;

	@Override
	public void addTweet(String tag, String tweetId)
	{
		TagLine tagLine = em.find(TagLine.class, tag);
		if (tagLine == null)
		{
			tagLine = new TagLine();
			tagLine.setTag(tag);
		}

		tagLine.getTweetIds().add(tweetId);
		em.persist(tagLine);
	}

	@Override
	public void removeTweet(String tag, String tweetId)
	{
		TagLine tagLine = em.find(TagLine.class, tag);
		if (tagLine == null)
		{
			// TODO Functional exception
			return;
		}

		tagLine.getTweetIds().remove(tweetId);
		em.persist(tagLine);

	}

	@Override
	public Collection<String> findTweetsForTag(String tag)
	{
		TagLine tagLine = em.find(TagLine.class, tag);
		if (tagLine != null)
		{
			return tagLine.getTweetIds();
		}
		return null;
	}

}
