package org.iris.iris_studio.gui;

import org.iris.iris_studio.gui.util.Buttons;
import org.iris.iris_studio.gui.util.Icons;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class MemoryInfo extends HBox {
	
	private final ProgressBar memoryBar;
	private final Text memoryText;
	private final Button gcButton;
	private final Animation memoryChecker;
	
	public MemoryInfo() {
		memoryBar = new ProgressBar();
		memoryBar.setMinHeight(26);
		memoryBar.setMinWidth(200);
		memoryBar.setTooltip(new Tooltip(""));
		
		memoryText = new Text("");
		memoryText.setFont(Font.font("Consolas", 12));
		memoryText.setFill(Color.WHITESMOKE);
		
		gcButton = Buttons.create(Icons.get(Icons.ICON_GARBAGE), false, e -> System.gc());
		gcButton.setTooltip(new Tooltip("Run garbage collector"));
		
		memoryChecker = new Timeline(new KeyFrame(Duration.seconds(0.4), e -> checkMemoryUsage()));
		memoryChecker.setCycleCount(-1);
		memoryChecker.play();
		
		StackPane stackPane = new StackPane();
		stackPane.getChildren().addAll(memoryBar, memoryText);

		getChildren().addAll(stackPane, gcButton);
	}
	
	public double getFreeMemory() {
		return bytesToMB(Runtime.getRuntime().freeMemory());
	}
	
	public double getTotalMemory() {
		return bytesToMB(Runtime.getRuntime().totalMemory());
	}
	
	public double getMaxMemory() {
		return bytesToMB(Runtime.getRuntime().maxMemory());
	}
	
	public double getUsedMemory() {
		return getTotalMemory() - getFreeMemory();
	}
	
	private double bytesToMB(long bytes) {
		return bytes / 1024.0 / 1024.0;
	}

	private void checkMemoryUsage() {
		double used = getUsedMemory();
		double total = getTotalMemory();
		double percentage = used * 100.0 / total;
		
		Platform.runLater(() -> {
			memoryText.setText(String.format("%.2fMB of %dMB (%.2f%%)", used, (int)total, percentage));
			memoryBar.setProgress(used / total);
			memoryBar.getTooltip().setText(String.format("Using %.2fMB of %.2fMB total Heap memory (%.2f%%)", used, total, percentage));
		});
	}
	
}
