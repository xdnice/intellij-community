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
package com.siyeh.ig.controlflow;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiLabeledStatement;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.StatementInspection;
import com.siyeh.ig.StatementInspectionVisitor;
import com.siyeh.InspectionGadgetsBundle;
import org.jetbrains.annotations.NotNull;

public class LabeledStatementInspection extends StatementInspection {

    public String getGroupDisplayName() {
        return GroupNames.CONTROL_FLOW_GROUP_NAME;
    }

    @NotNull
    protected String buildErrorString(Object... infos) {
        return InspectionGadgetsBundle.message(
                "labeled.statement.problem.descriptor");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new LabeledStatementVisitor();
    }

    private static class LabeledStatementVisitor
            extends StatementInspectionVisitor {

        public void visitLabeledStatement(PsiLabeledStatement statement) {
            super.visitLabeledStatement(statement);
            final PsiIdentifier labelIdentifier =
                    statement.getLabelIdentifier();
            registerError(labelIdentifier);
        }
    }
}