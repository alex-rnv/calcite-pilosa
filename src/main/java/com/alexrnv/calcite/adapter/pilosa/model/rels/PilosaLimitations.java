package com.alexrnv.calcite.adapter.pilosa.model.rels;


import com.alexrnv.calcite.adapter.pilosa.model.PilosaCalciteModelError;
import com.alexrnv.calcite.adapter.pilosa.expression.PilosaAggExpression;

import java.util.List;

import static com.alexrnv.calcite.adapter.pilosa.model.PilosaCalciteModelError.ErrorCode.PILOSA_LIMITATION;

class PilosaLimitations {

    static void assertAggCalls(List<PilosaAggExpression.AggOperation> aggOperations) {
        if (aggOperations.size() > 1) {
            throw new PilosaCalciteModelError(PILOSA_LIMITATION, "multiple aggregates are conceptually not applicable to Pilosa");
        }
    }
}
