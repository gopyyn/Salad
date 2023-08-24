package com.gopyyn.salad.enums;

import com.gopyyn.salad.core.SaladCommands;

import java.util.function.Consumer;
import java.util.function.Function;

public enum ActionMapEnum {
    VISIT("visit", (parameters) -> "* visit \""+ parameters[0]+"\"", (parameters) -> SaladCommands.goTo(parameters[0])),
    ENTER("enter", (parameters) -> "* enter \""+ parameters[0]+"\" \""+parameters[1]+"\"", (parameters) -> SaladCommands.enter(parameters[0], parameters[1])),
    ENTER2("enter", (parameters) -> "* enter \""+ parameters[1]+"\" \""+parameters[0]+"\"", (parameters) -> SaladCommands.enter(parameters[1], parameters[0])),
    CLICK("click", (parameters) -> "* click \""+ parameters[0]+"\"", (parameters) -> SaladCommands.click(parameters[0])),
    WAIT("wait", (parameters) -> "* wait "+ Long.valueOf(parameters[0])+" "+TimeUnit.valueOf(parameters[1]), (parameters) -> SaladCommands.wait(Long.valueOf(parameters[0]), TimeUnit.valueOf(parameters[1]))),
    SET("set", (parameters) -> "* set "+ parameters[0]+ " = \"" +parameters[1] + "\"", (parameters) -> SaladCommands.set(parameters[0], parameters[1])),
    DEF("def", (parameters) -> "* def "+ parameters[0]+ " = \"" +parameters[1] + "\"", (parameters) -> SaladCommands.def(parameters[0], parameters[1])),
    PRINT("print", (parameters) -> "* print "+ parameters[0], (parameters) -> SaladCommands.print(parameters[0])),
    VERIFY("verify", (parameters) -> {
        if (parameters.length == 2) {
            return "* verify \"" + parameters[0]+"\" is "+VerifyType.fromValue(parameters[1]);
        } else {
            return "* verify \"" +parameters[0]+"\" "+ parameters[1].toUpperCase()+" \""+parameters[2] +"\"";
        }
    }, (parameters) -> {
        if (parameters.length == 2) {
            SaladCommands.verifyElement(parameters[0], VerifyType.fromValue(parameters[1]));
        } else {
            SaladCommands.verify(parameters[0], parameters[1], parameters[2]);
        }
    });

    private String action;
    private Function<String[], String> saladCommand;
    private Consumer<String[]> saladExecute;

    ActionMapEnum(String action, Function<String[], String> saladCommand, Consumer<String[]> saladExecute) {
        this.action = action;
        this.saladCommand = saladCommand;
        this.saladExecute = saladExecute;
    }

    public String getSaladCommand(String[] parameters) {
        return saladCommand.apply(parameters);
    }

    public void executeCommand(String[] parameters) {
         saladExecute.accept(parameters);
    }
}
