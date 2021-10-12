/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.xoai.tests.helpers.stubs;

import java.util.HashMap;
import java.util.Map;

import org.dspace.core.Context;
import org.dspace.xoai.services.api.FieldResolver;

public class StubbedFieldResolver implements FieldResolver {
    private final Map<String, Integer> fieldsMap = new HashMap<>();

    @Override
    public int getFieldID(Context context, String field) {
        Integer integer = fieldsMap.get(field);
        if (integer == null) {
            return -1;
        }
        return integer;
    }

    public StubbedFieldResolver hasField(String field, int id) {
        fieldsMap.put(field, id);
        return this;
    }
}
