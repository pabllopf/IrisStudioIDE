package org.iris.iris_studio.gui;

import javafx.event.Event;
import javafx.scene.control.Tab;

public abstract class View extends Tab {
	
	public View() {
		
	}
	
	public View(String title) {
		super(title);
	}
	
	public void select() {
		if(getTabPane() != null) {
			getTabPane().getSelectionModel().select(this);
		}
	}
	
	public void close() {        
        if(!isClosable() || getTabPane() == null) {
        	return;
        }
                        
        if(getOnCloseRequest() != null) {
        	getOnCloseRequest().handle( new Event(TAB_CLOSE_REQUEST_EVENT));
        }
        
        getTabPane().getTabs().remove(this);
        
        if(getOnClosed() != null) {
        	getOnClosed().handle( new Event(CLOSED_EVENT));
        } 
    }
	
}
