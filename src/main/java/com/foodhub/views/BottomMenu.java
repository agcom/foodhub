package com.foodhub.views;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BottomMenu {
	
	//TODO: add support for other items list changes (remove and ...)
	private final ObservableList<BottomMenuItem> items = FXCollections.observableArrayList();
	private final IntegerProperty selectedItemIndex = new SimpleIntegerProperty(this, "selectedItemIndex", 0);
	
	{
		
		selectedItemIndex.addListener((observable, oldValue, newValue) -> {
			
			if (oldValue.equals(newValue)) return;
			
			if (newValue.equals(-1) && !oldValue.equals(-1)) { //deselect all
				
				items.get(oldValue.intValue()).isGroupCall = true;
				items.get(oldValue.intValue()).setSelected(false);
				items.get(oldValue.intValue()).isGroupCall = false;
				
			} else {
				
				//deselect old
				if (!oldValue.equals(-1)) {
					
					items.get(oldValue.intValue()).isGroupCall = true;
					items.get(oldValue.intValue()).setSelected(false);
					items.get(oldValue.intValue()).isGroupCall = false;
					
				}
				
				//select new
				if (!newValue.equals(-1)) {
					
					items.get(newValue.intValue()).isGroupCall = true;
					items.get(newValue.intValue()).setSelected(true);
					if (items.get(newValue.intValue()).getOnAction() != null)
						items.get(newValue.intValue()).getOnAction().run();
					items.get(newValue.intValue()).isGroupCall = false;
					
				}
				
			}
			
		});
		
	}
	
	public BottomMenu() {
	}
	
	ObservableList<BottomMenuItem> getItems() {
		return items;
	}
	
	public int getSelectedItemIndex() {
		return selectedItemIndex.get();
	}
	
	public IntegerProperty selectedItemIndexProperty() {
		return selectedItemIndex;
	}
	
	public void setSelectedItemIndex(int selectedItemIndex) {
		this.selectedItemIndex.set(selectedItemIndex);
	}
}
