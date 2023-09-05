package ru.trueengineering.feature.flag.starter.extractor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Set;

/**
 * @author m.yastrebov
 */
public class BeanValidationDeserializer extends BeanDeserializer {

    private final Validator validator;

    public BeanValidationDeserializer(BeanDeserializerBase src) {
        super(src);
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final Object instance = super.deserialize(p, ctxt);
        validate(instance);
        return instance;
    }

    private void validate(Object instance) throws IOException {
        Set<ConstraintViolation<Object>> violations = validator.validate(instance);
        if (violations.size() > 0) {
            StringBuilder msg = new StringBuilder();
            msg.append("JSON object is not valid. Reasons (").append(violations.size()).append("): ");
            for (ConstraintViolation<Object> violation : violations) {
                msg.append(violation.getMessage()).append(", ");
            }
            throw new IOException(new ConstraintViolationException(msg.toString(), violations));
        }
    }
}
