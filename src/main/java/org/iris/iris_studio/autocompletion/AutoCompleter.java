package org.iris.iris_studio.autocompletion;

import org.fxmisc.richtext.CodeArea;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoCompleter {

    private static final int MAX_LENGTH = 50;
    private static final String DECLARATION_PATTERN = "\\b(?:(?:auto\\s*|const\\s*|unsigned\\s*|signed\\s*|register\\s*|volatile\\s*|static\\s*|void\\s*|short\\s*|long\\s*|char\\s*|int\\s*|float\\s*|double\\s*|_Bool\\s*|complex\\s*)+)(?:\\s+\\*?\\*?\\s*)([a-zA-Z_][a-zA-Z0-9_]*)\\s*[\\[;,=)]";

    public static Set<String> getVariables(CodeArea codeArea) {
        HashSet variables = new HashSet();

        Pattern pattern = Pattern.compile(DECLARATION_PATTERN);
        Matcher matcher = pattern.matcher(codeArea.getText());
        String query = getQuery(codeArea);
        System.out.println("QUERY " + query);
        while (matcher.find()) {
            if(matcher.group(1).startsWith(query))
                variables.add(matcher.group(1));
        }

        return variables;
    }

    private static String getQuery(CodeArea codeArea) {
        String query = codeArea.getText().substring(codeArea.getCaretPosition() - MAX_LENGTH, codeArea.getCaretPosition());
        query = query.replaceAll("\\p{Punct}", " ").trim();
        query = query.replaceAll("\\n", " ").trim();

        int last = query.lastIndexOf(" ");
        return query.substring(last + 1);
    }
}
