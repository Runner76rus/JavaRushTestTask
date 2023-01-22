package com.game.service;

import com.game.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class PlayerEmptyValidator implements Validator {


    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "400");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "400");
        ValidationUtils.rejectIfEmpty(errors, "race", "400");
        ValidationUtils.rejectIfEmpty(errors, "profession", "400");
        ValidationUtils.rejectIfEmpty(errors, "birthday", "400");
        PlayerValidator.checkValue(errors, player);
    }
}
