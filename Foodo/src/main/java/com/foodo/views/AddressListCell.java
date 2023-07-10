package com.foodo.views;

import com.foodo.models.Address;
import com.jfoenix.controls.JFXListCell;

public class AddressListCell extends JFXListCell<Address> {
	
	public static final Address nothing = new Address("nothing"), loading = new Address("loading");
	
	@Override
	protected void updateItem(Address item, boolean empty) {
		super.updateItem(item, empty);
		
		if (item != null) {
			
			if (item.equals(loading)) {
				
				setGraphic(null);
				setText("Wait a moment...");
				
			} else if (item.equals(nothing)) {
				
				setText("Ops! Still not covered");
				setGraphic(null);
				
			} else {
				
				setText(item.getAddress());
				setGraphic(null);
				
			}
			
		} else {
			
			setText(null);
			setGraphic(null);
			setManaged(false);
			
		}
		
	}
	
}
