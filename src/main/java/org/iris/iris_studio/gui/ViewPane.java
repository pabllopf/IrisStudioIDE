package org.iris.iris_studio.gui;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class ViewPane extends TabPane {

	public <T extends View> T addOrSelect(Class<T> clazz, Object... args) {
		
		Optional<T> tabOfSameClass = findOfClass(clazz);

		if(tabOfSameClass.isPresent()) {
			if(clazz.getAnnotation(Multiple.class) == null) {
				select(clazz);
				return tabOfSameClass.get();
			}
		}

		T view = instanciate(clazz, args);
		Objects.requireNonNull(view);
		
		getTabs().add(view);
		select(view);
		
		return view;
	}
	
	public void select(int index) {
		getSelectionModel().select(index);
	}
	
	public void select(View view) {
		getSelectionModel().select(view);
	}

	public void selectIf(Predicate<? super Tab> condition) {
		filter(condition).findFirst().ifPresent(getSelectionModel()::select);
	}

	public void select(Class<? extends Tab> clazz) {
		filter(t -> ofClass(t, clazz)).findFirst().ifPresent(getSelectionModel()::select);
	}

	@SuppressWarnings("unchecked")
	public <T extends View> Optional<T> getSelected(Class<T> clazz) {
		Optional<View> selected = getSelected();
		if(selected.isPresent() && !ofClass(selected.get(), clazz)) {
			return Optional.empty();
		}
		return (Optional<T>) selected;
	}

	public Optional<View> getSelected() {
		return Optional.ofNullable((View) getSelectionModel().getSelectedItem());
	}

	@SuppressWarnings("unchecked")
	public <T extends Tab> List<T> findAllOfClass(Class<T> clazz) {
		return (List<T>) filter(t -> ofClass(t, clazz)).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Tab> Optional<T> findOfClass(Class<T> clazz) {
		return (Optional<T>) filter(t -> ofClass(t, clazz)).findFirst();
	}
	
	private <T extends View> T instanciate(Class<T> clazz, Object... args) {
		try {
			Constructor<T> ctor = clazz.getDeclaredConstructor(getClassesOf(args));
			ctor.setAccessible(true);
			return ctor.newInstance(args);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Class<?>[] getClassesOf(Object[] args) {
		Class<?>[] classes = new Class<?>[args.length];
		for(int i = 0;i < args.length;i++) {
			Object arg = args[i];
			classes[i] = arg == null ? null : arg.getClass();
		}
		return classes;
	}
	
	private boolean ofClass(Object obj, Class<?> clazz) {
		return obj.getClass().equals(clazz);
	}

	private Stream<Tab> filter(Predicate<? super Tab> predicate) {
		return stream().filter(predicate);
	}

	private Stream<Tab> stream() {
		return getTabs().stream();
	}

}
