package com.alexrnv.calcite.adapter.pilosa.pilosa.client;

import java.io.InputStream;

interface QueryMarshaller<T> {
    T readFromStream(InputStream stream);
}
