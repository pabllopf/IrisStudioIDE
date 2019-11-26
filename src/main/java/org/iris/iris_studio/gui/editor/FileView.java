package org.iris.iris_studio.gui.editor;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.iris.iris_studio.IrisStudio;
import org.iris.iris_studio.autocompletion.AutoCompleter;
import org.iris.iris_studio.codeformatting.AutoIndenter;
import org.iris.iris_studio.gui.Multiple;
import org.iris.iris_studio.gui.View;
import org.iris.iris_studio.gui.editor.autocompletion.AutoCompletionPopup;
import org.iris.iris_studio.gui.util.Icons;
import org.iris.iris_studio.gui.util.Theme;
import org.iris.iris_studio.projects.ProjectFile;
import org.iris.iris_studio.util.FileUtils;

import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Multiple
public class FileView extends View {

	private static final String[] KEYWORDS = new String[] {
			"namespace", "assert", "boolean", "break",
			"case", "catch", "char", "class", "const",
			"continue", "default", "do", "double", "else",
			"enum", "extends", "final", "finally", "float",
			"for", "goto", "if", "implements", "unsigned",
			"instanceof", "int", "interface", "long", "native",
			"new", "package", "private", "protected", "public",
			"return", "short", "static", "template", "struct",
			"switch", "sizeof", "this", "throw", "throws",
			"transient", "try", "void", "typedef", "while"
	};

	
	private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
	private static final String FUNCTION_PATTERN = "\\w+\\(";
	private static final String PAREN_PATTERN = "\\(|\\)";
	private static final String BRACE_PATTERN = "\\{|\\}";
	private static final String BRACKET_PATTERN = "\\[|\\]";
	private static final String PREPROCESSOR_PATTERN = "(#define|#include|#if|#ifndef|#endif|#else|elif)\\b";
	private static final String TYPE_PATTERN = "\\b(my_struct|vector|cout)\\b";
	private static final String SEMICOLON_PATTERN = "\\;";
	private static final String NAMESPACE_PATTERN = "\\b(sin|cos|printf|malloc|free|puts|main)\\b";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
	private static final String NUMBER_PATTERN = "\\d+";

	private static final Pattern PATTERN = Pattern.compile(
					  "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
					+ "|(?<FUNCTION>" + FUNCTION_PATTERN + ")"
					+ "|(?<PAREN>" + PAREN_PATTERN + ")"
					+ "|(?<BRACE>" + BRACE_PATTERN + ")"
					+ "|(?<BRACKET>" + BRACKET_PATTERN + ")"
					+ "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
					+ "|(?<STRING>" + STRING_PATTERN + ")"
					+ "|(?<COMMENT>" + COMMENT_PATTERN + ")"
					+ "|(?<PREPROCESSOR>" + PREPROCESSOR_PATTERN + ")"
					+ "|(?<USERDEFINED>" + TYPE_PATTERN + ")"
					+ "|(?<NAMESPACE>" + NAMESPACE_PATTERN + ")"
					+ "|(?<NUMBER>" + NUMBER_PATTERN + ")"
			);

	private static final String TAB = "    ";
	
	private final CodeArea codeArea = new CodeArea();
	private final ProjectFile file;
	private final ExecutorService codeInspector;
	private AutoCompletionPopup autoCompletePopup;
	private boolean modified;
	private boolean ignoreTextChanges;

	private final EventHandler<KeyEvent> onKeyReleased = event -> {
		if (autoCompletePopup == null) {
			autoCompletePopup = AutoCompletionPopup.get(codeArea);
		} else {
			autoCompletePopup.hide();
		}
        switch(event.getCode()) {
            case ENTER:
                AutoIndenter.indent(codeArea);
                break;
            case TAB:
                switch(AutoCompleter.getQuery(codeArea)) {
                    case "fori":
                        AutoCompleter.insertFori(codeArea);
                        break;
                    case "cout":
                        AutoCompleter.insertCout(codeArea);
                }
        }

		if (event.getCode().isLetterKey()) {
			autoCompletePopup.refresh();
			if (autoCompletePopup.size() != 0) {
				Bounds pointer = codeArea.caretBoundsProperty().getValue().get();
				autoCompletePopup.show(codeArea, pointer.getMaxX(), pointer.getMinY());
			}
		} else {
			autoCompletePopup.hide();
		}
	};

	FileView(ProjectFile file) {
		super(file.getName());

		codeArea.setId("codearea");
		codeArea.setOnKeyReleased(onKeyReleased);
		FileViews.applyFont(this);

		this.file = file;
		setGraphic();
						
		setInputMaps();
		
		codeArea.setParagraphGraphicFactory(new LineNumbers(codeArea));
		
		codeInspector = Executors.newSingleThreadExecutor();
		
		setSyntaxColoring();
		
		setContentsOf(file);
		codeArea.textProperty().addListener(this::onTextChanged);
		
		setTooltip(new Tooltip(file.getPath().toString()));
		
		setContent(new VirtualizedScrollPane<>(codeArea));
		
		setOnCloseRequest(this::onCloseRequest);
		
		setOnClosed(e -> shutdown());
	}
	
	public CodeArea getCodeArea() {
		return codeArea;
	}
	
	public boolean isModified() {
		return modified;
	}
	
	public void save() {
		if(!modified) {
			return;
		}
		
		file.write(codeArea.getText());
		
		setModified(false);
	}
	
	public ProjectFile getFile() {
		return file;
	}
	
	private void setModified(boolean modified) {
		if(this.modified != modified) {
			this.modified = modified;
			if(modified) {
				setText('*' + getText());			
			} else {
				setText(file.getName());
			}
		}
	}
	
	public void shutdown() {
		codeInspector.shutdown();
	}

	private void setGraphic() {
		
		Image icon = null;
		
		switch(file.getType()) {
		case HEADER:
			icon = Icons.get(Icons.ICON_HEADER_FILE);
			break;
		case RESOURCE:
			icon = Icons.get(Icons.ICON_TEXT_FILE);
			break;
		case SOURCE:
			icon = Icons.get(Icons.ICON_SOURCE_FILE);
			break;
		default:
			break;
		}

		setGraphic(new ImageView(icon));
	}
	
	private void setContentsOf(ProjectFile file) {
		String contents = FileUtils.readAllText(file.getPath());
		codeArea.replaceText(0, 0, contents.replaceAll("\t", TAB));
	}
	
	private void onCloseRequest(Event event) {
		if(isModified()) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			Theme.setTheme(alert.getDialogPane().getScene(), IrisStudio.getTheme());
			alert.setTitle("Close file view of " + file.getName());
			alert.setHeaderText("There are unsaved changes");
			alert.setContentText("Do you want to save the last changes?");
			alert.getButtonTypes().clear();
			alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
			
			alert.showAndWait().ifPresent(button -> {
				if(button == ButtonType.YES) {
					save();
				} else if(button == ButtonType.CANCEL) {
					event.consume();
				}
			});
		} 
	}
	
	private void setInputMaps() {
		InputMaps.setTabReplace(codeArea, TAB);
		InputMaps.setDeleteLine(codeArea);
		InputMaps.setSuggestions(codeArea);
		InputMaps.setZoom(codeArea);
	}
	
	private void onTextChanged(Object observable, String oldText, String newText) {
		if(ignoreTextChanges) {
			ignoreTextChanges = false;
			return;
		}
		
		ignoreTextChanges = true;
		
		int caretPos = Math.max(codeArea.getCaretPosition() - 1, 0);
		
		if(oldText.length() < newText.length()) {
			onTextInserted(caretPos, oldText, newText);
		} else if(oldText.length() > newText.length()) {
			onTextRemoved(caretPos, oldText, newText);
		} else {
			onTextReplaced(caretPos, oldText, newText);
		}
		
		ignoreTextChanges = false;
		
		setModified(true);
	}

	private void onTextInserted(int caretPos, String oldText, String newText) {
		if(manageOpeningCouples(caretPos, newText)) {
			return;
		} else if(manageClosingCouples(caretPos, newText)) {
			return;
		}
	}
	
	private boolean manageClosingCouples(int caretPos, String text) {
		char c = text.charAt(caretPos);
		
		switch(c) {
		case ')':
		case ']':
		case '\'':
		case '"':
			codeArea.deleteNextChar();
			codeArea.moveTo(caretPos+1);
			break;
		default:
			return false;
		}
		
		return true;
	}

	private boolean manageOpeningCouples(int caretPos, String text) {
		char c = text.charAt(caretPos);
				
		switch(c) {
		case '(':
			codeArea.replaceText(caretPos, caretPos+1, "()");
			break;
		case '[':
			codeArea.replaceText(caretPos, caretPos+1, "[]");
			break;
		case '\'':
			codeArea.replaceText(caretPos, caretPos+1, "''");
			break;
		case '"':
			codeArea.replaceText(caretPos, caretPos+1, "\"\"");
			break;
		case '*':
			if(text.charAt(Math.max(caretPos-1, 0)) == '/') {
				codeArea.replaceText(caretPos-1, caretPos+1, "/**/");
			}
			break;
		default:
			return false;
		}
		
		return true;
	}

	private void onTextRemoved(int caretPos, String oldText, String newText) {

	}

	private void onTextReplaced(int caretPos, String oldText, String newText) {
		
	}

	private void setSyntaxColoring() {
		codeArea.multiPlainChanges()
        .successionEnds(Duration.ofMillis(500))
        .supplyTask(this::computeHighlightingAsync)
        .awaitLatest(codeArea.multiPlainChanges())
        .filterMap(t -> {
            if(t.isSuccess()) {
                return Optional.of(t.get());
            } else {
                t.getFailure().printStackTrace();
                return Optional.empty();
            }
        })
        .subscribe(this::applyHighlighting);
	}
	
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        codeInspector.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
        	
            String styleClass = getStyleClass(matcher);
            
            lastKwEnd = processStyle(styleClass, lastKwEnd, matcher, spansBuilder);
            
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

	private static int processStyle(String styleClass, int lastKwEnd, Matcher matcher, StyleSpansBuilder<Collection<String>> spansBuilder) {
		
		int end = Objects.equals(styleClass, "function") ? Math.max(matcher.end() - 1, 0) : matcher.end();
		
		spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
        
        spansBuilder.add(Collections.singleton(styleClass), end - matcher.start());
        
        return end;
	}

	private static String getStyleClass(Matcher matcher) {
		return matcher.group("KEYWORD") != null ? "keyword" :
        matcher.group("PREPROCESSOR") != null ? "preprocessor" :
        matcher.group("FUNCTION") != null ? "function" :
        matcher.group("PAREN") != null ? "paren" :
        matcher.group("BRACE") != null ? "brace" :
        matcher.group("BRACKET") != null ? "bracket" :
        matcher.group("SEMICOLON") != null ? "semicolon" :
        matcher.group("STRING") != null ? "string" :
        matcher.group("COMMENT") != null ? "comment" :
        matcher.group("NUMBER") != null ? "number" :
        null;
	}
	
}
