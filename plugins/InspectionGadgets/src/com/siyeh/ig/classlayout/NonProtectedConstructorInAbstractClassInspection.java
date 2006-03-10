/*
 * Copyright 2003-2006 Dave Griffith, Bas Leijdekkers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.siyeh.ig.classlayout;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.MethodInspection;
import com.siyeh.ig.fixes.MakeProtectedFix;
import com.siyeh.ig.ui.SingleCheckboxOptionsPanel;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class NonProtectedConstructorInAbstractClassInspection
        extends MethodInspection {

    /**
     * @noinspection PublicField
     */
    public boolean m_ignoreNonPublicClasses = false;

    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "non.protected.constructor.in.abstract.class.display.name");
    }

    public String getID() {
        return "ConstructorNotProtectedInAbstractClass";
    }

    public String getGroupDisplayName() {
        return GroupNames.INHERITANCE_GROUP_NAME;
    }

    @NotNull
    protected String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "non.protected.constructor.in.abstract.class.problem.descriptor");
    }

    public JComponent createOptionsPanel() {
        return new SingleCheckboxOptionsPanel(
                InspectionGadgetsBundle.message(
                        "non.protected.constructor.in.abstract.class.ignore.option"),
                this, "m_ignoreNonPublicClasses");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new NonProtectedConstructorInAbstractClassVisitor();
    }

    public InspectionGadgetsFix buildFix(PsiElement location) {
        return new MakeProtectedFix();
    }

    private class NonProtectedConstructorInAbstractClassVisitor
            extends BaseInspectionVisitor {

        public void visitMethod(@NotNull PsiMethod method) {
            //no call to super, so we don't drill into anonymous classes
            if (!method.isConstructor()) {
                return;
            }
            if (method.hasModifierProperty(PsiModifier.PROTECTED)
                || method.hasModifierProperty(PsiModifier.PRIVATE)) {
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null) {
                return;
            }
            if (m_ignoreNonPublicClasses &&
                !containingClass.hasModifierProperty(PsiModifier.PUBLIC)) {
                return;
            }
            if (!containingClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                return;
            }
            if (containingClass.isEnum()) {
                return;
            }
            registerMethodError(method);
        }
    }
}