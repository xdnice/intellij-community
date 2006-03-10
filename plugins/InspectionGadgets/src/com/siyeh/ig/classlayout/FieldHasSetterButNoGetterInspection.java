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
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PropertyUtil;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.FieldInspection;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

public class FieldHasSetterButNoGetterInspection extends FieldInspection {

    public String getGroupDisplayName() {
        return GroupNames.JAVABEANS_GROUP_NAME;
    }

    @NotNull
    protected String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "field.has.setter.but.no.getter.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new StaticNonFinalFieldVisitor();
    }

    private static class StaticNonFinalFieldVisitor
            extends BaseInspectionVisitor {

        public void visitField(@NotNull PsiField field) {
            final PsiManager psiManager = field.getManager();
            final Project project = psiManager.getProject();
            final String propertyName =
                    PropertyUtil.suggestPropertyName(project, field);
            final boolean isStatic =
                    field.hasModifierProperty(PsiModifier.STATIC);
            final PsiClass containingClass = field.getContainingClass();
            final PsiMethod setter =
                    PropertyUtil.findPropertySetter(containingClass,
                            propertyName, isStatic, false);
            if (setter == null) {
                return;
            }
            final PsiMethod getter =
                    PropertyUtil.findPropertyGetter(containingClass,
                            propertyName, isStatic, false);
            if (getter != null) {
                return;
            }
            registerFieldError(field);
        }
    }
}