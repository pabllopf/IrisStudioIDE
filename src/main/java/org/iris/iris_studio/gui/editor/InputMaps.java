package org.iris.iris_studio.gui.editor;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.NavigationActions.SelectionPolicy;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;

public final class InputMaps {
	
	private InputMaps() {}
	
	public static void setDeleteLine(CodeArea codeArea) {
		Nodes.addInputMap(codeArea,InputMap.consume(
				EventPattern.keyPressed(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN)),
				e -> {
					codeArea.lineStart(SelectionPolicy.CLEAR);
					int start = Math.max(codeArea.getCaretPosition() - 1, 0);
					codeArea.lineEnd(SelectionPolicy.CLEAR);
					int end = codeArea.getCaretPosition();
					codeArea.deleteText(start, end);
				}));
	}
	
	public static void setTabReplace(CodeArea codeArea, String tab) {
		Nodes.addInputMap(codeArea, InputMap.consume(
			    EventPattern.keyPressed(KeyCode.TAB), 
			    e -> codeArea.replaceSelection(tab)
			));
	}
	
	public static void setSuggestions(CodeArea codeArea) {
		// TODO
		Nodes.addInputMap(codeArea, InputMap.consume(
				EventPattern.keyPressed(new KeyCodeCombination(KeyCode.ENTER, KeyCombination.ALT_DOWN)),
				e -> System.out.println("ALT + ENTER")));
	}
	
	public static void setZoom(CodeArea codeArea) {
		setZoomIn(codeArea);
		setZoomOut(codeArea);
	}
	
	public static void setZoomIn(CodeArea codeArea) {
		Nodes.addInputMap(codeArea, InputMap.consume(
				EventPattern.keyPressed(
						new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN)),
				e -> zoom(1.0)));
	}
	
	public static void setZoomOut(CodeArea codeArea) {
		Nodes.addInputMap(codeArea, InputMap.consume(
				EventPattern.keyPressed(
						new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN)),
				e -> zoom(-1.0)));
	}
	
	private static void zoom(double amount) {
		Font font = FileViews.getFont();
		double size = clamp(font.getSize() + amount, 8.0, 96.0);
		if(font.getSize() != size) {
			FileViews.setFont(font.getFamily(), size);
		}
	}

	private static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}

}
