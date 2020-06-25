package com.alexrnv.calcite.adapter.pilosa.model.rels;

import com.alexrnv.calcite.adapter.pilosa.model.PilosaRelTreeVisitor;

interface PilosaElement {
    void accept(PilosaRelTreeVisitor visitor);
}
