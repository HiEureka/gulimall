package com.atguigu.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {
    //可以再定义一个类，用来校验double类型的
//public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Double> {}
    private Set<Integer> set = new HashSet<>();
    //初始化方法
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] vals = constraintAnnotation.vals();//取出我们规定的值
        for (int val : vals) {
            set.add(val);//把我们规定填写的值全部取出，放进set集合
        }

    }

    //判断是否校验成功
    /**
     *
     * @param value 需要校验的值，传过来的
     * @param context
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        return set.contains(value);//判断set集合里面是否包含校验的值，包含说明传入的值是符合要求的，反之不符合
    }
}
