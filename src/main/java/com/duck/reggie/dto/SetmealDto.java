package com.duck.reggie.dto;

import com.duck.reggie.entity.Setmeal;
import com.duck.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
