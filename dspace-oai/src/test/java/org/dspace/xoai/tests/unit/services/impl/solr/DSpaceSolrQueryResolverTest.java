/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.xoai.tests.unit.services.impl.solr;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lyncode.xoai.dataprovider.filter.Scope;
import com.lyncode.xoai.dataprovider.filter.ScopedFilter;
import com.lyncode.xoai.dataprovider.filter.conditions.AndCondition;
import com.lyncode.xoai.dataprovider.filter.conditions.Condition;
import com.lyncode.xoai.dataprovider.filter.conditions.CustomCondition;
import com.lyncode.xoai.dataprovider.xml.xoaiconfig.parameters.ParameterList;
import com.lyncode.xoai.dataprovider.xml.xoaiconfig.parameters.ParameterMap;
import com.lyncode.xoai.dataprovider.xml.xoaiconfig.parameters.StringValue;
import org.dspace.xoai.filter.DSpaceMetadataExistsFilter;
import org.dspace.xoai.filter.DSpaceSetSpecFilter;
import org.dspace.xoai.filter.DateFromFilter;
import org.dspace.xoai.filter.DateUntilFilter;
import org.dspace.xoai.services.impl.solr.DSpaceSolrQueryResolver;
import org.dspace.xoai.tests.unit.services.impl.AbstractQueryResolverTest;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DSpaceSolrQueryResolverTest extends AbstractQueryResolverTest {
    private static final Date DATE = new Date();
    private static final String SET = "col_testSet";
    private static final String FIELD_1 = "dc.title";
    private static final String FIELD_2 = "dc.type";

    private DSpaceSolrQueryResolver underTest = new DSpaceSolrQueryResolver();

    @Before
    public void autowire() {
        autowire(underTest);
    }

    @After
    public void cleanup() {
        underTest = null;
    }

    @Test
    public void fromFilterQuery() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        scopedFilters.add(new ScopedFilter(() -> new DateFromFilter(DATE), Scope.Query));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result, is("((item.lastmodified:[" + escapedFromDate(DATE) + " TO *]))"));
    }

    @Test
    public void fromAndUntilFilterQuery() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        Condition fromCondition = () -> new DateFromFilter(DATE);
        Condition untilCondition = () -> new DateUntilFilter(DATE);
        scopedFilters.add(new ScopedFilter(new AndCondition(getFilterResolver(),
            fromCondition, untilCondition), Scope.Query));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result, is("(((item.lastmodified:[" + escapedFromDate(
            DATE) + " TO *]) AND (item.lastmodified:[* TO " + escapedUntilDate(DATE) + "])))"));
    }

    @Test
    public void customConditionForMetadataExistsFilterWithOneSingleValue() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        ParameterMap filterConfiguration = new ParameterMap().withValues(new StringValue()
            .withValue(FIELD_1)
            .withName("fields"));

        scopedFilters.add(new ScopedFilter(new CustomCondition(getFilterResolver(),
            DSpaceMetadataExistsFilter.class,
            filterConfiguration),
            Scope.Query));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result, is("(((metadata." + FIELD_1 + ":[* TO *])))"));
    }

    @Test
    public void customConditionForMetadataExistsFilterWithMultipleValues() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        ParameterMap filterConfiguration = new ParameterMap().withValues(new ParameterList()
            .withValues(
                new StringValue().withValue(FIELD_1),
                new StringValue().withValue(FIELD_2)
            )
            .withName("fields"));

        scopedFilters.add(new ScopedFilter(new CustomCondition(getFilterResolver(),
            DSpaceMetadataExistsFilter.class,
            filterConfiguration),
            Scope.Query));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result,
            is("(((metadata." + FIELD_1 + ":[* TO *] OR metadata." + FIELD_2 + ":[* TO *])))"));
    }

    @Test
    public void fromFilterInMetadataFormatScope() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        scopedFilters.add(new ScopedFilter(() -> new DateFromFilter(DATE), Scope.MetadataFormat));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result,
            is("((item.deleted:true OR (item.lastmodified:[" + escapedFromDate(DATE) + " TO *])))"));
    }

    @Test
    public void fromAndSetFilterQuery() {
        List<ScopedFilter> scopedFilters = new ArrayList<>();
        scopedFilters.add(new ScopedFilter(() -> new DateFromFilter(DATE), Scope.Query));
        scopedFilters.add(
            new ScopedFilter(() -> new DSpaceSetSpecFilter(collectionsService, handleResolver, SET), Scope.Query));

        String result = underTest.buildQuery(scopedFilters);

        MatcherAssert.assertThat(result, is("((item.lastmodified:[" + escapedFromDate(
            DATE) + " TO *])) AND ((item.collections:" + SET + "))"));
    }

}
