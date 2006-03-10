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
package com.siyeh.ig.junit;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.psiutils.TestUtils;
import com.siyeh.ig.psiutils.ClassUtils;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

public class TestCaseWithNoTestMethodsInspection extends ClassInspection {

    public String getID() {
        return "JUnitTestCaseWithNoTests";
    }

    public String getGroupDisplayName() {
        return GroupNames.JUNIT_GROUP_NAME;
    }

    @NotNull
    protected String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "test.case.with.no.test.methods.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new TestCaseWithNoTestMethodsVisitor();
    }

    private static class TestCaseWithNoTestMethodsVisitor
            extends BaseInspectionVisitor {

        public void visitClass(@NotNull PsiClass aClass) {
            if (aClass.isInterface()
                    || aClass.isEnum()
                    || aClass.isAnnotationType()
                    || aClass.hasModifierProperty(PsiModifier.ABSTRACT)) {
                return;
            }
            if (aClass instanceof PsiTypeParameter) {
                return;
            }
            if (!ClassUtils.isSubclass(aClass, "junit.framework.TestCase")) {
                return;
            }
            final PsiMethod[] methods = aClass.getMethods();
            for (final PsiMethod method : methods) {
                if (TestUtils.isJUnitTestMethod(method)) {
                    return;
                }
            }
            registerClassError(aClass);
        }
    }
}