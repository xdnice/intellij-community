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
package com.siyeh.ig.bugs;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.intellij.psi.util.TypeConversionUtil;
import com.siyeh.InspectionGadgetsBundle;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import com.siyeh.ig.psiutils.MethodCallUtils;
import org.jetbrains.annotations.NotNull;

public class EqualsBetweenInconvertibleTypesInspection
        extends ExpressionInspection {

    public String getDisplayName() {
        return InspectionGadgetsBundle.message(
                "equals.between.inconvertible.types.display.name");
    }

    public String getGroupDisplayName() {
        return GroupNames.BUGS_GROUP_NAME;
    }

    @NotNull
    public String buildErrorString(Object... infos) {
        final PsiType comparedType = (PsiType)infos[0];
        final PsiType comparisonType = (PsiType)infos[1];
        return InspectionGadgetsBundle.message(
                "equals.between.inconvertible.types.problem.descriptor",
                comparedType.getPresentableText(),
                comparisonType.getPresentableText());
    }

    public BaseInspectionVisitor buildVisitor() {
        return new EqualsBetweenInconvertibleTypesVisitor();
    }

    private static class EqualsBetweenInconvertibleTypesVisitor
            extends BaseInspectionVisitor {

        public void visitMethodCallExpression(
                @NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if(!MethodCallUtils.isEqualsCall(expression)){
                return;
            }
            final PsiReferenceExpression methodExpression =
                    expression.getMethodExpression();
            final PsiExpressionList argumentList = expression.getArgumentList();
            final PsiExpression[] args = argumentList.getExpressions();
            if (args.length != 1) {
                return;
            }
            final PsiExpression expression1 = args[0];
            final PsiExpression expression2 =
                    methodExpression.getQualifierExpression();
            if (expression2 == null) {
                return;
            }
            final PsiType comparedType = expression1.getType();
            if (comparedType == null) {
                return;
            }
            final PsiType comparisonType = expression2.getType();
            if (comparisonType == null) {
                return;
            }
            if (TypeConversionUtil.areTypesConvertible(comparedType,
                    comparisonType)) {
                return;
            }
            registerMethodCallError(expression, comparedType, comparisonType);
        }
    }
}