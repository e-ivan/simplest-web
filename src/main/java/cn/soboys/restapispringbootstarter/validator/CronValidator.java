package cn.soboys.restapispringbootstarter.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.scheduling.support.CronExpression;


/**
 * 校验是否为合法的 Cron表达式
 *
 * @author MrBird
 */
public class CronValidator implements ConstraintValidator<IsCron, String> {

    @Override
    public void initialize(IsCron isCron) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        try {
            return CronExpression.isValidExpression(value);
        } catch (Exception e) {
            return false;
        }
    }
}