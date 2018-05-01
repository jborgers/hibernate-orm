/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryplan;

import org.hibernate.test.type.BasicTypeRegistryTest;
import org.hibernate.type.CustomType;
import org.junit.Test;

import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQueryScalarReturn;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Tests equals() for NativeSQLQueryReturn implementations.
 *
 * @author Michael Stevens
 */
public class NativeSQLQueryPlanEqualsTest extends BaseCoreFunctionalTestCase {
	public String[] getMappings() {
		return new String[] {};
	}

	@Test
	public void testNativeSQLQuerySpecEquals() {
		QueryPlanCache cache = new QueryPlanCache( sessionFactory() );
		NativeSQLQuerySpecification firstSpec = createSpec();

		NativeSQLQuerySpecification secondSpec = createSpec();
		
		NativeSQLQueryPlan firstPlan = cache.getNativeSQLQueryPlan(firstSpec);
		NativeSQLQueryPlan secondPlan = cache.getNativeSQLQueryPlan(secondSpec);
		
		assertEquals(firstPlan, secondPlan);
		
	}

	private NativeSQLQuerySpecification createSpec() {
		String blah = "blah";
		String select = "select blah from blah";
		NativeSQLQueryReturn[] queryReturns = new NativeSQLQueryScalarReturn[] {
				new NativeSQLQueryScalarReturn( blah, sessionFactory().getTypeResolver().basic( "int" ) )
		};
		return new NativeSQLQuerySpecification( select, queryReturns, null );
	}

	@Test
	public void testNativeSQLQuerySpecEqualsFailsWithCustomTypeAndNoHashCodeEquals() {
		QueryPlanCache cache = new QueryPlanCache(sessionFactory());
		NativeSQLQuerySpecification firstSpec = createCustomTypeSpec();
		NativeSQLQuerySpecification secondSpec = createCustomTypeSpec();

		NativeSQLQueryPlan firstPlan = cache.getNativeSQLQueryPlan(firstSpec);
		NativeSQLQueryPlan secondPlan = cache.getNativeSQLQueryPlan(secondSpec);

		//Test will fail if either CustomType or specific UserType hashcode/equals are not implemented.
		//QueryPlanCache will contain 2 entries and keep growing in size;
		assertEquals(firstPlan, secondPlan);
	}

	private NativeSQLQuerySpecification createCustomTypeSpec() {
		String blah = "blah";
		String select = "select blah from blah";

		// UserType doesn't implement equals/hashCode: will fail
		// NativeSQLQueryReturn[] queryReturns = new NativeSQLQueryScalarReturn[]{
		//		new NativeSQLQueryScalarReturn(blah, new CustomType(new BasicTypeRegistryTest.TotallyIrrelevantUserType()))
		//};
		NativeSQLQueryReturn[] queryReturns = new NativeSQLQueryScalarReturn[]{
				new NativeSQLQueryScalarReturn(blah, new CustomType(new BasicTypeRegistryTest.UserTypeImplementingEqualsHashCode()))
		};
		return new NativeSQLQuerySpecification(select, queryReturns, null);
	}
}
