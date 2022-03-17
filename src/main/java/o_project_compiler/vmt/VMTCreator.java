

package o_project_compiler.vmt;

import o_project_compiler.exceptions.WrongObjectException;
import o_project_compiler.inheritancetree.InheritanceTree;
import o_project_compiler.inheritancetree.InheritanceTreeNode;
import o_project_compiler.inheritancetree.visitor.InheritanceTreeVisitor;
import o_project_compiler.util.Utils;
import rs.etf.pp1.symboltable.concepts.Obj;


public class VMTCreator implements InheritanceTreeVisitor {

    private void updateVMTs(InheritanceTreeNode node, Obj overriddenMethod) {
        try {
            node.getVMT().add(overriddenMethod);
        } catch (WrongObjectException e) {
            e.printStackTrace();
        }
        for (InheritanceTreeNode child : node.getChildren()) {
            boolean childVisited = false;
            for (Obj member : child.getClss().getType().getMembers()) {
                if (member.getKind() == Obj.Meth) {
                    try {
                        if (Utils.haveSameSignatures(member, overriddenMethod)) {
                            updateVMTs(child, member);
                            childVisited = true;
                            break;
                        }
                    } catch (WrongObjectException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!childVisited) {
                updateVMTs(child, overriddenMethod);
            }
        }
    }


    public void visit(InheritanceTreeNode node) {
        if (!node.equals(InheritanceTree.ROOT_NODE)) {
            for (Obj member : node.getClss().getType().getMembers()) {
                if (member.getKind() == Obj.Meth) {
                    InheritanceTreeNode parent = node.getParent();
                    while (!parent.equals(InheritanceTree.ROOT_NODE)) {
                        boolean overridenMethodFound = false;
                        for (Obj parentMember : parent.getClss().getType().getMembers()) {
                            if (parentMember.getKind() == Obj.Meth) {
                                try {
                                    if (Utils.haveSameSignatures(member, parentMember)) {
                                        if (Utils.returnTypesAssignmentCompatible(member, parentMember)) {
                                            overridenMethodFound = true;
                                            updateVMTs(parent, parentMember);
                                            break;
                                        }
                                    }
                                } catch (WrongObjectException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (overridenMethodFound) {
                            break;
                        }
                        parent = parent.getParent();
                    }
                }
            }
        }
    }

}
