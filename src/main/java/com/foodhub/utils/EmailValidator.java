package com.foodhub.utils;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

public class EmailValidator extends ValidatorBase {
	
	public EmailValidator(String message) {
		
		super(message);
		
	}
	
	public EmailValidator(String message, Node icon) {
		
		this(message);
		setIcon(icon);
		
	}
	
	@Override
	protected void eval() {
		
		hasErrors.set(!org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(((TextInputControl) srcControl.get()).getText()));
		
	}
	
}
