package com.alexrnv.calcite.adapter.pilosa.avatica;

import org.apache.calcite.avatica.Meta;
import org.apache.calcite.avatica.remote.LocalService;

public class LocalServiceWrapper extends LocalService {

    public LocalServiceWrapper(Meta meta) {
        super(meta);
    }

    @Override
    public ResultSetResponse toResponse(Meta.MetaResultSet resultSet) {
        ResultSetResponse resultSetResponse = super.toResponse(resultSet);
        return overrideMapCursorFactoryWithUndefinedFieldNamesByList(resultSetResponse);
    }

    private ResultSetResponse overrideMapCursorFactoryWithUndefinedFieldNamesByList(ResultSetResponse resultSetResponse) {
        if (needOverrideToListCursorFactory(resultSetResponse.signature.cursorFactory)) {
            Meta.Signature signatureWithListFactory = resultSetResponse.signature.setCursorFactory(Meta.CursorFactory.LIST);
            return new ResultSetResponse(resultSetResponse.connectionId, resultSetResponse.statementId, resultSetResponse.ownStatement,
                    signatureWithListFactory, resultSetResponse.firstFrame, resultSetResponse.updateCount, resultSetResponse.rpcMetadata);
        }
        return resultSetResponse;
    }

    private boolean needOverrideToListCursorFactory(Meta.CursorFactory cursorFactory) {
        return isMapCursorFactory(cursorFactory) && cursorFactoryFieldNamesNotDefined(cursorFactory);
    }

    private boolean isMapCursorFactory(Meta.CursorFactory cursorFactory) {
        return cursorFactory.style == Meta.Style.MAP;
    }

    private boolean cursorFactoryFieldNamesNotDefined(Meta.CursorFactory cursorFactory) {
        return cursorFactory.fieldNames == null;
    }

}
