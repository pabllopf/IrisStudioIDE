package org.iris.iris_studio.gui.editor.autocompletion;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;
import org.iris.iris_studio.autocompletion.AutoCompleter;

import java.util.List;
import java.util.Set;

public class AutoCompletionPopup extends Popup {
    private ListView autoCompletionListView;
    private CodeArea codeArea;
    private static AutoCompletionPopup instance;

    private final EventHandler<KeyEvent> onKeyPressed = event -> {
        switch(event.getCode()) {
            case ENTER:
            case TAB:
                completeText();
                break;
            case UP:
            case DOWN:
                break;
            default:
                hide();
        }
    };

    private final EventHandler<MouseEvent> onMouseClicked = clickEvent -> {
        completeText();
    };

    private void completeText() {
        String toBeInserted = autoCompletionListView.getSelectionModel().getSelectedItem()
                                                    .toString()
                                                    .substring(AutoCompleter.getQuery(codeArea).length());
        
        codeArea.insertText(codeArea.getCaretPosition(), toBeInserted);
        hide();
    }

    private AutoCompletionPopup(CodeArea codeArea) {
        super();
        this.codeArea = codeArea;
        initListView();
        fill();
    }

    private void initListView() {
        autoCompletionListView = new ListView();
        autoCompletionListView.setOnKeyPressed(onKeyPressed);
        autoCompletionListView.setOnMouseClicked(onMouseClicked);
        getContent().add(autoCompletionListView);
    }

    private void fill() {
        clear();
        autoCompletionListView.getItems().addAll(AutoCompleter.getVariables(codeArea));

    }

    public static AutoCompletionPopup get(CodeArea codeArea) {
        if(instance == null) {
            instance = new AutoCompletionPopup(codeArea);
        } else {
            instance.setCodeArea(codeArea);
        }

        return instance;
    }

    private void setCodeArea(CodeArea codeArea) {
        this.codeArea = codeArea;
    }

    public void clear() {
        this.autoCompletionListView.getItems().clear();
    }

    public int size() {
        return this.autoCompletionListView.getItems().size();
    }

    public void refresh() {
        fill();
    }

    @Override
    public void show(Node node, double x, double y) {
        autoCompletionListView.setPrefHeight(24 * size() + 2);
        fill();
        super.show(node, x, y);
    }
}
