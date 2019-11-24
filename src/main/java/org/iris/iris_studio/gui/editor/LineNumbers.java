package org.iris.iris_studio.gui.editor;

import java.util.function.IntFunction;

import org.fxmisc.richtext.GenericStyledArea;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class LineNumbers implements IntFunction<Node> {
	
	private final Val<Integer> nParagraphs;
	
	public LineNumbers(GenericStyledArea<?, ?, ?> area) {
		 nParagraphs = LiveList.sizeOf(area.getParagraphs());
	}

    @Override
    public Node apply(int index) {
        Val<String> formatted = nParagraphs.map(n -> format(index + 1, n));

        Label lineNumber = new Label();
        lineNumber.setId("linenumbers");
        lineNumber.setAlignment(Pos.TOP_RIGHT);
        lineNumber.setFocusTraversable(false);

        lineNumber.textProperty().bind(formatted.conditionOnShowing(lineNumber));

        return lineNumber;
    }

    private String format(int x, int max) {
    	int xDigits = digitsCount(x);
    	int maxDigits = digitsCount(max);
    	    	    	
    	String text = String.valueOf(x);
    	
    	if(xDigits < maxDigits) {
    		int padding = maxDigits - xDigits;
    		return addPaddingLeft(text, padding);
    	}
    	
    	return text;
    }
    
    private String addPaddingLeft(String str, int paddingCount) {
    	if(paddingCount == 1) {
    		return ' ' + str;
    	}
    	
    	StringBuilder builder = new StringBuilder();
    	for(int i = 0;i < paddingCount;i++) {
    		builder.append(' ');
    	}
    	builder.append(str);
    	
    	return builder.toString();
    }
    
    private int digitsCount(int number) {
    	return (int)(Math.log10(number) + 1);
    }

}
