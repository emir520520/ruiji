package ca.fangyux.dto;

import ca.fangyux.entity.Setmeal;
import ca.fangyux.entity.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
