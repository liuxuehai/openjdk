/*
 * Copyright (c) 2014, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import jdk.test.lib.ExitCode;
import jdk.test.lib.Platform;
import jdk.test.lib.cli.CommandLineOptionTest;
import jdk.test.lib.cli.predicate.AndPredicate;
import jdk.test.lib.cli.predicate.OrPredicate;

/**
 * Generic test case for SHA-related options targeted to CPUs which
 * support instructions required by the tested option.
 */
public class GenericTestCaseForSupportedCPU extends
        SHAOptionsBase.TestCase {
    public GenericTestCaseForSupportedCPU(String optionName) {
        super(optionName,
                new AndPredicate(
                    new OrPredicate(Platform::isSparc, Platform::isAArch64),
                    SHAOptionsBase.getPredicateForOption(optionName)));
    }

    @Override
    protected void verifyWarnings() throws Throwable {

        String shouldPassMessage = String.format("JVM should start with option"
                + " '%s' without any warnings", optionName);
        // Verify that there are no warning when option is explicitly enabled.
        CommandLineOptionTest.verifySameJVMStartup(null, new String[] {
                        SHAOptionsBase.getWarningForUnsupportedCPU(optionName)
                }, shouldPassMessage, shouldPassMessage, ExitCode.OK,
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(optionName, true));

        // Verify that option could be disabled even if +UseSHA was passed to
        // JVM.
        CommandLineOptionTest.verifySameJVMStartup(null, new String[] {
                        SHAOptionsBase.getWarningForUnsupportedCPU(optionName)
                }, shouldPassMessage, String.format("It should be able to "
                        + "disable option '%s' even if %s was passed to JVM",
                        optionName, CommandLineOptionTest.prepareBooleanFlag(
                            SHAOptionsBase.USE_SHA_OPTION, true)),
                ExitCode.OK,
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(
                        SHAOptionsBase.USE_SHA_OPTION, true),
                CommandLineOptionTest.prepareBooleanFlag(optionName, false));

        if (!optionName.equals(SHAOptionsBase.USE_SHA_OPTION)) {
            // Verify that if -XX:-UseSHA is passed to the JVM, it is not possible
            // to enable the tested option and a warning is printed.
            CommandLineOptionTest.verifySameJVMStartup(
                    new String[] { SHAOptionsBase.getWarningForUnsupportedCPU(optionName) },
                    null,
                    shouldPassMessage,
                    String.format("Enabling option '%s' should not be possible and should result in a warning if %s was passed to JVM",
                                  optionName,
                                  CommandLineOptionTest.prepareBooleanFlag(SHAOptionsBase.USE_SHA_OPTION, false)),
                    ExitCode.OK,
                    SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                    CommandLineOptionTest.prepareBooleanFlag(SHAOptionsBase.USE_SHA_OPTION, false),
                    CommandLineOptionTest.prepareBooleanFlag(optionName, true));
        }
    }

    @Override
    protected void verifyOptionValues() throws Throwable {
        // Verify that "It should be able to disable option "

        CommandLineOptionTest.verifyOptionValueForSameVM(optionName, "true",
                String.format("Option '%s' should be enabled by default",
                        optionName),
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS);

        // Verify that it is possible to explicitly enable the option.
        CommandLineOptionTest.verifyOptionValueForSameVM(optionName, "true",
                String.format("Option '%s' was set to have value 'true'",
                        optionName),
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(optionName, true));

        // Verify that it is possible to explicitly disable the option.
        CommandLineOptionTest.verifyOptionValueForSameVM(optionName, "false",
                String.format("Option '%s' was set to have value 'false'",
                        optionName),
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(optionName, false));

        // verify that option is disabled when -UseSHA was passed to JVM.
        CommandLineOptionTest.verifyOptionValueForSameVM(optionName, "false",
                String.format("Option '%s' should have value 'false' when %s"
                        + " flag set to JVM", optionName,
                        CommandLineOptionTest.prepareBooleanFlag(
                            SHAOptionsBase.USE_SHA_OPTION, false)),
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(optionName, true),
                CommandLineOptionTest.prepareBooleanFlag(
                        SHAOptionsBase.USE_SHA_OPTION, false));

        // Verify that it is possible to explicitly disable the tested option
        // even if +UseSHA was passed to JVM.
        CommandLineOptionTest.verifyOptionValueForSameVM(optionName, "false",
                String.format("Option '%s' should have value 'false' if set so"
                        + " even if %s flag set to JVM", optionName,
                        CommandLineOptionTest.prepareBooleanFlag(
                            SHAOptionsBase.USE_SHA_OPTION, true)),
                SHAOptionsBase.UNLOCK_DIAGNOSTIC_VM_OPTIONS,
                CommandLineOptionTest.prepareBooleanFlag(
                        SHAOptionsBase.USE_SHA_OPTION, true),
                CommandLineOptionTest.prepareBooleanFlag(optionName, false));
    }
}
