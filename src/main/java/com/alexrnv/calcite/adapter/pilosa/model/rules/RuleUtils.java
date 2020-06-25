package com.alexrnv.calcite.adapter.pilosa.model.rules;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeField;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

class RuleUtils {

    static boolean equalNameAndType(RelDataTypeField field1, RelDataTypeField field2) {
        requireNonNull(field2);
        requireNonNull(field1);
        requireNonNull(field1.getType());
        requireNonNull(field2.getType());
        requireNonNull(field1.getName());
        requireNonNull(field2.getName());

        return field2.getType().equals(field1.getType()) && field2.getName().equalsIgnoreCase(field1.getName());
    }

    static List<Integer> deriveGroupingIndexesFromInputAndOutputRows(RelDataType inputRowType, RelDataType outputRowType) {
        List<Integer> groupingFieldIndexes = new ArrayList<>();

        for (RelDataTypeField outputField : outputRowType.getFieldList()) {
            List<RelDataTypeField> inputRowFieldList = inputRowType.getFieldList();
            for (int inputFieldIndex = 0; inputFieldIndex < inputRowFieldList.size(); inputFieldIndex++) {
                RelDataTypeField inputField = inputRowFieldList.get(inputFieldIndex);
                if (RuleUtils.equalNameAndType(inputField, outputField)) {
                    groupingFieldIndexes.add(inputFieldIndex);
                }
            }
        }
        return groupingFieldIndexes;
    }

}
