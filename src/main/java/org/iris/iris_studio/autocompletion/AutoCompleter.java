package org.iris.iris_studio.autocompletion;

import org.fxmisc.richtext.Caret;
import org.fxmisc.richtext.CodeArea;
import org.iris.iris_studio.codeformatting.AutoIndenter;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCompleter {

    private static final int MAX_LENGTH = 50;
    private static final Pattern DECLARATION_PATTERN = Pattern.compile("\\b(?:(?:auto\\s*|const\\s*|unsigned\\s*|signed\\s*|register\\s*|volatile\\s*|static\\s*|void\\s*|short\\s*|long\\s*|char\\s*|int\\s*|float\\s*|double\\s*|_Bool\\s*|complex\\s*)+)(?:\\s+\\*?\\*?\\s*)([a-zA-Z_][a-zA-Z0-9_]*)\\s*[\\[;,=)(]");
    public static Set<String> getVariables(CodeArea codeArea) {
        HashSet variables = new HashSet();

        Matcher matcher = DECLARATION_PATTERN.matcher(codeArea.getText());
        String query = getQuery(codeArea);

        while (matcher.find()) {
            if(matcher.group(1).startsWith(query))
                variables.add(matcher.group(1));
        }

        return variables;
    }

    public static String getQuery(CodeArea codeArea) {
        String query;
        if(codeArea.getCaretPosition() - MAX_LENGTH < 0) {
            query = codeArea.getText().substring(0, codeArea.getCaretPosition());
        } else {
            query = codeArea.getText().substring(codeArea.getCaretPosition() - MAX_LENGTH, codeArea.getCaretPosition());
        }

        query = query.replaceAll("\\p{Punct}", " ").trim();
        query = query.replaceAll("\\n", " ").trim();

        int last = query.lastIndexOf(" ");
        return query.substring(last + 1);
    }

    public static void insertFori(CodeArea codeArea) {
        StringBuilder foriText = new StringBuilder();
        int nrOfSpaces = AutoIndenter.numberOfSpaces(codeArea);
        String indentation = " ".repeat(nrOfSpaces);

        foriText.append(indentation).append("for (int i = 0; i < ; i++)\n");
        foriText.append(indentation).append("{\n");
        foriText.append(indentation).append("\n");
        foriText.append(indentation).append("}");

        int caretPos = codeArea.getCaretPosition();

        codeArea.replaceText(codeArea.getCaretPosition() - 8 - nrOfSpaces, codeArea.getCaretPosition(), foriText.toString());
        codeArea.moveTo(caretPos + 12);
    }

    public static void insertCout(CodeArea codeArea) {
        String coutText = " ".repeat(AutoIndenter.numberOfSpaces(codeArea)) + "std::cout << \"\" << std::endl;";

        int caretPos = codeArea.getCaretPosition();

        codeArea.replaceText(codeArea.getCaretPosition() - 8 - AutoIndenter.numberOfSpaces(codeArea), codeArea.getCaretPosition(), coutText);
        codeArea.moveTo(caretPos + 6);
    }
}
