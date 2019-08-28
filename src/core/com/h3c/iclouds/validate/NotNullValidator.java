package com.h3c.iclouds.validate;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotNullValidator implements ConstraintValidator<NotNull, Object> {

	public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
		if (object != null) {
			if(object instanceof String) {
				if(object.toString().trim().length() == 0) {
					return false;
				}
			}
			return true;			
		}
		return false;
	}

	@Override
	public void initialize(NotNull constraintAnnotation) {
		// TODO Auto-generated method stub
		
	}


}
