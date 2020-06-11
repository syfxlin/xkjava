package me.ixk.framework.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.Data;
import org.hibernate.validator.HibernateValidator;

public abstract class Validation {
    protected static Validator validator = javax
        .validation.Validation.byProvider(HibernateValidator.class)
        .configure()
        .failFast(false)
        .buildValidatorFactory()
        .getValidator();

    public static <T> Result validate(T obj, Class<?>... groups) {
        Result result = new Result();
        Set<ConstraintViolation<T>> violationSet = validator.validate(
            obj,
            groups
        );
        boolean hasError = violationSet != null && violationSet.size() > 0;
        if (hasError) {
            for (ConstraintViolation<T> violation : violationSet) {
                result.addError(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
                );
            }
        }
        return result;
    }

    public static <T> Result validate(T obj, String propertyName) {
        Result result = new Result();
        Set<ConstraintViolation<T>> violationSet = validator.validateProperty(
            obj,
            propertyName
        );
        boolean hasError = violationSet != null && violationSet.size() > 0;
        if (hasError) {
            for (ConstraintViolation<T> violation : violationSet) {
                result.addError(propertyName, violation.getMessage());
            }
        }
        return result;
    }

    @Data
    public static class Result {
        private List<ErrorMessage> errors;

        public Result() {
            this.errors = new ArrayList<>();
        }

        public Result(List<ErrorMessage> errors) {
            this.errors = errors;
        }

        public boolean isOk() {
            return errors.isEmpty();
        }

        public boolean isFail() {
            return !errors.isEmpty();
        }

        public List<ErrorMessage> getErrors() {
            return errors;
        }

        public void addError(String propertyName, String message) {
            this.errors.add(new ErrorMessage(propertyName, message));
        }
    }

    @Data
    public static class ErrorMessage {
        private String propertyPath;

        private String message;

        public ErrorMessage() {}

        public ErrorMessage(String propertyPath, String message) {
            this.propertyPath = propertyPath;
            this.message = message;
        }

        public String getPropertyPath() {
            return propertyPath;
        }

        public void setPropertyPath(String propertyPath) {
            this.propertyPath = propertyPath;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
