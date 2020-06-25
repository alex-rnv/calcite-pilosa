package com.alexrnv.calcite.adapter.pilosa.pilosa.converter;

import com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.FieldGroup;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.GroupByQueryResult;
import com.alexrnv.calcite.adapter.pilosa.pilosa.dto.GroupByQueryResultItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alexrnv.calcite.adapter.pilosa.pilosa.PilosaAPIError.ErrorCode.INVALID_RESPONSE;

public class GroupByResponseConverter extends QueryResponseConverter<GroupByQueryResult> {

    private final static Logger LOG = LoggerFactory.getLogger(GroupByResponseConverter.class);

    @Override
    public List<Object> convert(GroupByQueryResult queryResult) {
        return convertToArraysOfObjects(queryResult, header);
    }

    private List<Object> convertToArraysOfObjects(GroupByQueryResult groupByResult, List<String> header) {
        List<List<GroupByQueryResultItem>> results = groupByResult.getResults();
        if (results.isEmpty())
            return Collections.emptyList();

        if (results.size() > 1) {
            throw new PilosaAPIError(INVALID_RESPONSE, "more than one result returned: " + results.size());
        }

        validateHeader(groupByResult, header);

        List<Object> result = new ArrayList<>();
        for (GroupByQueryResultItem item : results.get(0)) {
            List<FieldGroup> group = item.getGroup();
            Object[] row = new Object[group.size() + 1];
            for (FieldGroup fieldGroup : group) {
                for (int i = 0; i < header.size(); i++) {
                    if (header.get(i).equalsIgnoreCase(fieldGroup.getField())) {
                        row[i] = fieldGroup.getRowID();
                        break;
                    }
                }
            }
            row[row.length - 1] = item.getCount();
            result.add(row);
        }

        return result;
    }

    private void validateHeader(GroupByQueryResult groupByResult, List<String> header) {

        List<List<GroupByQueryResultItem>> results = groupByResult.getResults();
        GroupByQueryResultItem firstItem = results.get(0).get(0);

        if (header.size() != firstItem.getGroup().size() + 1) {
            LOG.error("header and pilosa result do not match, different sizes," +
                    " header is expected to be longer by one item to accumulate count field:" +
                    " header={}, firstItem={}", header, firstItem);
            throw new PilosaAPIError(INVALID_RESPONSE, "header and pilosa result do not match");
        }

        for (FieldGroup fieldGroup : firstItem.getGroup()) {
            if (!listContainsIgnoreCase(header, fieldGroup.getField())) {
                LOG.error("header and pilosa result do not match:" +
                        " some fields are not present in header," +
                        " header={}, firstItem={}", header, firstItem);
                throw new PilosaAPIError(INVALID_RESPONSE, "field " + fieldGroup.getField() + " is not in the header");
            }
        }
    }

    private boolean listContainsIgnoreCase(List<String> list, String value) {
        for (String headerValues : list) {
            if (headerValues.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }


}
