/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service.config;

/**
 *
 * @author Nik
 */
public class ConfigSettings {
    
    boolean AskDialog = true;
    boolean ExitOperation = false;

    public ConfigSettings(boolean AskDialog,boolean ExitOperation) {
        this.AskDialog = AskDialog;
        this.ExitOperation = ExitOperation;
    }

    public boolean isAskDialog() {
        return AskDialog;
    }

    public boolean isExitOperation() {
        return ExitOperation;
    }

    public void setAskDialog(boolean AskDialog) {
        this.AskDialog = AskDialog;
    }

    public void setExitOperation(boolean ExitOperation) {
        this.ExitOperation = ExitOperation;
    }

    @Override
    public String toString() {
        String xml = "<Settings AskExitDialog = " + 
                ((this.AskDialog) ? "\"true\"":"\"false\"") +
                " ExitOperation = " + ((this.ExitOperation) ? "\"true\"" : "\"false\"")
                +">\r\n</Settings>";
        return xml;
    }

}
