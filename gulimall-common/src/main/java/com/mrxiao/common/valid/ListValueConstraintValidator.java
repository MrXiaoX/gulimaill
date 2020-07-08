package com.mrxiao.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> set=new HashSet<>();

    @Override
    public void initialize(ListValue constraintValidator){
        int[] vals=constraintValidator.vals();
        if(vals.length>0)
        {
            for (int val : vals) {
                set.add(val);
            }
        }
    }


    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
