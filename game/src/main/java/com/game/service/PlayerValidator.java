package com.game.service;

import com.game.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class PlayerValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Player player = (Player) target;
        checkValue(errors, player);
    }

    public static void checkValue(Errors errors, Player player) {
        Date minDate = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();
        Date maxDate = new GregorianCalendar(3000, Calendar.DECEMBER, 31).getTime();
        if (player.getName() != null && player.getName().length() > 12) errors.rejectValue(
                "name", "400", "Name cannot exceed 12 characters");
        if (player.getTitle() != null && player.getTitle().length() > 30) errors.rejectValue(
                "title", "400", "Title cannot exceed 30 characters");
        if (player.getBirthday() != null && (player.getBirthday().before(minDate) || player.getBirthday().after(maxDate)))
            errors.rejectValue(
                    "birthday", "400", "Birthday must be at least 2000 and not more than 3000");
        if (player.getExperience() != null && (player.getExperience() < 0 || player.getExperience() > 10000000))
            errors.rejectValue(
                    "experience", "400", "Experience must be at least 0 and not more than 10,000,000");
    }
}
