package org.iris.iris_studio.codeformatting;

import org.fxmisc.richtext.CodeArea;

public class AutoIndentator {
    private static final int MAX_LENGTH = 200;
    public static void indentate(CodeArea codeArea) {
        codeArea.insertText(codeArea.getCaretPosition(), " ".repeat(numberOfSpaces(codeArea)));
    }

    private static int numberOfSpaces(CodeArea codeArea) {
        int caretPos = codeArea.getCaretPosition();

        String temp;
        if(caretPos - MAX_LENGTH < 0) {
            temp = codeArea.getText().substring(0, caretPos - 1);
        } else {
            temp = codeArea.getText().substring(caretPos - MAX_LENGTH, caretPos - 1);
        }

        while(temp.contains("\n")) {
            temp = temp.substring(temp.indexOf("\n") + 1);
        }

        int nrOfSpaces = 0;
        for (int i = 0; i < temp.length(); i++) {
            if(temp.charAt(i) != ' ') {
                return nrOfSpaces;
            }
            nrOfSpaces++;
        }
        
        return nrOfSpaces;
    }
}
