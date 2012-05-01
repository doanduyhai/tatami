package fr.ippon.tatami.repository;

import static fr.ippon.tatami.config.ColumnFamilyKeys.USER_INDEX_CF;

import java.util.List;

import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

import org.testng.annotations.Test;

import fr.ippon.tatami.AbstractCassandraTatamiTest;

public class CompositeColumnTest extends AbstractCassandraTatamiTest
{
	@Test
	public void test()
	{
		CompositeSerializer ce = new CompositeSerializer();
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);

		Composite col = new Composite();
		col.addComponent("A", se).addComponent("A", se).addComponent("A", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("A", se).addComponent("B", se).addComponent("B", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("A", se).addComponent("B", se).addComponent("C", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("A", se).addComponent("C", se).addComponent("B", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("B", se).addComponent("A", se).addComponent("A", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("B", se).addComponent("B", se).addComponent("A", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("B", se).addComponent("B", se).addComponent("B", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("C", se).addComponent("A", se).addComponent("B", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		col = new Composite();
		col.addComponent("C", se).addComponent("B", se).addComponent("A", se);
		mutator.insert("test", USER_INDEX_CF, HFactory.createColumn(col, "", ce, oe));

		// Composite start = new Composite();
		// start.addComponent(0, "US", Composite.ComponentEquality.EQUAL);
		//
		// Composite end = new Composite();
		// end.addComponent(0, "US", Composite.ComponentEquality.GREATER_THAN_EQUAL);

		Composite start = new Composite();

		start.addComponent(0, "", Composite.ComponentEquality.EQUAL);
		start.addComponent(1, "B", Composite.ComponentEquality.EQUAL);
		// start.addComponent(2, "A", Composite.ComponentEquality.EQUAL);

		Composite end = new Composite(); // Character.MAX_VALUE + ""
		end.addComponent(0, Character.MAX_VALUE + "", Composite.ComponentEquality.GREATER_THAN_EQUAL);
		end.addComponent(1, "B", Composite.ComponentEquality.EQUAL);
		// end.addComponent(2, Character.MAX_VALUE + "", Composite.ComponentEquality.GREATER_THAN_EQUAL);

		List<HColumn<Composite, Object>> columns = HFactory.createSliceQuery(keyspace, se, ce, oe).setColumnFamily(USER_INDEX_CF).setKey("test")
				.setRange(start, end, false, 100).execute().get().getColumns();

		for (HColumn<Composite, Object> column : columns)
		{
			System.out.println(column.getName().get(0, se) + ":" + column.getName().get(1, se) + ":" + column.getName().get(2, se));
		}

	}
}
