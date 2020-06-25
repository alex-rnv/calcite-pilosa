package com.alexrnv.calcite.adapter.pilosa.model.rules;

import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaExcept;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaIntersect;
import com.alexrnv.calcite.adapter.pilosa.model.rels.PilosaSetOp;
import org.apache.calcite.rel.RelNode;
import org.apache.commons.lang3.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public abstract class SetOperationsFolder {

    RelNode fold(RelNode leftRel, RelNode rightRel) {
        if (leftRel instanceof PilosaSetOp) {
            return foldSetOp((PilosaSetOp) leftRel, rightRel);
        } else if (rightRel instanceof PilosaSetOp) {
            return foldSetOp((PilosaSetOp) rightRel, leftRel);
        } else {
            return foldProjectProject(leftRel, rightRel);
        }
    }

    private RelNode foldSetOp(PilosaSetOp leftSetOp, RelNode rightRel) {
        if (leftSetOp instanceof PilosaIntersect) {
            return foldIntersect(((PilosaIntersect) leftSetOp), rightRel);
        } else if (leftSetOp instanceof PilosaExcept) {
            return foldExcept(((PilosaExcept) leftSetOp), rightRel);
        } else {
            throw new RuntimeException("folding algorithm is not defined for set operation " + leftSetOp.getClass().getName());
        }
    }

    private RelNode foldIntersect(PilosaIntersect leftIntersect, RelNode rightRel) {
        if (rightRel instanceof PilosaIntersect) {
            return foldIntersectIntersect(leftIntersect, (PilosaIntersect) rightRel);
        } else if (rightRel instanceof PilosaExcept) {
            return foldIntersectExcept(leftIntersect, (PilosaExcept) rightRel);
        } else {
            return foldSetOpProject(leftIntersect, rightRel);
        }
    }

    private RelNode foldExcept(PilosaExcept leftExcept, RelNode rightRel) {
        if (rightRel instanceof PilosaIntersect) {
            return foldIntersectExcept((PilosaIntersect) rightRel, leftExcept);
        } else if (rightRel instanceof PilosaExcept) {
            return foldExceptExcept(leftExcept, ((PilosaExcept) rightRel));
        } else {
            return foldSetOpProject(leftExcept, rightRel);
        }
    }

    private PilosaIntersect foldIntersectIntersect(PilosaIntersect leftIntersect, PilosaIntersect rightIntersect) {
        List<RelNode> inputsLeft = new ArrayList<>(leftIntersect.getInputs());
        inputsLeft.addAll(rightIntersect.getInputs());
        return new PilosaIntersect(leftIntersect.getCluster(), leftIntersect.getTraitSet(),
                leftIntersect.getRowType(), inputsLeft);
    }

    private PilosaSetOp foldExceptExcept(PilosaExcept leftExcept, PilosaExcept rightExcept) {
        List<RelNode> inputsLeft = new ArrayList<>(leftExcept.getInputs());
        inputsLeft.addAll(rightExcept.getInputs());
        return new PilosaExcept(leftExcept.getCluster(), leftExcept.getTraitSet(),
                leftExcept.getRowType(), inputsLeft);
    }

    private PilosaSetOp foldIntersectExcept(PilosaIntersect leftIntersect, PilosaExcept rightExcept) {
        throw new NotImplementedException("not implemented yet");
    }

    private RelNode foldSetOpProject(PilosaSetOp setOp, RelNode project) {
        List<RelNode> inputs = new ArrayList<>(setOp.getInputs());
        inputs.add(project);
        return setOp.copy(setOp.getTraitSet(), inputs);
    }

    protected abstract RelNode foldProjectProject(RelNode leftProject, RelNode rightProject);
}
